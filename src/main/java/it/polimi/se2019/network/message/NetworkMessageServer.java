package it.polimi.se2019.network.message;

import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.rmi.client.CallbackInterface;

import java.io.Serializable;

/**
 * NetworkMessage that can be executed by a server
 * @param <T> The type of the parameter in the message
 */
public class NetworkMessageServer<T> extends NetworkMessage<T> implements Serializable
{
    private final MessageExecutor<NetworkMessageServer<T>> executor;
    private transient ClientConnection clientConnection;
    private CallbackInterface sender;

    /**
     * Create a message without a parameter
     * @param executor The executor of the message
     */
    public NetworkMessageServer(MessageExecutor<NetworkMessageServer<T>> executor)
    {
        this.executor = executor;
    }

    public NetworkMessageServer()
    {
        this.executor = message -> {};
    }

    @Override
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

    public NetworkMessageServer<T> setClientConnection(ClientConnection connection)
    {
        this.clientConnection = connection;
        return this;
    }

    public ClientConnection getClientConnection()
    {
        return this.clientConnection;
    }

    @Override
    public void execute()
    {
        getExecutor().execute(this);
    }

    @Override
    public NetworkMessageServer<T> clone()
    {
        NetworkMessageServer<T> cloned = new NetworkMessageServer<>(executor);
        cloned.param = param;
        cloned.sender = sender;
        return cloned;
    }
}
