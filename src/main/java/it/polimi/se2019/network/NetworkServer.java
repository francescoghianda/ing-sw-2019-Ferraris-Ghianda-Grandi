package it.polimi.se2019.network;

import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.rmi.client.CallbackInterface;

import java.util.List;

public interface NetworkServer
{
    void startServer(int port);
    void stopServer();

    List<String> getConnectedClientsUsername();
    List<String> getDisconnectedClientsUsername();
    void clientReconnected(String username, CallbackInterface client);
    void sendBroadcastMessage(NetworkMessageClient<?> message);

}
