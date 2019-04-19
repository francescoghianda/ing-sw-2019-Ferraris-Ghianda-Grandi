package it.polimi.se2019.network.socket.message;


public interface MessageExecutor<T extends NetworkMessage>
{
    void execute(T message);
}
