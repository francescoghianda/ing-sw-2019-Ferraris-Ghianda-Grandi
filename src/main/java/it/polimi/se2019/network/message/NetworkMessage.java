package it.polimi.se2019.network.message;

import java.io.Serializable;

class NetworkMessage<T> implements Serializable
{
    T param;

    NetworkMessage()
    {

    }

    public T getParam()
    {
        return param;
    }
}
