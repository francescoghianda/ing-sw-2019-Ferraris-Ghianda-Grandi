package it.polimi.se2019.network;

import it.polimi.se2019.ui.UI;

public interface NetworkClient
{
    /**
     * Search and create a connection with a server
     * @param serverIp The ip of the server
     * @param serverPort The port of the server
     * @return true in case of succeeded connection
     */
    boolean connect(String serverIp, int serverPort);


    String getUsername();
    void invalidNickname();
    void setLogged(boolean logged);
    UI getUI();

    /**
     * Close the connection with the server
     */
    void stop();
}
