package it.polimi.se2019.network;

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
}
