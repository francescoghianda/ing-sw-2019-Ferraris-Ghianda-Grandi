package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.map.Block;

class BlockParameter extends ParameterType<Block>
{

    @Override
    public Block cast(String parameter, Executor executor) throws ParameterCastException
    {
        if(!executor.containsBlock(parameter))throw new ParameterCastException();
        return executor.getBlock(parameter).orElse(null);
    }
}
