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
    private final String requestId;

    public Response(String message, String requestId, Serializable content, Status status)
    {
        super(message, content, Type.RESPONSE);
        this.status = status;
        this.requestId = requestId;
    }

    public Status getStatus()
    {
        return this.status;
    }

    public String getRequestId()
    {
        return requestId;
    }

    public Response setSender(CallbackInterface sender)
    {
        super.setSender(sender);
        return this;
    }
}
