package it.polimi.se2019.card.cardscript;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Check a string of script and checks if it is valid
 */
public class Script
{
    private static String commandRegex = "[a-z|_]+([(]((([^,]),?)*[a-z0-9\\s])?[)])";
    private static Pattern commandPattern = Pattern.compile(commandRegex);

    private List<String> scriptLines;
    private List<ScriptCommand> commands;

    /**
     * Constructs a new script from which to create commands
     * @param script contains the script not parsed
     */
    public Script(String script)
    {
        scriptLines = new ArrayList<>(Arrays.asList(script.split("\n")));
        scriptLines = scriptLines.stream().map(String::trim).map(String::toLowerCase).collect(Collectors.toList());
        commands = new ArrayList<>();
        createCommands();
    }

    /**
     * Creates the command from the specific script
     */
    private void createCommands()
    {
        for(int i = 0; i < scriptLines.size(); i++)
        {
            if(!commandPattern.matcher(scriptLines.get(i)).matches())throw new CardScriptErrorException("Syntax error at line "+i);
            createCommand(scriptLines.get(i), i);
        }
    }

    /**
     * Create a new ScriptCommand object parsing the String line
     * @param line the String that contains the raw command
     * @param lineNumber the line number of the command in the original script
     */
    private void createCommand(String line, int lineNumber)
    {
        char[] chars = line.toCharArray();
        StringBuilder command = new StringBuilder();
        int i = 0;
        for(; i < chars.length; i++)
        {
            if(chars[i] == '(')break;
            command.append(chars[i]);
        }
        String[] parameters = line.substring(i+1, line.length()-1).split(",");

        if(parameters.length == 1 && parameters[0].isEmpty())parameters = new String[0];

        commands.add(new ScriptCommand(command.toString(), parameters, lineNumber));
    }

    public Iterator<ScriptCommand> iterator()
    {
        return commands.iterator();
    }

}
