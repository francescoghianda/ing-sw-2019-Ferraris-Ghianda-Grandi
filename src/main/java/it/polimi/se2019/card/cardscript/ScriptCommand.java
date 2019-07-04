package it.polimi.se2019.card.cardscript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains the string of the command
 */
public class ScriptCommand
{
    private final String command;
    private final List<String> parameters;
    private final int scriptLineNumber;

    /**
     * Containes the command, the parameters and the lineNumber of the script command
     * @param command
     * @param parameters
     * @param scriptLineNumber
     */
    public ScriptCommand(String command, String[] parameters, int scriptLineNumber)
    {
        this.command = command;
        this.parameters = Arrays.stream(parameters).map(String::trim).collect(Collectors.toList());
        this.scriptLineNumber = scriptLineNumber;
    }

    public String getCommand()
    {
        return command;
    }

    public int getScriptLineNumber()
    {
        return scriptLineNumber;
    }

    public List<String> getParametersAsList()
    {
        return new ArrayList<>(parameters);
    }

    public String[] getParameters()
    {
        String[] parametersArray = new String[parameters.size()];
        parametersArray = parameters.toArray(parametersArray);
        return parametersArray;
    }
}
