package it.polimi.se2019.card.cardscript.command;

/**
 * Defines the exception of the command
 */
public class CommandExecutionException extends Exception
{
    private final CommandError error;

    /**
     *
     * @param error
     */

    public CommandExecutionException(CommandError error)
    {
        super();
        this.error = error;
    }

    /**
     *
     * @return
     */
    public CommandError getError()
    {
        return error;
    }
}
