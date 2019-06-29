package it.polimi.se2019.utils.list;

import java.util.*;

public class ObservableList<E> implements Iterable<E>
{
    private List<ListChangeListener<E>> listeners;
    private List<E> list;

    public ObservableList()
    {
        list = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public ObservableList(Collection<E> collection)
    {
        list = new ArrayList<>(collection);
        listeners = new ArrayList<>();
    }

    public void addChangeListener(ListChangeListener<E> listener)
    {
        listeners.add(listener);
    }

    public void removeChangeListener(ListChangeListener<E> listener)
    {
        listeners.remove(listener);
    }

    public boolean remove(E element)
    {
        if(!list.contains(element))return false;
        list.remove(element);
        listeners.forEach(listener -> listener.onChanged(element, ListChangeEvent.ELEMENT_REMOVED));
        return true;
    }

    public E remove(int index)
    {
        E element = list.remove(index);
        listeners.forEach(listener -> listener.onChanged(element, ListChangeEvent.ELEMENT_REMOVED));
        return element;
    }

    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    public int size()
    {
        return list.size();
    }

    public Iterator<E> iterator()
    {
        return list.iterator();
    }

    public boolean contains(E element)
    {
        return list.contains(element);
    }

    public boolean containsAll(Collection<? extends E> elements)
    {
        return list.containsAll(elements);
    }

    public boolean removeAll(Collection<? extends E> elements)
    {
        boolean ret = list.removeAll(elements);
        if(ret)listeners.forEach(listener -> listener.onChanged(elements, ListChangeEvent.COLLECTION_REMOVED));
        return ret;
    }

    public E get(int index)
    {
        return list.get(index);
    }

    public void shuffle()
    {
        Collections.shuffle(list);
    }

    public ArrayList<E> toArrayList()
    {
        return new ArrayList<>(list);
    }

    public boolean add(E element)
    {
        list.add(element);
        listeners.forEach(listener -> listener.onChanged(element, ListChangeEvent.ELEMENT_ADDED));
        return true;
    }

    public boolean addAll(Collection<? extends E> elements)
    {
        list.addAll(elements);
        listeners.forEach(listener -> listener.onChanged(elements, ListChangeEvent.COLLECTION_ADDED));
        return true;
    }

    public void clear()
    {
        list.clear();
        listeners.forEach(ListChangeListener::onClear);
    }
}
