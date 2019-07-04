package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.LogicExpression;
import it.polimi.se2019.card.cardscript.LogicExpressionEvaluationException;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Receive a logic expression that has to be associated with just one block. If the logic expression is
 * associated with more than one block, select the first one
 */
public class GetBlockCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.GENERIC, ParameterTypes.LOGIC_EXPRESSION);

    private LogicExpression logicExpression;
    private String varName;

    public GetBlockCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        varName = (String)getParam(0);
        logicExpression = (LogicExpression)getParam(1);
    }

    @Override
    protected boolean exec() throws CommandExecutionException
    {
        List<Block> allBlocks = executor.getContextPlayerBlock().getRoom().getMap().getAllBlocks();

        List<Block> validBlocks = LogicExpression.filter(allBlocks, logicExpression, executor);

        if(validBlocks.isEmpty())
        {
            executor.addBlock(varName, null);
            return false;
        }
        executor.addBlock(varName, validBlocks.get(0));
        return true;
    }

    @Override
    public Commands getType()
    {
        return Commands.GET_BLOCK;
    }
}
