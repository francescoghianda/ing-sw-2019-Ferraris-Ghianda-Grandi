package it.polimi.se2019.network;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.message.AsyncMessage;
import it.polimi.se2019.network.message.Message;
import it.polimi.se2019.network.message.Request;
import it.polimi.se2019.network.message.Response;
import it.polimi.se2019.player.Player;

public interface ClientConnection
{

    /**
     *  Send a message to all the clients connected, except this
     * @param message The message to send
     */
    void notifyOtherClients(AsyncMessage message);

    /**
     *  Send a message to the client
     * @param message The message to send
     */
    void sendMessageToClient(AsyncMessage message);

    /**
     * Send a message to the client and wait for a response
     * @param request The message to send
     * @return The response message
     */
    Response getResponseTo(Request request);

    /**
     * Close the connection with the client
     */
    void stop();
    NetworkServer getServer();
    void setUsername(String username);
    String getUsername();
    void setLogged(boolean logged);
    boolean isLogged();
    Player getPlayer();
    void setPlayer(Player player);
    GameController getGameController();
}
