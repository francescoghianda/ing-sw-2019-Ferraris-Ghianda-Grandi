package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * It receives a block or a player. If it receives a block, it moves the players of that block to the block specified in the
 * "BLOCK" parameter.
 * If it receives a player, it moves only that player to the block specified in the "BLOCK" parameter.
 */
public class MoveCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.PLAYER_OR_BLOCK, ParameterTypes.BLOCK);

    private List<Player> players;
    private Block destinationBlock;

    /**
     * Constructs a new MoveCommand
     * @param executor the executor that will execute the command
     * @param parameters are the parameter for the command that has to be executed
     */
    public MoveCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        players = new ArrayList<>();

        if(getParam(0) != null)
        {
            if(isPlayer(parameters[0])) players.add((Player)getParam(0));
            else players.addAll(((Block)getParam(0)).getPlayers());
        }

        destinationBlock = (Block) getParam(1);
    }

    @Override
    public boolean exec()
    {
        if(destinationBlock == null || players.isEmpty())return false;
        players.forEach(player -> player.setBlock(destinationBlock));
        return true;
    }

    @Override
    public Commands getType()
    {
        return Commands.MOVE;
    }
}
