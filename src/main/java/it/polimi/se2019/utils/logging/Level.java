package it.polimi.se2019.utils.logging;

import it.polimi.se2019.utils.constants.Ansi;

public enum Level
{
    INFO(0, Ansi.WHITE), DEBUG(1, Ansi.GREEN), WARNING(2, Ansi.YELLOW), ERROR(3, Ansi.RED), CLI(4, Ansi.WHITE);

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