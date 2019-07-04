package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.CardScriptErrorException;
import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterType;
import it.polimi.se2019.utils.logging.Logger;

/**
 * The pattern for all the possible commands, which contains the type of the parameters, the casted parameters
 */
public class CommandPattern
{
    private ParameterType[] parameterTypes;

    private Object[] castedParameters;

    /**
     * constract a new commandPattern
     * @param parameterTypes are the type of the parameters of the new commandPattern
     */
    public CommandPattern(ParameterType... parameterTypes)
    {
        this.parameterTypes = parameterTypes;
    }

    /**
     * it checks if the commandPattern is valid
     * @param executor is the executor of the command  of the commandPatter
     * @param parameters are tha parameters of the command of the commandPattern
     * @return
     */
    public boolean matches(Executor executor, String[] parameters)
    {
        if(parameters.length != parameterTypes.length)return false;

        castedParameters = new Object[parameters.length];

        for(int i = 0; i < parameterTypes.length; i++)
        {
            String parameter = parameters[i];
            ParameterType parameterType = parameterTypes[i];

            try
            {
                castedParameters[i] = parameterType.set(parameter, executor);
            }
            catch (CardScriptErrorException e)
            {
                Logger.exception(e);
                return false;
            }
        }

        return true;
    }

    Object getParameter(int index)
    {
        if(index >= castedParameters.length || index < 0)return null;
        return castedParameters[index];
    }
}
