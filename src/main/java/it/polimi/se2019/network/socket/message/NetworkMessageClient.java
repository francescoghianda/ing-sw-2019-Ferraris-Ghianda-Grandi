package it.polimi.se2019.network.socket.message;

import it.polimi.se2019.network.socket.client.Client;

import java.io.Serializable;

public class NetworkMessageClient<T> extends NetworkMessage<T> implements Serializable
{
    private final MessageExecutor<NetworkMessageClient<T>> executor;
    private Client client;

    public NetworkMessageClient(MessageExecutor<NetworkMessageClient<T>> executor)
    {
        this.executor = executor;
    }

    public NetworkMessageClient<T> setParam(T param)
    {
        NetworkMessageClient<T> nmc = this.clone();
        nmc.param = param;
        return nmc;
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

    public MessageExecutor<NetworkMessageClient<T>> getExecutor()
    {
        return this.executor;
    }

    public void execute()
    {
        getExecutor().execute(this);
    }

    @Override
    public NetworkMessageClient<T> clone()
    {
        return new NetworkMessageClient<>(executor);
    }
}
