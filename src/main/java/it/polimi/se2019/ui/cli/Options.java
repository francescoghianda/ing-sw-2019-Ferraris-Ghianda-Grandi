package it.polimi.se2019.ui.cli;


import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.TimeOutException;
import it.polimi.se2019.utils.constants.Characters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * receives a question and some options of the group of options
 * @param <T>
 */
public final class Options<T>
{
    private String question;
    private ArrayList<Option<T>> options;
    private boolean firstDefault;
    private Option<T> cancelOption;
    private AtomicInteger abbreviationNumber;

    /**
     * constructs new options. It has got in input the question string and the boolean value First Default
     * @param question
     * @param firstDefault
     */
    public Options(String question, boolean firstDefault)
    {
        this.options = new ArrayList<>();
        this.question = question;
        this.firstDefault = firstDefault;
        abbreviationNumber = new AtomicInteger(0);
    }

    public Options<T> addOption(Option<T> option)
    {
        options.add(option);
        return this;
    }

    /**
     * it adds the option that let to cancel an action
     * @return
     */
    private Options<T> addCancelOption()
    {
        cancelOption = new Option<>("Annulla", options.size());
        options.add(cancelOption);
        return this;
    }

    /**
     *   it adds an option, standard value is set NULL
     **/


    public Options<T> addOption(String option)
    {
        options.add(new Option<>(option, options.size()));
        return this;
    }

    /**
     * it also add an option, with a specific value
     * @param option is the option that has to be added
     * @param value
     * @return
     */
    public Options<T> addOption(String option, T value)
    {
        options.add(new Option<>(option, options.size(), value));
        return this;
    }

    /**
     * adds an option for each element of the list, where as value it uses the same object contained
     * in the list. To generate the option string it uses a function that receives
     * the object
     * @param values
     * @param optionToStringFunction
     * @return a string
     */
    public Options<T> addOptions(List<T> values, Function<T, String> optionToStringFunction)
    {
        abbreviationNumber.set(0);
        values.forEach(value -> addOption(optionToStringFunction.apply(value), value));
        return this;
    }

    /**
     * prints the box with the options, prints the question, allows the player to answer and when
     * the player give an answer with a valid option, the Option object
     * returns both the option and the "value" object
     * @return
     */
    private Option<T> showOptions()
    {
        if(options.isEmpty())return null;

        writeOptions();

        Option<T> selected;
        String response;

        //GameConsole.print(question);
        //GameConsole.saveCaretPosition();

        do
        {
            GameConsole.print(question);
            //GameConsole.restoreCaretPosition();
           //GameConsole.eraseLine();

            response = GameConsole.nextLine().trim();

            if(firstDefault && response.isEmpty())
            {
                selected = options.get(0);
                break;
            }
        }
        while ((selected = findOption(response)) == null);

        return selected;
    }

    private OptionList<T> showOptionsMultipleSelectable()
    {
        if(options.isEmpty())return OptionList.empty();

        writeOptions();

        //GameConsole.print(question);
        //GameConsole.saveCaretPosition();

        String response;
        List<Option<T>> selected;

        do
        {
            GameConsole.print(question);
            //GameConsole.restoreCaretPosition();
            //GameConsole.eraseLine();

            response = GameConsole.nextLine().trim();

            if(firstDefault && response.isEmpty())
            {
                return OptionList.of(options.get(0));
            }

            String[]optionsSelected = response.split(",");
            selected = findOptions(optionsSelected);
        }
        while (selected.isEmpty());

        return OptionList.of(selected);
    }

    /**
     *
     * @return
     */
    public Option<T> show() throws TimeOutException
    {
        return showOptions();
    }

    /**
     * adds a "cancel" option at the end of the present options.
     * if the player chooses the " option", the CanceledActionException exception is thrown
     * @return
     * @throws TimeOutException
     * @throws CanceledActionException
     */
    public Option<T> showCancellable() throws TimeOutException, CanceledActionException
    {
        addCancelOption();
        Option<T> selected = showOptions();
        if(selected != null && selected.equals(cancelOption))throw new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER);
        return selected;
    }

    public OptionList<T> showMultipleSelectable() throws TimeOutException
    {
        return showOptionsMultipleSelectable();
    }

    private int getMaxOptionLength()
    {
        return options.stream().map(Option::getOption).mapToInt(String::length).max().orElse(0);
    }

    /**
     * omposes the box with the options, in this case, the first line
     * @param builder
     * @param maxOptionsLength
     */
    private void writeFirstLine(StringBuilder builder, int maxOptionsLength)
    {
        builder.append("\t\t\t");
        for(int i = 0; i < maxOptionsLength+8; i++)
        {
            if(i == 0)builder.append(Characters.BoxDrawing.UPPER_LEFT_CORNER);
            else builder.append(Characters.BoxDrawing.DOUBLE_HORIZONTAL);
        }
        builder.append(Characters.BoxDrawing.UPPER_RIGHT_CORNER).append('\n');
    }

    /**
     * writes the title of the box with the options, the string with "Options"
     * @param builder
     * @param title
     * @param maxOptionsLength
     */

    private void writeTitle(StringBuilder builder, String title, int maxOptionsLength)
    {
        builder.append("\t\t\t");
        int pos = (maxOptionsLength+9)/2 - title.length()/2;

        for(int i = 0; i < maxOptionsLength+8; i++)
        {
            if(i == 0)builder.append(Characters.BoxDrawing.DOUBLE_VERTICAL);
            else if(i < pos || i-pos >= title.length()) builder.append(' ');
            else builder.append(title.charAt(i-pos));
        }
        builder.append(Characters.BoxDrawing.DOUBLE_VERTICAL).append('\n');
    }

    /**
     * writes the line that divides the title from the options
     * @param builder
     * @param maxOptionsLength
     */

    private void writeMidLine(StringBuilder builder, int maxOptionsLength)
    {
        builder.append("\t\t\t");
        for(int i = 0; i < maxOptionsLength+8; i++)
        {
            if(i == 0)builder.append(Characters.BoxDrawing.DOUBLE_LEFT_T_CHAR);
            else if(i == 5)builder.append(Characters.BoxDrawing.DOUBLE_T_CHAR);
            else builder.append(Characters.BoxDrawing.DOUBLE_HORIZONTAL);
        }
        builder.append(Characters.BoxDrawing.DOUBLE_RIGHT_T_CHAR).append('\n');
    }

    /**
     * close the box with the last line
     * @param builder
     * @param maxOptionsLength
     */
    private void writeLastLine(StringBuilder builder, int maxOptionsLength)
    {
        builder.append("\t\t\t");
        for(int i = 0; i < maxOptionsLength+8; i++)
        {
            if(i == 0)builder.append(Characters.BoxDrawing.BOTTOM_LEFT_CORNER);
            else if(i == 5)builder.append(Characters.BoxDrawing.DOUBLE_REVERSED_T_CHAR);
            else builder.append(Characters.BoxDrawing.DOUBLE_HORIZONTAL);
        }
        builder.append(Characters.BoxDrawing.BOTTOM_RIGHT_CORNER).append('\n');
    }

    /**
     * writes the line with the options
     * @param builder
     * @param option
     * @param index
     * @param maxOptionsLength
     */
    private void writeOptionLine(StringBuilder builder, String option, int index, int maxOptionsLength)
    {
        builder.append("\t\t\t");
        for(int i = 0; i < maxOptionsLength+8; i++)
        {
            if(i == 0)builder.append(Characters.BoxDrawing.DOUBLE_VERTICAL);
            else if(i == 1 || i == 4 || i == 6)builder.append(' ');
            else if(i == 2)builder.append(index);
            else if(i == 3)builder.append('.');
            else if(i == 5)builder.append(Characters.BoxDrawing.DOUBLE_VERTICAL);
            else if(i-7 < option.length()) builder.append(option.charAt(i-7));
            else builder.append(' ');
        }
        builder.append(Characters.BoxDrawing.DOUBLE_VERTICAL).append('\n');
    }

    /**
     * uses all the methods defined before to build the entire box
     */
    protected void writeOptions()
    {
        int maxOptionsLength = getMaxOptionLength();
        StringBuilder builder = new StringBuilder();

        writeFirstLine(builder, maxOptionsLength);
        writeTitle(builder, "Opzioni", maxOptionsLength);
        writeMidLine(builder, maxOptionsLength);
        options.forEach(option -> writeOptionLine(builder, option.getOption(), option.getIndex(), maxOptionsLength));
        writeLastLine(builder, maxOptionsLength);

        GameConsole.println(builder.toString());
    }

    /**
     *
     * @param response
     * @return
     */
    private Option<T> findOption(String response)
    {
        response = response.trim();
        if(isInteger(response) && Integer.parseInt(response) < options.size())return options.get(Integer.parseInt(response));

        for(Option<T> option : options)
        {
            if(option.getOption().equalsIgnoreCase(response))return option;
        }
        return null;
    }

    private List<Option<T>> findOptions(String[] optionsSelected)
    {
        List<Option<T>> list = new ArrayList<>();

        for(String optionStr : optionsSelected)
        {
            Option<T> option = findOption(optionStr);
            if(option == null)return new ArrayList<>();
            list.add(option);
        }

        return list;
    }

    private boolean isInteger(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }


}
