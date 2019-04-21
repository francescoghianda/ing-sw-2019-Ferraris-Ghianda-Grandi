package it.polimi.se2019.network.socket.message;

import it.polimi.se2019.network.socket.server.ClientConnection;

import java.io.Serializable;

public class NetworkMessageServer<T> extends NetworkMessage<T> implements Serializable
{
    private final MessageExecutor<NetworkMessageServer<T>> executor;
    private ClientConnection clientConnection;

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

    public void execute()
    {
        getExecutor().execute(this);
    }

    public NetworkMessageServer<T> clone()
    {
        return new NetworkMessageServer<>(executor);
    }
}
