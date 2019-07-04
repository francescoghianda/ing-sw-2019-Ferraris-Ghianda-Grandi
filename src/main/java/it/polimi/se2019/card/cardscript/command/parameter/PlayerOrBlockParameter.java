package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.Executor;

/**
 * class that converts the parameter passed through the cardscript function into a block type variable for the executor
 */
public class PlayerOrBlockParameter extends ParameterType<Object>
{
    @Override
    public Object cast(String parameter, Executor executor) throws ParameterCastException
    {
        if(executor.containsPlayer(parameter))
        {
            return executor.getPlayer(parameter).orElse(null);
        }
        else if(executor.containsBlock(parameter))
        {
            return executor.getBlock(parameter).orElse(null);
        }

        throw new ParameterCastException();
    }
}
