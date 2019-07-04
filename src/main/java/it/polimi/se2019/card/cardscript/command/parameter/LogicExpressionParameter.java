package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.LogicExpression;
import it.polimi.se2019.card.cardscript.Executor;

/**
 * class that converts the parameter passed through the cardscript function into a block type variable for the executor
 */
class LogicExpressionParameter extends ParameterType<LogicExpression>
{

    @Override
    LogicExpression cast(String parameter, Executor executor)throws ParameterCastException
    {
        try
        {
            return new LogicExpression(parameter);
        }
        catch (Exception e)
        {
            throw new ParameterCastException();
        }
    }
}
