package it.polimi.se2019.network.message;

import it.polimi.se2019.map.Block;

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
            String username = message.getClient().getNickname();
            stateMessage = (Integer)message.getClient().getResponseTo(Messages.LOGIN_RESPONSE.setParam(username), message).param;
            if(stateMessage != OK)message.getClient().invalidNickname();
        }
        while (stateMessage != OK);
        message.getClient().setLogged(true);
    });


    public static final NetworkMessageClient<Integer> STATE_MESSAGE = new NetworkMessageClient<>(message -> {});


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////MESSAGES EXECUTED ON SERVER///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final NetworkMessageServer<Block> MOVE_PLAYER = new NetworkMessageServer<>(message ->
            message.getServer().getGameController().movePlayer(message.getServer().getPlayer(message.getSender()), message.param));


    public static final NetworkMessageServer<String> LOGIN_RESPONSE = new NetworkMessageServer<>(message ->
    {
        NetworkMessageClient<Integer> stateMessage = Messages.STATE_MESSAGE.setRecipient(message.getSender());

        if(message.getServer().getNicknames().contains(message.param))
        {
            message.getServer().sendMessageToClient(stateMessage.setParam(INVALID_USER));
        }
        else
        {
            message.getServer().sendMessageToClient(stateMessage.setParam(OK));
            message.getServer().setNickname(message.param, message.getSender());
            message.getServer().setLogged(true, message.getSender());
        }

    });


}
