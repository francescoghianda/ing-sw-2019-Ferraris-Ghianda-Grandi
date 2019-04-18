package it.polimi.se2019.utils.logging;

import java.io.OutputStream;
import java.io.PrintStream;

public class LoggerOutputStream
{
    private PrintStream printStream;
    private boolean supportColor;

    public LoggerOutputStream(PrintStream printStream, boolean supportColor)
    {
        this.printStream = printStream;
        this.supportColor = supportColor;
    }

    public LoggerOutputStream(OutputStream outputStream, boolean supportColor)
    {
        this.printStream = new PrintStream(outputStream);
        this.supportColor = supportColor;
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

}
