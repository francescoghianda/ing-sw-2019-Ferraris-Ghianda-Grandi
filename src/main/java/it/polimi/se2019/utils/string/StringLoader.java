package it.polimi.se2019.utils.string;

import it.polimi.se2019.utils.logging.Logger;

import java.io.IOException;
import java.util.*;

public class StringLoader
{

    private static HashMap<String, String> strings;

    private StringLoader()
    {

    }

    public static String getString(String id)
    {
        if(strings == null)loadStrings();
        return strings.getOrDefault(id, "Error loading string "+id);
    }

    private static void loadStrings()
    {
        try
        {
            strings = new HashMap<>();
            Properties stringsProp = new Properties();
            stringsProp.loadFromXML(StringLoader.class.getResourceAsStream("/xml/cli/strings.xml"));
            Set<String> ids = stringsProp.stringPropertyNames();

            ids.forEach(id ->
            {
                if(strings.containsKey(id)) Logger.warning("String with id "+id+" is duplicated!\nThe string will be overwritten");
                strings.put(id, correctNewLine(stringsProp.getProperty(id)));
            });
        }
        catch (IOException e)
        {
            Logger.exception(e);
        }
    }

    private static String correctNewLine(String string)
    {
        ArrayList<Character> characterList = new ArrayList<>(Arrays.asList(covert(string.toCharArray())));
        for(int i = 0; i < characterList.size()-1; i++)
        {
            if(characterList.get(i) == '\\' && characterList.get(i+1) == 'n')
            {
                characterList.set(i, '\n');
                characterList.remove(i+1);
            }
        }
        StringBuilder builder = new StringBuilder();
        characterList.forEach(builder::append);
        return builder.toString();
    }

    private static Character[] covert(char[] array)
    {
        Character[] converted = new Character[array.length];
        for(int i = 0; i < converted.length; i++) converted[i] = array[i];
        return converted;
    }
}
