package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;

public class AskIfCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.GENERIC, ParameterTypes.GENERIC);

    private String question;
    private String varName;

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
