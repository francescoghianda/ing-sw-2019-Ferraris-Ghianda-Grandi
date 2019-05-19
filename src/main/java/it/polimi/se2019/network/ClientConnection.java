package it.polimi.se2019.network;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.player.Player;

public interface ClientConnection
{

    /**
     *  Send a message to all the clients connected, except this
     * @param message The message to send
     */
    void notifyOtherClients(NetworkMessageClient<?> message);

    /**
     *  Send a message to the client
     * @param message The message to send
     */
    void sendMessageToClient(NetworkMessageClient<?> message);

    /**
     * Send a message to the client and wait for a response
     * @param messageToClient The message to send
     * @return The response message
     */
    NetworkMessageServer getResponseTo(NetworkMessageClient<?> messageToClient);

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
