package it.polimi.se2019.utils.logging;

import java.io.OutputStream;
import java.io.PrintStream;

public class LoggerOutputStream
{
    private PrintStream printStream;
    private boolean supportColor;
    private Logger.LoggerFormatter formatter;
    private int minValue;

    public LoggerOutputStream(PrintStream printStream, Logger.LoggerFormatter formatter, boolean supportColor)
    {
        this.printStream = printStream;
        this.supportColor = supportColor;
        this.formatter = formatter;
    }

    public LoggerOutputStream(OutputStream outputStream, Logger.LoggerFormatter formatter, boolean supportColor)
    {
        this.printStream = new PrintStream(outputStream);
        this.supportColor = supportColor;
        this.formatter = formatter;
    }

    public boolean supportColor()
    {
        return this.supportColor;
    }

    public void print(String x)
    {
        printStream.print(x);
    }

    public void println(String x)
    {
        printStream.println(x);
    }

    public Logger.LoggerFormatter getFormatter()
    {
        return this.formatter;
    }

    public void setFormatter(Logger.LoggerFormatter formatter)
    {
        this.formatter = formatter;
    }

    void setMinLevel(Level minLevel)
    {
        this.minValue = minLevel.getValue();
    }

    int getMinValue()
    {
        return this.minValue;
    }

}
