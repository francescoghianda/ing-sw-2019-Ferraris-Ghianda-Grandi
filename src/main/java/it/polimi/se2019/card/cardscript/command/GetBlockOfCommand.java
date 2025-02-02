package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.player.Player;

/**
 * Receive a player from the parameters of the command and returns the block of that player
 */
public class GetBlockOfCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.GENERIC, ParameterTypes.PLAYER);

    private String varName;
    private Player player;

    public GetBlockOfCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        varName = (String) getParam(0);
        player = (Player) getParam(1);
    }

    @Override
    protected boolean exec()
    {
        if(player == null)
        {
            executor.addBlock(varName, null);
            return false;
        }
        return executor.addBlock(varName, player.getBlock());
    }

    @Override
    public Commands getType()
    {
        return Commands.GET_BLOCK_OF;
    }
}
