package it.polimi.se2019.network.socket.message;

import it.polimi.se2019.utils.logging.Logger;

import java.io.Serializable;

public class Messages
{
    private Messages(){}

    public static final NetworkMessageClient<String> CHAT_MESSAGE = new NetworkMessageClient<String>()
            .setExecutor((MessageExecutor<NetworkMessageClient<String>> & Serializable) message -> Logger.info("Chat message: "+message.param));



}
