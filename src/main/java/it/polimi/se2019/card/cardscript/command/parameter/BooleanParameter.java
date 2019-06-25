package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.Executor;

class BooleanParameter extends ParameterType<Boolean>
{

    @Override
    Boolean cast(String parameter, Executor executor) throws ParameterCastException
    {
        if(!isBoolean(parameter))throw new ParameterCastException();
        return Boolean.valueOf(parameter);
    }

    private boolean isBoolean(String parameter)
    {
        parameter = parameter.trim().toLowerCase();
        return parameter.equals("true") || parameter.equals("false");
    }
}
