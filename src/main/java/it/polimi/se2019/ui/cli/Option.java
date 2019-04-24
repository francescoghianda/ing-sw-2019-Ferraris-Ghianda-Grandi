package it.polimi.se2019.ui.cli;

public class Option<T>
{
    private String option;
    private String abbreviation;
    private T value;

    public Option(String option, String abbreviation)
    {
        this.option = option;
        this.abbreviation = abbreviation;
    }

    public Option(String option, String abbreviation, T value)
    {
        this.option = option;
        this.abbreviation = abbreviation;
        this.value = value;
    }

    public String getOption()
    {
        return option;
    }

    public String getAbbreviation()
    {
        return abbreviation;
    }

    public T getValue()
    {
        return this.value;
    }

    public boolean compare(String opt)
    {
        return opt.equalsIgnoreCase(option) || opt.equalsIgnoreCase(abbreviation);
    }

    @Override
    public String toString()
    {
        return "["+option+"]";
    }
}
