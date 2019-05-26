package it.polimi.se2019.network.message;

import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.ui.UI;

import java.io.Serializable;

public class AsyncMessage extends Message implements Serializable
{
    private SerializableConsumer consumer;

    public AsyncMessage(String message, SerializableConsumer consumer)
    {
        super(message, Type.ASYNC_MESSAGE);
        this.consumer = consumer;
    }

    public AsyncMessage(String message, Serializable content, SerializableConsumer consumer)
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