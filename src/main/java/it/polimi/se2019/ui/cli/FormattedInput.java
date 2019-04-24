package it.polimi.se2019.ui.cli;

import it.polimi.se2019.utils.logging.Logger;

import java.util.Scanner;
import java.util.function.Predicate;

public class FormattedInput
{
    public static final String IP_REGEX = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
    public static final String USERNAME_REGEX = "^[a-z0-9_-]{3,10}$";
    public static final String NUMERIC_REGEX = "[0-9]+";

    private String question;
    private String regex;
    private Scanner scanner;
    private Predicate<String> predicate;

    private String defaultResponse;

    private boolean useRegex;
    private boolean usePredicate;

    public FormattedInput(String question, String regex)
    {
        this.question = question;
        this.regex = regex;
        this.useRegex = true;
        this.usePredicate = false;
        this.scanner = new Scanner(System.in);
    }

    public FormattedInput(String question, Predicate<String> predicate)
    {
        this.question = question;
        this.predicate = predicate;
        this.useRegex = false;
        this.usePredicate = true;
        this.scanner = new Scanner(System.in);
    }

    public FormattedInput(String question, String regex, Predicate<String> predicate)
    {
        this.question = question;
        this.predicate = predicate;
        this.regex = regex;
        this.useRegex = true;
        this.usePredicate = true;
        this.scanner = new Scanner(System.in);
    }

    public FormattedInput setDefaultResponse(String defaultResponse)
    {
        this.defaultResponse = defaultResponse;
        return this;
    }

    public String show()
    {
        String response;
        while(true)
        {
            Logger.inputCli(question);
            response = scanner.nextLine();
            if(defaultResponse != null && response.isEmpty())response = defaultResponse;
            boolean matchRegex = useRegex && response.matches(regex);
            boolean matchPredicate = useRegex ? usePredicate && matchRegex && predicate.test(response) : usePredicate && predicate.test(response);
            if(useRegex && usePredicate && matchRegex && matchPredicate || useRegex && !usePredicate && matchRegex || usePredicate && !useRegex && matchPredicate)break;
            Logger.cli(Strings.INVALID_INPUT);
        }
        return response;
    }

}
