package it.polimi.se2019.network;

import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.rmi.client.CallbackInterface;

import java.util.List;

public interface NetworkServer
{
    /**
     * Start the server on the localhost at the specified port
     * @param port The port where the server open the connection
     */
    void startServer(int port);

    /**
     * Close all the connection with the clients and stop the server
     */
    void stopServer();

    /**
     * @return All the username of the connected clients
     */
    List<String> getConnectedClientsUsername();

    /**
     * @return All the username of the client that has lost the connection
     */
    List<String> getDisconnectedClientsUsername();

    /**
     * Restore the data of the client that reconnect after has lost the connection
     * @param username The username of the client
     * @param client The callback of the client
     */
    void clientReconnected(String username, CallbackInterface client);
    void sendBroadcastMessage(NetworkMessageClient<?> message);

}
