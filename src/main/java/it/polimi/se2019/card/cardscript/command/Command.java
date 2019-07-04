package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.CardScriptErrorException;
import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.OnCommandExecutedListener;

/**
 * the superclass for all the commands of cardscript
 */
public abstract class Command
{
    protected final String[] parameters;
    protected final Executor executor;
    private CommandPattern pattern;

    private OnCommandExecutedListener listener;

    private boolean result;

    private int lineNumber;

    /**
     * Constructs a new command
     * @param executor indicates the executor of the command
     * @param parameters refers to the parameters of the command
     * @param pattern defines which parameters have to be passed to the command
     */
    public Command(Executor executor, String[] parameters, CommandPattern pattern)
    {
        if(!pattern.matches(executor, parameters))throw new CardScriptErrorException("Invalid parameters!");
        this.pattern = pattern;
        this.executor = executor;
        this.parameters = parameters;
        this.listener = executor;
    }

    public Command setLineNumber(int lineNumber)
    {
        this.lineNumber = lineNumber;
        return this;
    }

    /**
     * Executes the command and invokes the event onCommandExecuted
     * @return the result of the exec of the command, if the command has been executed
     * @throws CommandExecutionException
     */
    public boolean execute() throws CommandExecutionException
    {
        result = exec();
        if(listener != null)listener.onCommandExecuted(this);
        return result;
    }

    protected Object getParam(int index)
    {
        return pattern.getParameter(index);
    }

    protected abstract boolean exec() throws CommandExecutionException;

    public abstract Commands getType();

    public boolean getResult()
    {
        return this.result;
    }

    protected boolean isPlayer(String varName)
    {
        return executor.containsPlayer(varName);
    }

    protected boolean isBlock(String varName)
    {
        return executor.containsBlock(varName);
    }

    protected boolean askIf(String question)
    {
        String answer = executor.getContextPlayer().getView().choose(question, "Si", "No");
        return answer.equals("Si");
    }

    public String toString()
    {
        return getType()+" at line "+lineNumber;
    }
}
