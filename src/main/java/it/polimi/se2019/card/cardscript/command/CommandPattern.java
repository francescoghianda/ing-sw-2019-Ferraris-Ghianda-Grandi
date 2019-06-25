package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.CardScriptErrorException;
import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterType;
import it.polimi.se2019.utils.logging.Logger;

public class CommandPattern
{
    private ParameterType[] parameterTypes;

    private Object[] castedParameters;

    public CommandPattern(ParameterType... parameterTypes)
    {
        this.parameterTypes = parameterTypes;
    }

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

            /*if(parameterType == ParameterType.PLAYER && !executor.containsPlayer(parameter))return false;
            if(parameterType == ParameterType.BLOCK && !executor.containsBlock(parameter))return false;
            if(parameterType == ParameterType.PLAYER_OR_BLOCK && !(executor.containsPlayer(parameter) || executor.containsBlock(parameter)))return false;
            if(parameterType == ParameterType.DIGIT && !isDigit(parameter))return false;
            if(parameterType == ParameterType.BOOLEAN && !isBoolean(parameter))return false;*/
        }

        return true;
    }

    Object getParameter(int index)
    {
        if(index >= castedParameters.length || index < 0)return null;
        return castedParameters[index];
    }
}
