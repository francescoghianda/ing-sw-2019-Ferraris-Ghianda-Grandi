package it.polimi.se2019.ui.gui.value;

public abstract class ObservableValue<T>
{
    abstract void addObserver(ValueObserver<T> valueObserver);
    public abstract void set(T value);
    public abstract T get();
}
