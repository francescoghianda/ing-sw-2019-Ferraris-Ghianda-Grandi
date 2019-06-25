package it.polimi.se2019.card.cardscript.command.parameter;

import it.polimi.se2019.card.cardscript.Executor;

import java.util.Arrays;
import java.util.List;

public class EnumParameter extends ParameterType<String>
{

    private List<String> values;

    public EnumParameter(String[] values)
    {
        this.values = Arrays.asList(values);
    }

    @Override
    String cast(String parameter, Executor executor) throws ParameterCastException
    {
        if(values.contains(parameter))return parameter;
        throw new ParameterCastException();
    }
}
