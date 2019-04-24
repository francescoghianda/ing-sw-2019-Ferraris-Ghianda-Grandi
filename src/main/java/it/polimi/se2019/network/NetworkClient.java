package it.polimi.se2019.network;

import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;

public interface NetworkClient
{
    void sendMessageToServer(NetworkMessageServer<?> message);

    String getUsername();

    NetworkMessageClient getResponseTo(NetworkMessageServer<?> messageToServer, NetworkMessageClient<?> currentMessage);
}
