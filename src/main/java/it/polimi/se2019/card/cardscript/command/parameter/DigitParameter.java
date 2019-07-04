package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.Executor;

/**
 * class that converts the parameter passed through the cardscript function into a block type variable for the executor
 */
class DigitParameter extends ParameterType<Number>
{

    @Override
    Number cast(String parameter, Executor executor) throws ParameterCastException
    {
        if(!isDigit(parameter))throw new ParameterCastException();
        return Integer.parseInt(parameter);
    }

    private boolean isDigit(String parameter)
    {
        if(parameter == null || parameter.isEmpty())return false;
        for(char ch : parameter.toCharArray())if(!Character.isDigit(ch))return false;
        return true;
    }
}
