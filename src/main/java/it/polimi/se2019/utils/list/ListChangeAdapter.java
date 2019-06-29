package it.polimi.se2019.utils.list;

import java.util.Collection;

public abstract class ListChangeAdapter<E> implements ListChangeListener<E>
{

    @Override
    public void onChanged(E element, ListChangeEvent eventType)
    {

    }

    @Override
    public void onChanged(Collection<? extends E> elements, ListChangeEvent eventType)
    {

    }

    @Override
    public void onClear()
    {

    }
}
