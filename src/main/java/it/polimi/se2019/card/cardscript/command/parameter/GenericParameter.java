package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.Executor;

public class GenericParameter extends ParameterType<String>
{

    @Override
    String cast(String parameter, Executor executor)
    {
        return parameter;
    }

}
