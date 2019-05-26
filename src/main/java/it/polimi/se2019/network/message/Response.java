package it.polimi.se2019.network.message;

import it.polimi.se2019.network.rmi.client.CallbackInterface;

import java.io.Serializable;

public class Response extends Message
{

    public enum Status
    {
        OK, ACTION_CANCELED
    }

    private final Status status;

    public Response(String message, Serializable content, Status status)
    {
        super(message, content, Type.RESPONSE);
        this.status = status;
    }

    public Status getStatus()
    {
        return this.status;
    }

    public Response setSender(CallbackInterface sender)
    {
        super.setSender(sender);
        return this;
    }
}
