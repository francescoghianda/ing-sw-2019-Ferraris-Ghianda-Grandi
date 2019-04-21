package it.polimi.se2019.network.message;

import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.rmi.client.CallbackInterface;

import java.io.Serializable;

public class NetworkMessageServer<T> extends NetworkMessage<T> implements Serializable
{
    private final MessageExecutor<NetworkMessageServer<T>> executor;
    private NetworkServer clientConnection;

    private CallbackInterface sender;

    public NetworkMessageServer(MessageExecutor<NetworkMessageServer<T>> executor)
    {
        this.executor = executor;
    }

    public NetworkMessageServer<T> setParam(T param)
    {
        NetworkMessageServer<T> nms = this.clone();
        nms.param = param;
        return nms;
    }

    public NetworkMessageServer<T> setSender(CallbackInterface sender)
    {
        NetworkMessageServer<T> nms = this.clone();
        nms.sender = sender;
        return nms;
    }

    public CallbackInterface getSender()
    {
        return this.sender;
    }

    public MessageExecutor<NetworkMessageServer<T>> getExecutor()
    {
        return this.executor;
    }

    public NetworkMessageServer<T> setClientConnection(NetworkServer connection)
    {
        this.clientConnection = connection;
        return this;
    }

    public NetworkServer getServer()
    {
        return this.clientConnection;
    }

    public void execute()
    {
        getExecutor().execute(this);
    }

    public NetworkMessageServer<T> clone()
    {
        NetworkMessageServer<T> cloned = new NetworkMessageServer<>(executor);
        cloned.param = param;
        cloned.sender = sender;
        return cloned;
    }
}
