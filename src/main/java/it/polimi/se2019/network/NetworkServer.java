package it.polimi.se2019.network;

import it.polimi.se2019.network.message.NetworkMessageClient;

public interface NetworkServer
{
    void sendMessageToClient(NetworkMessageClient<?> message);
}
