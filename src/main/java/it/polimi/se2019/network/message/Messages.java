package it.polimi.se2019.network.message;

import it.polimi.se2019.map.Block;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.logging.Logger;

public class Messages
{
    public static final int OK = 0;
    public static final int INVALID_USER = 1;
    public static final int INVALID_PLAYER = 2;

    private Messages(){}


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////MESSAGES EXECUTED ON CLIENT///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final NetworkMessageClient<Void> LOGIN_REQUEST = new NetworkMessageClient<>(message ->
    {
        int stateMessage;
        do
        {
            String username = message.getClient().getUsername();
            stateMessage = (Integer)message.getClient().getResponseTo(Messages.LOGIN_RESPONSE.setParam(username)).getParam();
            if(stateMessage != OK)message.getClient().invalidNickname();
        }
        while (stateMessage != OK);
        message.getClient().setLogged(true);
    });


    public static final NetworkMessageClient<Integer> STATE_MESSAGE = new NetworkMessageClient<>();

    public static final NetworkMessageClient<Void> YOU_ARE_FIRST_PLAYER = new NetworkMessageClient<>(message ->
            message.getClient().getUI().youAreFirstPlayer());

    public static final NetworkMessageClient<String> FIRST_PLAYER_IS = new NetworkMessageClient<>(message ->
            message.getClient().getUI().firstPlayerIs(message.getParam()));

    public static final NetworkMessageClient<Void> SELECT_PLAYER = new NetworkMessageClient<>(message ->
    {
        NetworkMessageClient<?> stateMessage;
        do
        {
            Player selectedPlayer = message.getClient().getUI().selectPlayer();
            stateMessage = message.getClient().getResponseTo(Messages.SELECT_PLAYER_RESPONSE.setParam(selectedPlayer));
        }
        while ((Integer)stateMessage.getParam() != OK);

    });

    public static final NetworkMessageClient<Void> GAME_IS_STARTING = new NetworkMessageClient<>(message ->
            message.getClient().getUI().gameIsStarting());

    public static final NetworkMessageClient<Void> GAME_IS_STARTED = new NetworkMessageClient<>(message ->
            message.getClient().getUI().gameStarted());

    public static final NetworkMessageClient<Integer> TIMER_SECONDS = new NetworkMessageClient<>(message ->
            message.getClient().getUI().showTimerCountdown(message.getParam()));


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////MESSAGES EXECUTED ON SERVER///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final NetworkMessageServer<Player> SELECT_PLAYER_RESPONSE = new NetworkMessageServer<>();

    public static final NetworkMessageServer<String> LOGIN_RESPONSE = new NetworkMessageServer<>(message ->
    {

        if(ClientsManager.getInstance().getConnectedClientsUsername().contains(message.getParam()))
        {
            message.getClientConnection().sendMessageToClient(Messages.STATE_MESSAGE.setParam(INVALID_USER));
        }
        else
        {
            message.getClientConnection().sendMessageToClient(Messages.STATE_MESSAGE.setParam(OK));
            message.getClientConnection().setUsername(message.param);
            message.getClientConnection().setLogged(true);
            if(ClientsManager.getInstance().getDisconnectedClientsUsername().contains(message.getParam()))
            {

                message.getClientConnection().getServer().clientReconnected(message.getClientConnection());
                Logger.warning("Client "+message.param+" has reconnected!");
            }
        }

    });

    public static final NetworkMessageServer<Block> MOVE_PLAYER = new NetworkMessageServer<>(message ->
            message.getClientConnection().getGameController().movePlayer(message.getClientConnection().getPlayer(), message.getParam()));


}
