package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.LogicExpression;
import it.polimi.se2019.card.cardscript.LogicExpressionEvaluationException;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  * Receive a logic expression that has to be associated with just one player. If the logic expression is
 *  * associated with more than one player, select the first one
 */
public class GetPlayerCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.GENERIC, ParameterTypes.LOGIC_EXPRESSION);

    private String varName;
    private LogicExpression logicExpression;

    public GetPlayerCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        varName = (String) getParam(0);
        logicExpression = (LogicExpression) getParam(1);
    }

    @Override
    protected boolean exec() throws CommandExecutionException
    {
        List<Player> allPlayers = executor.getContextPlayer().getGameController().getPlayers();
        allPlayers.remove(executor.getContextPlayer());

        List<Player> validPlayers = LogicExpression.filter(allPlayers, logicExpression, executor);

        if(validPlayers.isEmpty())
        {
            executor.addPlayer(varName, null);
            return false;
        }
        executor.addPlayer(varName, validPlayers.get(0));
        return true;
    }

    @Override
    public Commands getType()
    {
        return Commands.GET_PLAYER;
    }
}
