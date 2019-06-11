package it.polimi.se2019.network.message;

import java.io.Serializable;

public class Bundle<T1 extends Serializable, T2 extends Serializable> implements Serializable
{
    public static final long serialVersionUID = 8L;

    private final T1 obj1;
    private final T2 obj2;

    public Bundle(T1 first, T2 second)
    {
        this.obj1 = first;
        this.obj2 = second;
    }

    public T1 getFirst()
    {
        return obj1;
    }

    public T2 getSecond()
    {
        return obj2;
    }
}
