package it.polimi.se2019.network.message;

import it.polimi.se2019.map.Block;
import it.polimi.se2019.utils.logging.Logger;

import java.util.Scanner;

public class Messages
{
    private Messages(){}

    public static final NetworkMessageClient<String> CHAT_MESSAGE = new NetworkMessageClient<>(
            message ->
            {
                Logger.info("Chat message: "+message.param);
                Scanner s = new Scanner(System.in);
                message.getClient().sendMessageToServer(Messages.CHAT_MESSAGE_SERVER.setParam(s.nextLine()));
            });

    public static final NetworkMessageServer<String> CHAT_MESSAGE_SERVER = new NetworkMessageServer<>(message ->
    {
        Logger.warning(message.param);
        Scanner s = new Scanner(System.in);
        message.getServer().sendMessageToClient(Messages.CHAT_MESSAGE.setParam(s.nextLine()).setRecipient(message.getSender()));
    });

    public static final NetworkMessageServer<String> LOGIN_RESPONSE = new NetworkMessageServer<>(message ->
    {
        Logger.info("SERVER LOGIN_RESPONSE");

        message.getServer().sendMessageToClient(Messages.STATE_MESSAGE.setParam("OK").setRecipient(message.getSender()));
    });

    public static final NetworkMessageClient<String> STATE_MESSAGE = new NetworkMessageClient<>(message -> {});

    public static final NetworkMessageClient<Void> LOGIN_REQUEST = new NetworkMessageClient<>(message ->
    {
        Logger.info("CLIENT LOGIN_REQUEST");

        String username = message.getClient().getUsername();
        NetworkMessageClient stateMessage = message.getClient().getResponseTo(Messages.LOGIN_RESPONSE.setParam(username), message);

        Logger.info((String)stateMessage.param);
    });


    public static final NetworkMessageServer<Block> MOVE_PLAYER = new NetworkMessageServer<>(message ->
            message.getServer().getGameController().movePlayer(message.getServer().getPlayer(message.getSender()), message.param));


}
