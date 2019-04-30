package it.polimi.se2019.network.message;


import java.io.Serializable;

public interface MessageExecutor<T extends NetworkMessage> extends Serializable
{
    void execute(T message);
}