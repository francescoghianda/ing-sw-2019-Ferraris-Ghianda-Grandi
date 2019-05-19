package it.polimi.se2019.ui.gui;

public class ValueObserver<R>
{
    private final Object lock;
    private boolean wait;

    public ValueObserver()
    {
        this.lock = new Object();
    }

    R getValue(ObservableValue<R> observableValue)
    {
        synchronized (lock)
        {
            try
            {
                observableValue.addObserver(this);
                wait = true;
                while (wait)lock.wait();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        return observableValue.getValue();
    }

    public void notifyObserver()
    {
        synchronized (lock)
        {
            wait = false;
            lock.notifyAll();
        }
    }

}
