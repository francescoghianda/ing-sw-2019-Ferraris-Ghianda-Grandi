package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.player.Player;

import java.util.ArrayList;
import java.util.List;

public class MoveCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.PLAYER_OR_BLOCK, ParameterTypes.BLOCK);

    private List<Player> players;
    private Block destinationBlock;

    public MoveCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        players = new ArrayList<>();

        if(isPlayer(parameters[0])) players.add((Player)getParam(0));
        else players.addAll(((Block)getParam(0)).getPlayers());

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
