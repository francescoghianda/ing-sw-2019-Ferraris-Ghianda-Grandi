package it.polimi.se2019.utils.logging;

import it.polimi.se2019.utils.constants.AnsiColor;

public enum Level
{
    INFO(0, AnsiColor.WHITE), DEBUG(1, AnsiColor.GREEN), WARNING(2, AnsiColor.YELLOW), ERROR(3, AnsiColor.RED), CLI(4, AnsiColor.WHITE);

    private int value;
    private String printColor;

    Level(int value, String printColor)
    {
        this.value = value;
        this.printColor = printColor;
    }

    public int getValue()
    {
        return this.value;
    }

    public String getPrintColor()
    {
        return this.printColor;
    }

    @Override
    public String toString()
    {
        return "["+name()+"]";
    }
}