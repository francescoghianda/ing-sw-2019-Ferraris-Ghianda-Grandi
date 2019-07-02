package it.polimi.se2019.ui.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OptionList<T>
{
    private List<Option<T>> options;

    private OptionList()
    {
        options = new ArrayList<>();
    }

    private OptionList(List<Option<T>> options)
    {
        options = new ArrayList<>(options);
    }

    private OptionList<T> add(Option<T> option)
    {
        options.add(option);
        return this;
    }

    static <T> OptionList<T> of(List<Option<T>> options)
    {
        return new OptionList<>(options);
    }

    static <T> OptionList<T> of(Option<T> option)
    {
        OptionList<T> optionList = new OptionList<>();
        return optionList.add(option);
    }

    static <T> OptionList<T> empty()
    {
        return new OptionList<>();
    }


    List<Option<T>> options()
    {
        return new ArrayList<>(options);
    }

    ArrayList<T> values()
    {
        return options.stream().map(Option::getValue).collect(Collectors.toCollection(ArrayList::new));
    }
}
