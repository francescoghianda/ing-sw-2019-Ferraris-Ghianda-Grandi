package it.polimi.se2019.network.message;

import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.ui.UI;

import java.io.Serializable;

/**
 * defines an async message (message that doesn't have a response
 */
public class AsyncMessage extends Message implements Serializable
{
    private ActionConsumer consumer;

    public AsyncMessage(String message, ActionConsumer consumer)
    {
        super(message, Type.ASYNC_MESSAGE);
        this.consumer = consumer;
    }

    public AsyncMessage(String message, Serializable content, ActionConsumer consumer)
    {
        super(message, content, Type.ASYNC_MESSAGE);
        this.consumer = consumer;
    }

    public void accept(UI ui)
    {
        consumer.accept(ui);
    }

    public AsyncMessage setSender(CallbackInterface sender)
    {
        super.setSender(sender);
        return this;
    }
}
