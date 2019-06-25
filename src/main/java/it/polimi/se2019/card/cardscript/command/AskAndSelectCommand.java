package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.CardScriptErrorException;
import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;

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

    @Override
    public Commands getType()
    {
        return Commands.ASK_AND_SELECT;
    }
}
