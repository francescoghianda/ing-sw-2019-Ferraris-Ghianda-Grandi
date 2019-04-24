package it.polimi.se2019.network;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.player.Player;

public interface NetworkServer
{
    void sendMessageToClient(NetworkMessageClient<?> message);
    void sendBroadcastMessage(NetworkMessageClient<?> message);

    Player getPlayer(CallbackInterface client);
    GameController getGameController();
}
