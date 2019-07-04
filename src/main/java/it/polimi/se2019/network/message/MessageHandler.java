package it.polimi.se2019.network.message;

import it.polimi.se2019.network.ClientConnection;

public class MessageHandler
{
    private ClientConnection clientConnection;

    public MessageHandler(ClientConnection clientConnection)
    {
        this.clientConnection = clientConnection;
    }

    public void handle(Message message)
    {
        if(message.getMessage().equals("start_match"))
        {
            clientConnection.getUser().getMatch().startGame();
        }
    }
}
