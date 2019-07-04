package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.CardScriptErrorException;
import it.polimi.se2019.card.cardscript.Executor;

/**class that converts the parameter passed through the cardscript function into a block type variable for the executor
 *
 * @param <T> the type of the parameter
 */
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
