package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;

/**
 * It ask a question and generates a boolean variable based on the answer
 */
public class AskIfCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.GENERIC, ParameterTypes.GENERIC);

    private String question;
    private String varName;

    /**
     * constructs a new AskIfCommand
     * @param executor is the executor the executes the command
     * @param parameters are the parameters of the command that will be executed
     */
    public AskIfCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);
        this.question = (String) getParam(0);
        varName = (String) getParam(1);
    }

    @Override
    protected boolean exec()
    {
        boolean value = askIf(question);
        return executor.addBoolean(varName, value);
    }

    @Override
    public Commands getType()
    {
        return Commands.ASK_IF;
    }
}
