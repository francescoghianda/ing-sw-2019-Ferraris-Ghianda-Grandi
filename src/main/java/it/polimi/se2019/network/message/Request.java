package it.polimi.se2019.network.message;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.ui.UI;

import java.io.Serializable;

public class Request extends Message
{
    private final SerializableFunction function;

    public Request(String message, SerializableFunction function)
    {
        super(message, Type.REQUEST);
        this.function = function;
    }

    public Serializable apply(UI ui) throws CanceledActionException
    {
        return function.apply(ui);
    }
}
