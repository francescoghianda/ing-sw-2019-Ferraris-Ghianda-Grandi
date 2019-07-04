package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.Executor;

/**
 * class that converts the parameter passed through the cardscript function into a block type variable for the executor
 */
public class GenericParameter extends ParameterType<String>
{

    @Override
    String cast(String parameter, Executor executor)
    {
        return parameter;
    }

}
