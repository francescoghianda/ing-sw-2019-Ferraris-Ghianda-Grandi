package it.polimi.se2019.network.message;

import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.ui.GameEvent;
import it.polimi.se2019.utils.logging.Logger;
import it.polimi.se2019.utils.network.SerializableVoid;

import java.util.ArrayList;


public class Messages
{
    public static final int OK = 0;
    public static final int INVALID_USER = 1;
    public static final int INVALID_PLAYER = 2;
    public static final int INVALID_BLOCK = 3;

    private Messages(){}


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////MESSAGES EXECUTED ON CLIENT///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final NetworkMessageClient<SerializableVoid> LOGIN_REQUEST = new NetworkMessageClient<>(message ->
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


    public static final NetworkMessageClient<Integer> STATE_MESSAGE_CLIENT = new NetworkMessageClient<>();

    public static final NetworkMessageClient<SerializableVoid> YOU_ARE_FIRST_PLAYER = new NetworkMessageClient<>(message ->
            message.getClient().getUI().youAreFirstPlayer());

    public static final NetworkMessageClient<String> FIRST_PLAYER_IS = new NetworkMessageClient<>(message ->
            message.getClient().getUI().firstPlayerIs(message.getParam()));

    public static final NetworkMessageClient<SerializableVoid> SELECT_PLAYER = new NetworkMessageClient<>(message ->
    {
        NetworkMessageClient<?> stateMessage;
        do
        {
            String selectedPlayer = message.getClient().getUI().selectPlayer();
            stateMessage = message.getClient().getResponseTo(Messages.SELECT_PLAYER_RESPONSE.setParam(selectedPlayer));
        }
        while ((Integer)stateMessage.getParam() != OK);

    });

    public static final NetworkMessageClient<SerializableVoid> SELECT_BLOCK = new NetworkMessageClient<>(message ->
    {
        NetworkMessageClient<?> stateMessage;
        do
        {
            String selectedBlock = message.getClient().getUI().selectBlock();
            stateMessage = message.getClient().getResponseTo(Messages.SELECT_BLOCK_RESPONSE.setParam(selectedBlock));
        }
        while ((Integer)stateMessage.getParam() != OK);

    });

    public static final NetworkMessageClient<SerializableVoid> GAME_IS_STARTING = new NetworkMessageClient<>(message ->
            message.getClient().getUI().gameIsStarting());

    public static final NetworkMessageClient<SerializableVoid> GAME_IS_STARTED = new NetworkMessageClient<>(message ->
            message.getClient().getUI().gameStarted());

    public static final NetworkMessageClient<Integer> TIMER_SECONDS = new NetworkMessageClient<>(message ->
            message.getClient().getUI().showTimerCountdown(message.getParam()));

    public static final NetworkMessageClient<Bundle<String, ArrayList<String>>> CHOOSER_MESSAGE = new NetworkMessageClient<>(message ->
    {
        String res = message.getClient().getUI().choose(message.getParam());
        message.getClient().sendMessageToServer(Messages.CHOOSER_RESPONSE.setParam(res));
    });

    public static final NetworkMessageClient<Bundle<PowerUpCard, PowerUpCard>> CHOOSE_SPAWN_POINT = new NetworkMessageClient<>(message ->
    {
        PowerUpCard chosen = message.getClient().getUI().chooseSpawnPoint(message.getParam().getFirst(), message.getParam().getSecond());
        message.getClient().sendMessageToServer(Messages.CHOSEN_SPAWN_POINT.setParam(chosen));
    });

    public static final NetworkMessageClient<SerializableVoid> IS_YOUR_ROUND = new NetworkMessageClient<>(message ->
    {
        message.getClient().getUI().handle(new GameEvent(GameEvent.IS_YOUR_ROUND));
        message.getClient().sendMessageToServer(Messages.STATE_MESSAGE_SERVER.setParam(OK));
    });




    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////MESSAGES EXECUTED ON SERVER///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final NetworkMessageServer<Integer> STATE_MESSAGE_SERVER = new NetworkMessageServer<>();

    public static final NetworkMessageServer<PowerUpCard> CHOSEN_SPAWN_POINT = new NetworkMessageServer<>();

    public static final NetworkMessageServer<String> CHOOSER_RESPONSE = new NetworkMessageServer<>();

    public static final NetworkMessageServer<String> SELECT_PLAYER_RESPONSE = new NetworkMessageServer<>();

    public static final NetworkMessageServer<String> SELECT_BLOCK_RESPONSE = new NetworkMessageServer<>();

    public static final NetworkMessageServer<String> LOGIN_RESPONSE = new NetworkMessageServer<>(message ->
    {
        if(ClientsManager.getInstance().getConnectedClientsUsername().contains((String) message.getParam()))
        {
            message.getClientConnection().sendMessageToClient(Messages.STATE_MESSAGE_CLIENT.setParam(INVALID_USER));
        }
        else
        {
            message.getClientConnection().sendMessageToClient(Messages.STATE_MESSAGE_CLIENT.setParam(OK));
            message.getClientConnection().setUsername(message.param);
            message.getClientConnection().setLogged(true);
            if(ClientsManager.getInstance().getDisconnectedClientsUsername().contains((String) message.getParam()))
            {

                message.getClientConnection().getServer().clientReconnected(message.getClientConnection());
                Logger.warning("Client "+message.param+" has reconnected!");
            }
        }

    });

    /*public static final NetworkMessageServer<String> MOVE_PLAYER = new NetworkMessageServer<>(message ->
            message.getClientConnection().getGameController().movePlayer(message.getClientConnection().getPlayer(), message.getParam()));*/

}
