package it.polimi.se2019.network.message;

import java.io.Serializable;

class NetworkMessage<T> implements Serializable
{
    T param;

    NetworkMessage()
    {

    }

    NetworkMessage(T param)
    {
        this.param = param;
    }

    public NetworkMessage<T> setParam(T param)
    {
        NetworkMessage<T> nm = this.clone();
        nm.param = param;
        return nm;
    }

    public T getParam()
    {
        return param;
    }

    public NetworkMessage<T> clone()
    {
        return new NetworkMessage<>(param);
    }

    public void execute()
    {

    }
}
