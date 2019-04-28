package it.polimi.se2019.network;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.player.Player;

public interface ClientConnectionInterface
{
    void notifyOtherClients(NetworkMessageClient<?> message);
    void sendMessageToClient(NetworkMessageClient<?> message);
    NetworkMessageServer getResponseTo(NetworkMessageClient<?> messageToClient);

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
