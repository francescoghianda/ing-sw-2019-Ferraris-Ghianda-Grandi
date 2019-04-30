package it.polimi.se2019.network.message;

import it.polimi.se2019.network.NetworkClient;

import java.io.Serializable;

/**
 * Network message that can be executed by a client
 * @param <T> The type of the parameter
 */
public class NetworkMessageClient<T> extends NetworkMessage<T> implements Serializable
{
    private final MessageExecutor<NetworkMessageClient<T>> executor;
    private transient NetworkClient client;

    /**
     * Create a message without a parameter
     * @param executor The executor of the message
     */
    public NetworkMessageClient(MessageExecutor<NetworkMessageClient<T>> executor)
    {
        this.executor = executor;
    }

    @Override
    public NetworkMessageClient<T> setParam(T param)
    {
        NetworkMessageClient<T> nmc = this.clone();
        nmc.param = param;
        return nmc;
    }

    /**
     * @param client The client that execute the message
     * @return
     */
    public NetworkMessageClient<T> setClient(NetworkClient client)
    {
        this.client = client;
        return this;
    }

    public NetworkClient getClient()
    {
        return this.client;
    }

    public MessageExecutor<NetworkMessageClient<T>> getExecutor()
    {
        return this.executor;
    }

    @Override
    public void execute()
    {
        getExecutor().execute(this);
    }

    @Override
    public NetworkMessageClient<T> clone()
    {
        NetworkMessageClient<T> cloned = new NetworkMessageClient<>(executor);
        cloned.param = param;
        return cloned;
    }
}
