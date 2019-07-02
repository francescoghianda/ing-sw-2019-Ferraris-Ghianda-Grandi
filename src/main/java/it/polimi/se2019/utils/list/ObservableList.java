package it.polimi.se2019.utils.list;

import java.lang.reflect.Array;
import java.util.*;

public class ObservableList<E> implements Iterable<E>, Collection<E>
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

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean remove(Object element)
    {
        if(!list.contains(element))return false;
        list.remove(element);
        listeners.forEach(listener -> listener.onChanged((E)element, ListChangeEvent.ELEMENT_REMOVED));
        return true;
    }

    public E remove(int index)
    {
        E element = list.remove(index);
        listeners.forEach(listener -> listener.onChanged(element, ListChangeEvent.ELEMENT_REMOVED));
        return element;
    }

    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    @Override
    public int size()
    {
        return list.size();
    }

    @Override
    public Iterator<E> iterator()
    {
        return list.iterator();
    }

    @Override
    public Object[] toArray()
    {
        Object[] array = new Object[list.size()];
        for(int i = 0; i < array.length; i++)
        {
            array[i] = list.get(i);
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a)
    {
        T[] array = a;
        if(array.length < list.size())array = (T[]) Array.newInstance(a.getClass(), list.size());

        for(int i = 0; i < array.length; i++)
        {
            if(i < list.size())array[i] = (T) list.get(i);
            else array[i] = null;
        }

        return array;
    }

    @Override
    public boolean contains(Object element)
    {
        return list.contains(element);
    }

    @Override
    public boolean containsAll(Collection<?> elements)
    {
        return list.containsAll(elements);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeAll(Collection<?> elements)
    {
        boolean ret = list.removeAll(elements);
        if(ret)listeners.forEach(listener -> listener.onChanged((Collection<? extends E>) elements, ListChangeEvent.COLLECTION_REMOVED));
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

    @Override
    public boolean add(E element)
    {
        list.add(element);
        listeners.forEach(listener -> listener.onChanged(element, ListChangeEvent.ELEMENT_ADDED));
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> elements)
    {
        list.addAll(elements);
        listeners.forEach(listener -> listener.onChanged(elements, ListChangeEvent.COLLECTION_ADDED));
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        boolean executed = false;
        for(E elem : list)
        {
            if(!c.contains(elem))
            {
                list.remove(elem);
                executed = true;
            }
        }
        return executed;
    }

    @Override
    public void clear()
    {
        list.clear();
        listeners.forEach(ListChangeListener::onClear);
    }
}
