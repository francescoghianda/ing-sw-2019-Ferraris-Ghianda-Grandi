package it.polimi.se2019.network;

import it.polimi.se2019.ui.UI;

public interface NetworkClient
{
    /**
     * Searches for a connection and creates a connection with the server
     * @param serverIp The ip of the server
     * @param serverPort The port of the server
     * @return true in case of succeeded connection
     */
    boolean connect(String serverIp, int serverPort);

    String getUsername();

    void setLogged(boolean logged);

    /**
     *
     * @return the UI of the client
     */
    UI getUI();

    /**
     * Closes the connection with the server
     */
    void stop();
}
