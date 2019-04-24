package it.polimi.se2019.network;

import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;

public interface NetworkClient
{
    boolean connect(String serverIp, int serverPort);
    void sendMessageToServer(NetworkMessageServer<?> message);
    NetworkMessageClient getResponseTo(NetworkMessageServer<?> messageToServer, NetworkMessageClient<?> currentMessage);
    String getUsername();
    void stop();
}
