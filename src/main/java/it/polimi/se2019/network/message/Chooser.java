package it.polimi.se2019.network.message;

import it.polimi.se2019.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chooser
{
    private ArrayList<String> options;
    private String question;

    public Chooser(String question, List<String> options)
    {
        this.options = (ArrayList<String>) options;
        this.question = question;
    }

    public Chooser(String question, String... options)
    {
        this.options = new ArrayList<>(Arrays.asList(options));
        this.question = question;
    }

    public void addAll(List<String> options)
    {
        this.options.addAll(options);
    }

    public void add(String option)
    {
        this.options.add(option);
    }

    public String getResponse(Player player)
    {
        return (String) player.getResponseTo(Messages.CHOOSER_MESSAGE.setParam(new Bundle<>(question, options))).getParam();
    }

}
