package it.polimi.se2019.network;

import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;

public interface NetworkClient
{
    /**
     * Search and create a connection with a server
     * @param serverIp The ip of the server
     * @param serverPort The port of the server
     * @return true in case of succeeded connection
     */
    boolean connect(String serverIp, int serverPort);

    /**
     * Send a message to server
     * @param message The message to send
     */
    void sendMessageToServer(NetworkMessageServer<?> message);

    /**
     * Send a message to the server and wait for a response
     * @param messageToServer Message to send
     * @return The response message
     */
    NetworkMessageClient getResponseTo(NetworkMessageServer<?> messageToServer);
    String getUsername();
    void invalidNickname();
    void setLogged(boolean logged);

    /**
     * Close the connection with the server
     */
    void stop();
}
