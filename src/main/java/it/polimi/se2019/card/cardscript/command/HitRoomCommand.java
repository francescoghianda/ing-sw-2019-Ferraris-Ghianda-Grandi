package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Hits all the players into the room of the specific block
 */
public class HitRoomCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.PLAYER_OR_BLOCK, ParameterTypes.DIGIT);

    private List<Player> players;
    private int damage;

    public HitRoomCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        players = new ArrayList<>();

        if(getParam(0) != null)
        {
            if(isPlayer(parameters[0]))
            {
                ((Player)getParam(0)).getBlock().getRoom().getBlocks().forEach(block -> players.addAll(block.getPlayers()));
            }
            else
            {
                ((Block)getParam(0)).getRoom().getBlocks().forEach(block -> players.addAll(block.getPlayers()));
            }
        }

        damage = Integer.parseInt(parameters[1]);
    }

    @Override
    protected boolean exec()
    {
        if(players.isEmpty())return false;
        players.remove(executor.getContextPlayer());
        players.forEach(player -> player.getGameBoard().addDamage(executor.getContextPlayer(), damage));
        return true;
    }

    @Override
    public Commands getType()
    {
        return Commands.HIT_ROOM;
    }
}
