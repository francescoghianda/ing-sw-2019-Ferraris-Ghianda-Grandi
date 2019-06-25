package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;

public class EndIfCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern();

    public EndIfCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);
    }

    @Override
    protected boolean exec()
    {
        return true;
    }

    @Override
    public Commands getType()
    {
        return Commands.END_IF;
    }
}
