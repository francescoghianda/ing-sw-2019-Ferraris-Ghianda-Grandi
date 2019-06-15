package it.polimi.se2019.network.message;

import it.polimi.se2019.network.rmi.client.CallbackInterface;

import java.io.Serializable;

public abstract class Message implements Serializable
{
    public static final long serialVersionUID = 7L;

    public enum Type
    {
        ASYNC_MESSAGE, REQUEST, RESPONSE
    }

    private CallbackInterface sender;
    private final String contentMessage;
    private final Serializable content;
    private final Type type;

    protected Message(String message, Type type)
    {
        this(message, null, type);
    }

    protected Message(String message, Serializable content, Type type)
    {
        this.contentMessage = message;
        this.content = content;
        this.type = type;
    }

    public Message setSender(CallbackInterface sender)
    {
        this.sender = sender;
        return this;
    }

    public Type getType()
    {
        return this.type;
    }

    public final String getMessage()
    {
        return this.contentMessage;
    }

    public final boolean isContentPresent()
    {
        return content != null;
    }

    public final Serializable getContent()
    {
        return this.content;
    }

    public final CallbackInterface getSender()
    {
        return this.sender;
    }

    @Override
    public String toString()
    {
        return "["+type+"] "+contentMessage;
    }
}
