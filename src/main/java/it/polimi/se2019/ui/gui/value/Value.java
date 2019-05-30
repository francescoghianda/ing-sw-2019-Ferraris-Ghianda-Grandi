package it.polimi.se2019.ui.gui.value;

import java.util.ArrayList;
import java.util.List;

public final class Value<T> extends ObservableValue<T>
{
    private T value;

    private List<ValueObserver<T>> valueObservers;

    public Value()
    {
        valueObservers = new ArrayList<>();
    }

    @Override
    void addObserver(ValueObserver<T> valueObserver)
    {
        valueObservers.add(valueObserver);
    }

    @Override
    public void set(T value)
    {
        this.value = value;
        valueObservers.forEach(ValueObserver::notifyObserver);
        valueObservers.clear();
    }

    @Override
    public T get()
    {
        return value;
    }
}
