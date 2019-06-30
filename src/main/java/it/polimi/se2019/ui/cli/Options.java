package it.polimi.se2019.ui.cli;


import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.TimeOutException;
import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * receives a question and some options of the group of options
 * @param <T>
 */
public final class Options<T>
{
    private String question;
    private OnOptionSelectedListener listener;
    private ArrayList<Option<T>> options;
    private CancelableReader reader;
    private boolean firstDefault;
    private Option<T> cancelOption;

    public Options(String question, boolean firstDefault)
    {
        this.options = new ArrayList<>();
        this.question = question;
        this.firstDefault = firstDefault;
        reader= CancelableReader.createNew(System.in);
    }

    public Options setOptionListener(OnOptionSelectedListener listener)
    {
        this.listener = listener;
        return this;
    }

    public Options<T> addOption(Option<T> option)
    {
        options.add(option);
        return this;
    }

    public Options addCancelOption()
    {
        cancelOption = new Option<>("Annulla", "A");
        options.add(cancelOption);
        return this;
    }

    public Options addOption(String option, String abbreviation)
    {
        options.add(new Option<>(option, abbreviation));
        return this;
    }

    public Options<T> addOption(String option, String abbreviation, T value)
    {
        options.add(new Option<>(option, abbreviation, value));
        return this;
    }


    private Option<T> showOptions()
    {
        if(options.isEmpty())return null;
        StringBuilder output = new StringBuilder();
        output.append(question).append(" ");
        options.forEach(option -> output.append(option).append(" "));

        Option<T> selected;
        String response;

        do
        {
            try
            {
                GameConsole.out.print(output.toString());
                response = reader.nextLine().trim();
            }
            catch (CanceledInputException e)
            {
                throw new TimeOutException();
            }

            if(firstDefault && response.isEmpty())
            {
                selected = options.get(0);
                break;
            }
        }
        while ((selected = findOption(response)) == null);

        return selected;
    }

    /**
     *
     * @return
     */
    public Option<T> show() throws TimeOutException
    {
        Option<T> selected = showOptions();
        if(listener != null)listener.onOptionSelected(selected);
        return selected;
    }

    public Option<T> showCancellable() throws TimeOutException, CanceledActionException
    {
        Option<T> selected = showOptions();
        if(selected != null && selected.equals(cancelOption))throw new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER);
        if(selected != null && listener != null)listener.onOptionSelected(selected);
        return selected;
    }

    /**
     *
     * @param response
     * @return
     */
    private Option<T> findOption(String response)
    {
        for(Option<T> option : options)
        {
            if(option.compare(response))return option;
        }
        return null;
    }


}
