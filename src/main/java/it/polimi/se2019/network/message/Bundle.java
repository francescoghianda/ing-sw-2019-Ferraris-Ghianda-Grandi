package it.polimi.se2019.network.message;

import java.io.Serializable;

public class Bundle<T1 extends Serializable, T2 extends Serializable> implements Serializable
{
    public static final long serialVersionUID = 8L;

    private final T1 first;
    private final T2 second;

    private Bundle(T1 first, T2 second)
    {
        this.first = first;
        this.second = second;
    }

    public static <T1 extends Serializable, T2 extends Serializable> Bundle<T1, T2> of(T1 first, T2 second)
    {
        if(first == null || second == null)throw new NullPointerException();
        return new Bundle<>(first, second);
    }

    public static <T1 extends Serializable, T2 extends Serializable> Bundle<T1, T2> ofFirstNullable(T1 first, T2 second)
    {
        if(second == null)throw new NullPointerException();
        return new Bundle<>(first, second);
    }

    public static <T1 extends Serializable, T2 extends Serializable> Bundle<T1, T2> ofSecondNullable(T1 first, T2 second)
    {
        if(first == null)throw new NullPointerException();
        return new Bundle<>(first, second);
    }

    public static <T1 extends Serializable, T2 extends Serializable> Bundle<T1, T2> ofNullable(T1 first, T2 second)
    {
        return new Bundle<>(first, second);
    }

    public static <T1 extends Serializable, T2 extends Serializable> Bundle<T1, T2> empty()
    {
        return new Bundle<>(null, null);
    }

    public static <T1 extends Serializable, T2 extends Serializable> Bundle<T1, T2> cast(Object obj, Class<T1> firstType, Class<T2> secondType)
    {
        if (obj instanceof Bundle)
        {
            Bundle bundle = (Bundle) obj;

            if((bundle.first == null || firstType.isAssignableFrom(bundle.first.getClass())) && (bundle.second == null || secondType.isAssignableFrom(bundle.second.getClass())))
            {
                return Bundle.ofNullable(firstType.cast(bundle.first), secondType.cast(bundle.second));
            }
        }

        throw new ClassCastException();
    }

    public boolean isFirstPresent()
    {
        return first != null;
    }

    public boolean isSecondPresent()
    {
        return second != null;
    }

    public T1 getFirst()
    {
        return first;
    }


    public T2 getSecond()
    {
        return second;
    }
}
