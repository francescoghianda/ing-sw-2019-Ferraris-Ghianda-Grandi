package it.polimi.se2019.card.cardscript.command;

public class CommandExecutionException extends Exception
{
    private final CommandError error;

    public CommandExecutionException(CommandError error)
    {
        super();
        this.error = error;
    }

    public CommandError getError()
    {
        return error;
    }
}
