package it.polimi.se2019.utils.logging;

/**
 * A message that can be printed by a logger
 */
public class LogMessage
{
    private final Level level;
    private final String message;
    private boolean printColor;

    private LogMessage(Level level, String message)
    {
        this.level = level;
        this.message = message;
    }

    /**
     * Create the message
     * @param level Level of the message
     * @param message The message to print
     * @return The created message
     */
    public static LogMessage pack(Level level, String message)
    {
        return new LogMessage(level, message);
    }

    public LogMessage setPrintColor(boolean printColor)
    {
        this.printColor = printColor;
        return this;
    }

    public boolean isPrintColor()
    {
        return this.printColor;
    }

    public Level getLevel()
    {
        return this.level;
    }

    public String getMessage()
    {
        return this.message;
    }

    public String toString()
    {
        return level + " " + getMessage();
    }
}
