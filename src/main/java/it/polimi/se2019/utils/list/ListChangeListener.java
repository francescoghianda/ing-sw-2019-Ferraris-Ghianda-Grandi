package it.polimi.se2019.utils.list;

import java.util.Collection;

public interface ListChangeListener<E>
{
    void onChanged(E element, ListChangeEvent eventType);
    void onChanged(Collection<? extends E> elements, ListChangeEvent eventType);
    void onClear();
}
