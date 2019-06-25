package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.LogicExpression;
import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.LogicExpressionEvaluationException;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SelectPlayerCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.BOOLEAN, ParameterTypes.GENERIC, ParameterTypes.LOGIC_EXPRESSION);

    private boolean optional;
    private String varName;
    private LogicExpression logicExpression;

    public SelectPlayerCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        optional = (boolean) getParam(0);
        varName = (String) getParam(1);
        logicExpression = (LogicExpression) getParam(2);
    }

    @Override
    public boolean exec() throws CommandExecutionException
    {
        List<Player> validPlayers = executor.getContextPlayer().getGameController().getPlayers();
        validPlayers.remove(executor.getContextPlayer());

        ArrayList<String> validUsername = validPlayers.stream().filter(player -> {
                try
                {
                    return logicExpression.evaluate(executor, player);
                }
                catch (LogicExpressionEvaluationException e)
                {
                    Logger.exception(e);
                    return false;
                }
            }).map(Player::getUsername).collect(Collectors.toCollection(ArrayList::new));


        if(optional && !askIf("Vuoi selezionare un altro giocatore?"))
        {
            executor.addPlayer(varName, null);
            return true;
        }

        if(validUsername.isEmpty())
        {
            if(optional)
            {
                executor.addPlayer(varName, null);
                return true;
            }
            throw new CommandExecutionException(new CommandError(this, CommandErrorHandler.STOP_IMPOSSIBLE_ACTION));
        }

        String chosen;
        try
        {
            chosen = executor.getContextPlayer().getView().chooseOrCancel(new Bundle<>("Scegli un giocatore tra", validUsername));
        }
        catch (CanceledActionException e)
        {
            if(optional)
            {
                executor.addPlayer(varName, null);
                return true;
            }
            else throw new CommandExecutionException(new CommandError(this, CommandErrorHandler.STOP_CANCELED_BY_USER));
        }

        Optional<Player> chosenPlayer = executor.getContextPlayer().getGameController().findPlayerByUsername(chosen);

        if(!chosenPlayer.isPresent())throw new CommandExecutionException(new CommandError(this, CommandErrorHandler.STOP_ERROR));

        return executor.addPlayer(varName, chosenPlayer.get());
    }

    @Override
    public Commands getType()
    {
        return Commands.SELECT_PLAYER;
    }
}
