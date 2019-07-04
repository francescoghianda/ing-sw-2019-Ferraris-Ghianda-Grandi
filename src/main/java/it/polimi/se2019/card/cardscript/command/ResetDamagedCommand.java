package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;

/**
 * The command that reset all the damaged players from a specific (context) player
 */
public class ResetDamagedCommand extends Command
{

    public ResetDamagedCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, new CommandPattern());
    }

    @Override
    protected boolean exec()
    {
        executor.getContextPlayer().resetDamagedPlayers();
        return true;
    }

    @Override
    public Commands getType()
    {
        return Commands.RESET_DAMAGED_PLAYERS;
    }
}
