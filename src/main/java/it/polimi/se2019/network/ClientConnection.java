package it.polimi.se2019.network;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.controller.Match;
import it.polimi.se2019.network.message.*;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.player.VirtualView;

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
    Response getResponseTo(CancellableActionRequest request) throws CanceledActionException;

    Response getResponseTo(ActionRequest request);

    /**
     * Close the connection with the client
     */
    void stop();
    void setLogged(boolean logged, boolean reconnected);
    void setConnected(boolean connected);
    boolean isLogged();
    boolean isConnected();
    NetworkServer getServer();
    User getUser();
    VirtualView getVirtualView();
    GameController getGameController();
    Match getMatch();
}
