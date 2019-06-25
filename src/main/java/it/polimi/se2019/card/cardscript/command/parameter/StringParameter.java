package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.utils.string.StringLoader;

public class StringParameter extends ParameterType<String>
{

    @Override
    String cast(String parameter, Executor executor)
    {
        if(parameter.startsWith("$"))return StringLoader.getString(parameter.substring(1));
        return parameter;
    }
}
