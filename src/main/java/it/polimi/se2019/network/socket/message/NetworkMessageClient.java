package it.polimi.se2019.network.socket.message;

import it.polimi.se2019.network.socket.client.Client;

import java.io.Serializable;

public class NetworkMessageClient<T> extends NetworkMessage<T> implements Serializable
{
    private MessageExecutor<NetworkMessageClient<T>> executor;
    private Client client;

    public NetworkMessageClient setParam(T param)
    {
        this.param = param;
        return this;
    }

    public NetworkMessageClient<T> setClient(Client client)
    {
        this.client = client;
        return this;
    }

    public Client getClient()
    {
        return this.client;
    }

    public NetworkMessageClient<T> setExecutor(MessageExecutor<NetworkMessageClient<T>> executor)
    {
        this.executor = executor;
        return this;
    }

    public MessageExecutor<NetworkMessageClient<T>> getExecutor()
    {
        return this.executor;
    }

    public void execute()
    {
        getExecutor().execute(this);
    }
}
