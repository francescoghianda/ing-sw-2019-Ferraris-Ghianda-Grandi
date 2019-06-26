package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.CardScriptErrorException;
import it.polimi.se2019.card.cardscript.LogicExpression;
import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.LogicExpressionEvaluationException;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectBlockCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.GENERIC, ParameterTypes.LOGIC_EXPRESSION);

    private String varName;
    private LogicExpression logicExpression;

    public SelectBlockCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        varName = (String) getParam(0);
        logicExpression = (LogicExpression) getParam(1);
    }

    @Override
    public boolean exec() throws CommandExecutionException
    {
        List<Block> allBlocks = executor.getContextPlayerBlock().getRoom().getMap().getAllBlocks();
        ArrayList<Coordinates> validBlocks = allBlocks.stream().filter(block -> {
            try
            {
                return logicExpression.evaluate(executor, block);
            }
            catch (LogicExpressionEvaluationException e)
            {
                Logger.exception(e);
                return false;
            }
        }).map(Block::getCoordinates).collect(Collectors.toCollection(ArrayList::new));

        if(validBlocks.isEmpty())
        {
            executor.addBlock(varName, null);
            throw new CommandExecutionException(new CommandError(this, CommandErrorHandler.CONTINUE));
        }

        Coordinates chosen;
        try
        {
            chosen = executor.getContextPlayer().getView().chooseBlockFrom(validBlocks);
        }
        catch (CanceledActionException e)
        {
            throw new CommandExecutionException(new CommandError(this, CommandErrorHandler.STOP_CANCELED_BY_USER));
        }
        Block chosenBlock = executor.getContextPlayerBlock().getRoom().getMap().getBlock(chosen.getX(), chosen.getY());

        if(chosenBlock == null)throw new CommandExecutionException(new CommandError(this, CommandErrorHandler.STOP_ERROR));

        return executor.addBlock(varName, chosenBlock);
    }

    @Override
    public Commands getType()
    {
        return Commands.SELECT_BLOCK;
    }
}
