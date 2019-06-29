package it.polimi.se2019.ui.gui.value;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.TimeOutException;
import it.polimi.se2019.ui.gui.MatchScene;
import it.polimi.se2019.ui.gui.SceneManager;
import it.polimi.se2019.utils.logging.Logger;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public final class ValueObserver<R>
{
    private static List<ValueObserver> activeObserverList = new ArrayList<>();

    private final Object lock;
    private boolean wait;

    private Thread waitingThread;

    public ValueObserver()
    {
        this.lock = new Object();
    }

    private ObservableValue<R> wait(ObservableValue<R> value) throws TimeOutException
    {
        synchronized (lock)
        {
            activeObserverList.add(this);
            try
            {
                value.addObserver(this);
                wait = true;
                waitingThread = Thread.currentThread();
                while (wait)lock.wait();
            }
            catch (InterruptedException e)
            {
                Logger.exception(e);
                Thread.currentThread().interrupt();
                throw new TimeOutException();
            }
            finally
            {
                activeObserverList.remove(this);
            }
        }

        return value;
    }

    public R get(Value<R> value)
    {
        return wait(value).get();
    }

    public R get(CancelableValue<R> value) throws CanceledActionException
    {
        Platform.runLater(() -> MatchScene.getInstance().addCancelableOption(value));
        wait(value);
        if(value.isCanceled())
        {
            value.reset();
            MatchScene.getInstance().onActionCanceled();
            throw new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER);
        }
        return value.get();
    }

    public static void timeout()
    {
        activeObserverList.forEach(ValueObserver::interrupt);
    }

    private void interrupt()
    {
        if(waitingThread != null)waitingThread.interrupt();
    }

    void notifyObserver()
    {
        synchronized (lock)
        {
            wait = false;
            lock.notifyAll();
        }
    }

}
