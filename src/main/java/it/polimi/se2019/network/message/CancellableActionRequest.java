package it.polimi.se2019.network.message;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.ui.UI;

import java.io.Serializable;

public class CancellableActionRequest extends Message
{
    private final CancellableActionFunction function;

    CancellableActionRequest(String message, CancellableActionFunction function)
    {
        super(message, Message.Type.REQUEST);
        this.function = function;
    }

    public Serializable apply(UI ui) throws CanceledActionException
    {
        return function.apply(ui);
    }
}
