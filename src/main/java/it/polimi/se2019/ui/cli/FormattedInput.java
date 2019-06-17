package it.polimi.se2019.ui.cli;

import it.polimi.se2019.utils.logging.Logger;

import java.util.Scanner;
import java.util.function.Predicate;

/**
 * receive a question , a regular expression/a predicate
 */
public class FormattedInput
{
    public static final String USERNAME_REGEX = "^[a-z0-9_-]{3,10}$";
    public static final String NUMERIC_REGEX = "[0-9]+";

    private String question;
    private String regex;
    private CancelableReader reader;
    private Predicate<String> predicate;

    private String defaultResponse;

    private boolean useRegex;
    private boolean usePredicate;

    /**
     *
     * @param question
     * @param regex
     */
    public FormattedInput(String question, String regex)
    {
        this.question = question;
        this.regex = regex;
        this.useRegex = true;
        this.usePredicate = false;
        this.reader = new CancelableReader(System.in);
    }

    /**
     *
     * @param question
     * @param predicate
     */
    public FormattedInput(String question, Predicate<String> predicate)
    {
        this.question = question;
        this.predicate = predicate;
        this.useRegex = false;
        this.usePredicate = true;
        this.reader = new CancelableReader(System.in);
    }

    /**
     *
     * @param question
     * @param regex
     * @param predicate
     */
    public FormattedInput(String question, String regex, Predicate<String> predicate)
    {
        this.question = question;
        this.predicate = predicate;
        this.regex = regex;
        this.useRegex = true;
        this.usePredicate = true;
        this.reader = new CancelableReader(System.in);
    }

    public FormattedInput setDefaultResponse(String defaultResponse)
    {
        this.defaultResponse = defaultResponse;
        return this;
    }

    public String show() throws CanceledInputException
    {
        String response;
        while(true)
        {
            GameConsole.out.print(question);
            response = reader.nextLine();
            if(defaultResponse != null && response.isEmpty())response = defaultResponse;
            boolean matchRegex = useRegex && response.matches(regex);
            boolean matchPredicate = useRegex ? usePredicate && matchRegex && predicate.test(response) : usePredicate && predicate.test(response);
            if(useRegex && usePredicate && matchRegex && matchPredicate || useRegex && !usePredicate && matchRegex || usePredicate && !useRegex && matchPredicate)break;
            GameConsole.out.println(CliString.INVALID_INPUT);
        }
        return response;
    }

}
