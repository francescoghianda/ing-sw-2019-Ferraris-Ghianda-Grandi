package it.polimi.se2019.network.message;


import java.io.Serializable;

public interface MessageExecutor<T extends NetworkMessage> extends Serializable
{
    /**
     * The method is invoked by the server or the client when the message in received
     * @param message The message that contains the executor
     */
    void execute(T message);
}
