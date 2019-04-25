package it.polimi.se2019.network;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.player.Player;

import java.util.List;

public interface ClientConnectionInterface
{
    void sendMessageToClient(NetworkMessageClient<?> message);
    void sendBroadcastMessage(NetworkMessageClient<?> message);

    void setNickname(String nickname, CallbackInterface client);
    String getNickname(CallbackInterface client);
    void setLogged(boolean logged, CallbackInterface client);
    boolean isLogged(CallbackInterface client);
    Player getPlayer(CallbackInterface client);
    List<String> getNicknames();
    GameController getGameController();
}
