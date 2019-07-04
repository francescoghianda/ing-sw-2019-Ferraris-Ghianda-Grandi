package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * It receive a block or a player. If it receives a block, it gives a damage to all the players into that block.
 * If it receives a player,it gives him a quantity of damage equal to the value expressed in the "DIGIT" parameter
 */
public class HitCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.PLAYER_OR_BLOCK, ParameterTypes.DIGIT);

    private List<Player> players;
    private int damage;

    public HitCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        players = new ArrayList<>();

        if(getParam(0) != null)
        {
            if(isPlayer(parameters[0])) players.add((Player)getParam(0));
            else players.addAll(((Block)getParam(0)).getPlayers());
        }


        damage = Integer.parseInt(parameters[1]);
    }

    @Override
    public boolean exec()
    {
        if(players.isEmpty())return false;
        players.remove(executor.getContextPlayer());
        players.forEach(player -> player.getGameBoard().addDamage(executor.getContextPlayer(), damage));
        return true;
    }

    @Override
    public Commands getType()
    {
        return Commands.HIT;
    }
}
