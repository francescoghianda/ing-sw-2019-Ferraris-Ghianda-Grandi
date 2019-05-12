package it.polimi.se2019.network.message;

import java.io.Serializable;

class NetworkMessage<T extends Serializable> implements Serializable
{
    T param;

    NetworkMessage()
    {

    }

    NetworkMessage(T param)
    {
        this.param = param;
    }

    /**
     * Set the parameter of the message on the cloned instance of the message
     * @param param The parameter for the message
     * @return The cloned message with the parameter
     */
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
