package it.polimi.se2019.ui.cli;

/**
 * defines the option of generic parameter T
 * @param <T>
 */
public class Option<T>
{
    private String option;
    private int index;
    private T value;

    public Option(String option, int index)
    {
        this.option = option;
        this.index = index;
    }

    public Option(String option, int index, T value)
    {
        this.option = option;
        this.index = index;
        this.value = value;
    }

    public String getOption()
    {
        return option;
    }

    public int getIndex()
    {
        return index;
    }

    public T getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return "Option <"+option+">";
    }
}
