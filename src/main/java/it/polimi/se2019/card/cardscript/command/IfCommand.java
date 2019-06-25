package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;

public class IfCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.GENERIC);

    private String varName;

    public IfCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);
        varName = (String) getParam(0);
    }

    @Override
    protected boolean exec()
    {
        return executor.getBoolean(varName);
    }

    @Override
    public Commands getType()
    {
        return Commands.IF;
    }
}
