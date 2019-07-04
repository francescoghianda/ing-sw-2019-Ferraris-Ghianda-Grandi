package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.CardScriptErrorException;
import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.LogicExpression;
import it.polimi.se2019.card.cardscript.LogicExpressionEvaluationException;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * the class that select a player or a block if the response of question is "yes"
 */
public class AskAndSelectCommand extends Command
{
    private static final String PLAYER_MODE = "player";
    private static final String BLOCK_MODE = "block";

    //question, varName, mode(player|block), logic expression
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.STRING, ParameterTypes.GENERIC, ParameterTypes.ENUM(PLAYER_MODE, BLOCK_MODE), ParameterTypes.GENERIC);

    private String question;
    private String varName;
    private String mode;
    private String logicExpression;

    /**
     * Constructs a new command
     * @param executor is the executor which will execute the command
     * @param parameters are the parameters of the command that has to be executed
     */
    public AskAndSelectCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        question = (String) getParam(0);
        varName = (String) getParam(1);
        mode = (String) getParam(2);
        logicExpression = (String) getParam(3);

        if(!mode.equals(PLAYER_MODE) && !mode.equals(BLOCK_MODE))throw new CardScriptErrorException("Invalid parameters!");
    }

    @Override
    protected boolean exec() throws CommandExecutionException
    {
        if(!isSelectionPossible())
        {
            if(mode.equals(PLAYER_MODE)) executor.addPlayer(varName, null);
            else executor.addBlock(varName, null);
            return false;
        }

        boolean answer = askIf(question);

        if(!answer)
        {
            if(mode.equals(PLAYER_MODE)) executor.addPlayer(varName, null);
            else executor.addBlock(varName, null);
            return false;
        }

        Command command;

        if(mode.equals(PLAYER_MODE))
        {
            String[] parameters = new String[3];
            parameters[0] = "false";
            parameters[1] = varName;
            parameters[2] = logicExpression;
            command = new SelectPlayerCommand(executor, parameters);
        }
        else
        {
            String[] parameters = new String[2];
            parameters[0] = varName;
            parameters[1] = logicExpression;
            command = new SelectBlockCommand(executor, parameters);
        }

        return command.execute();
    }

    /**
     * It checks if there is the possibility to select a player or a block according to the rules of logic expressions
     * @return true if there is at least a selectable player/block
     */
    private boolean isSelectionPossible()
    {
        LogicExpression expression = new LogicExpression(logicExpression);
        if(mode.equals(PLAYER_MODE))
        {
            List<Player> validPlayers = executor.getContextPlayer().getGameController().getPlayers();
            validPlayers.remove(executor.getContextPlayer());

            List<Player> filtered = LogicExpression.filter(validPlayers, expression, executor);
            return !filtered.isEmpty();
        }
        else
        {
            List<Block> allBlocks = executor.getContextPlayerBlock().getRoom().getMap().getAllBlocks();
            List<Block> filtered = LogicExpression.filter(allBlocks, expression, executor);
            return !filtered.isEmpty();
        }
    }

    @Override
    public Commands getType()
    {
        return Commands.ASK_AND_SELECT;
    }
}
