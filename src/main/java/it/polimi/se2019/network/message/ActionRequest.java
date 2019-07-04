package it.polimi.se2019.network.message;

import it.polimi.se2019.ui.UI;

import java.io.Serializable;

/**
 * defines a request message
 */
public class ActionRequest extends Message
{
    private final ActionFunction function;

    ActionRequest(String message, ActionFunction function)
    {
        super(message, Message.Type.REQUEST);
        this.function = function;
    }

    public Serializable apply(UI ui)
    {
        return function.apply(ui);
    }

}
