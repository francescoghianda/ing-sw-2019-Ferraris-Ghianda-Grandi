package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.CardScriptErrorException;
import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.OnCommandExecutedListener;

public abstract class Command
{
    protected final String[] parameters;
    protected final Executor executor;
    private CommandPattern pattern;

    private OnCommandExecutedListener listener;

    private boolean result;

    private int lineNumber;

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
