package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.player.ImpossibleActionException;

public class CommandError
{
    private final Command command;
    private final CommandErrorHandler handler;

    public CommandError(Command command, CommandErrorHandler handler)
    {
        this.command = command;
        this.handler = handler;
    }

    public Command getCommand()
    {
        return command;
    }

    public void handle() throws CanceledActionException, ImpossibleActionException
    {
        handler.handle(this);
    }
}