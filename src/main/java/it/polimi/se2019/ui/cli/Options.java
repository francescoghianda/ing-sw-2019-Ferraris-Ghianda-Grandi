package it.polimi.se2019.ui.cli;


import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.Scanner;

public class Options<T>
{
    private String question;
    private OnOptionSelectedListener listener;
    private ArrayList<Option<T>> options;
    private Scanner scanner;
    private boolean firstDefault;

    public Options(String question, boolean firstDefault)
    {
        this.options = new ArrayList<>();
        this.question = question;
        this.firstDefault = firstDefault;
        scanner = new Scanner(System.in);
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

    public Option<T> show()
    {
        if(options.isEmpty())return null;
        StringBuilder output = new StringBuilder();
        output.append(question).append(" ");
        options.forEach(option -> output.append(option).append(" "));

        Option<T> selected;
        String response;

        do
        {
            Logger.inputCli(output.toString());
            response = scanner.nextLine().trim();
            if(firstDefault && response.isEmpty())
            {
                selected = options.get(0);
                break;
            }
        }
        while ((selected = findOption(response)) == null);

        if(listener != null)listener.onOptionSelected(selected);

        return selected;
    }

    private Option<T> findOption(String response)
    {
        for(Option<T> option : options)
        {
            if(option.compare(response))return option;
        }
        return null;
    }


}