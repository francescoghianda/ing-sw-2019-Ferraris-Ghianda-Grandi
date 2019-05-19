package it.polimi.se2019.ui.gui;

import java.util.ArrayList;
import java.util.List;

public class ObservableValue<T>
{
    private T value;

    private List<ValueObserver<T>> valueObservers;

    public ObservableValue()
    {
        valueObservers = new ArrayList<>();
    }

    public void addObserver(ValueObserver<T> valueObserver)
    {
        valueObservers.add(valueObserver);
    }

    public void setValue(T value)
    {
        this.value = value;
        valueObservers.forEach(ValueObserver::notifyObserver);
        valueObservers.clear();
    }

    public T getValue()
    {
        return value;
    }
}
