package it.polimi.se2019.ui.gui.value;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.utils.logging.Logger;

public final class ValueObserver<R>
{
    private final Object lock;
    private boolean wait;

    public ValueObserver()
    {
        this.lock = new Object();
    }

    private ObservableValue<R> wait(ObservableValue<R> value)
    {
        synchronized (lock)
        {
            try
            {
                value.addObserver(this);
                wait = true;
                while (wait)lock.wait();
            }
            catch (InterruptedException e)
            {
                Logger.exception(e);
                Thread.currentThread().interrupt();
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
        wait(value);
        if(value.isCanceled())
        {
            value.reset();
            throw new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER);
        }
        return value.get();
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
