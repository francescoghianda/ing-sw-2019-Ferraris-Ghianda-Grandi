package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.CardScriptErrorException;
import it.polimi.se2019.card.cardscript.Executor;

public abstract class ParameterType<T>
{
    abstract T cast(String parameter, Executor executor) throws ParameterCastException;

    public final T set(String parameter, Executor executor)
    {
        try
        {
            return cast(parameter, executor);
        }
        catch (ParameterCastException e)
        {
            throw new CardScriptErrorException("Invalid parameter: "+parameter);
        }
    }
}
