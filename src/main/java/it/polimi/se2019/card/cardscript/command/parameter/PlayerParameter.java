package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.player.Player;

/**class that converts the parameter passed through the cardscript function into a block type variable for the executor
 *
 */
public class PlayerParameter extends ParameterType<Player>
{

    @Override
    public Player cast(String parameter, Executor executor) throws ParameterCastException
    {
        if(!executor.containsPlayer(parameter))throw new ParameterCastException();
        return executor.getPlayer(parameter).orElse(null);
    }
}
