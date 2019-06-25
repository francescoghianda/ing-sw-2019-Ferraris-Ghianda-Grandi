package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.player.Player;

public class PlayerParameter extends ParameterType<Player>
{

    @Override
    public Player cast(String parameter, Executor executor) throws ParameterCastException
    {
        if(!executor.containsPlayer(parameter))throw new ParameterCastException();
        return executor.getPlayer(parameter).orElse(null);
    }
}
