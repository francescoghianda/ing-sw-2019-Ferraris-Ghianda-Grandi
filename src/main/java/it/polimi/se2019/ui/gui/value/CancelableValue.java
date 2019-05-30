package it.polimi.se2019.ui.gui.value;

public class CancelableValue<T> extends ObservableValue<T>
{
    private final Value<T> value;

    private boolean canceled;

    public CancelableValue()
    {
        value = new Value<>();
    }

    @Override
    void addObserver(ValueObserver<T> valueObserver)
    {
        value.addObserver(valueObserver);
    }

    public void cancel()
    {
        canceled = true;
    }

    boolean isCanceled()
    {
        return canceled;
    }

    void reset()
    {
        canceled = false;
    }

    @Override
    public void set(T value)
    {
        this.value.set(value);
    }

    @Override
    public T get()
    {
        return value.get();
    }
}
