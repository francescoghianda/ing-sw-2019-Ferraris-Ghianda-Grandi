package it.polimi.se2019.network.message;

import it.polimi.se2019.utils.logging.Logger;

public class Messages
{
    private static final int OK = 0;
    private static final int INVALID_USER = 1;

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
            stateMessage = (Integer)message.getClient().getResponseTo(Messages.LOGIN_RESPONSE.setParam(username)).param;
            if(stateMessage != OK)message.getClient().invalidNickname();
        }
        while (stateMessage != OK);
        message.getClient().setLogged(true);
    });


    public static final NetworkMessageClient<Integer> STATE_MESSAGE = new NetworkMessageClient<>(message -> {});

    public static final NetworkMessageClient<Void> YOU_ARE_FIRST_PLAYER = new NetworkMessageClient<>(message ->
    {
        //TODO
    });


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////MESSAGES EXECUTED ON SERVER///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final NetworkMessageServer<Block> MOVE_PLAYER = new NetworkMessageServer<>(message ->
            message.getClientConnection().getGameController().movePlayer(message.getClientConnection().getPlayer(), message.param));


    public static final NetworkMessageServer<String> LOGIN_RESPONSE = new NetworkMessageServer<>(message ->
    {

        if(message.getClientConnection().getServer().getConnectedClientsUsername().contains(message.getParam()))
        {
            message.getClientConnection().sendMessageToClient(Messages.STATE_MESSAGE.setParam(INVALID_USER));
        }
        else
        {
            message.getClientConnection().sendMessageToClient(Messages.STATE_MESSAGE.setParam(OK));
            message.getClientConnection().setUsername(message.param);
            message.getClientConnection().setLogged(true);
            if(message.getClientConnection().getServer().getDisconnectedClientsUsername().contains(message.getParam()))
            {
                message.getClientConnection().getServer().clientReconnected(message.getParam(), message.getSender());
                Logger.warning("Client "+message.param+" has reconnected!");
            }

        }

    });


}
