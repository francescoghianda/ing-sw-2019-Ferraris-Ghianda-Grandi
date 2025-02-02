package it.polimi.se2019.controller.settings;

/**
 * it defines the settings including the name, a default value and a value
 * @param <T>
 */
public class Setting<T>
{
    private final String name;
    private final T defaultValue;
    private T value;

    public Setting(String name, T defaultValue)
    {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    String getName()
    {
        return name;
    }

    void set(T value)
    {
        this.value = value;
    }

    T get()
    {
        if(value != null)return value;
        return defaultValue;
    }

}
