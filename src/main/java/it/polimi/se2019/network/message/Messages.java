package it.polimi.se2019.network.message;

import it.polimi.se2019.utils.logging.Logger;

public class Messages
{
    private Messages(){}

    public static final NetworkMessageClient<String> CHAT_MESSAGE = new NetworkMessageClient<>(
            message ->
            {
                Logger.info("Chat message: "+message.param);
                message.getClient().sendMessageToServer(Messages.CHAT_MESSAGE_SERVER.setParam("Callback"));
            });

    public static final NetworkMessageServer<String> CHAT_MESSAGE_SERVER = new NetworkMessageServer<>(message ->
    {
        Logger.warning(message.param);
        message.getServer().sendMessageToClient(Messages.CHAT_MESSAGE.setParam("Ciao dal server").setRecipient(message.getSender()));
    });




}
