package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;

public class SelectRoomCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.GENERIC, ParameterTypes.LOGIC_EXPRESSION);

    public SelectRoomCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);
    }

    @Override
    protected boolean exec() throws CommandExecutionException
    {
        return false;
    }

    @Override
    public Commands getType()
    {
        return Commands.SELECT_ROOM;
    }
}
