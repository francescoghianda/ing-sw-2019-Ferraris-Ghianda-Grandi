package it.polimi.se2019;

import it.polimi.se2019.map.Map;
import it.polimi.se2019.network.rmi.client.RmiClient;
import it.polimi.se2019.network.rmi.server.RmiServer;
import it.polimi.se2019.network.socket.client.Client;
import it.polimi.se2019.network.message.Messages;
import it.polimi.se2019.network.socket.server.Server;
import it.polimi.se2019.utils.logging.Logger;

import java.rmi.RemoteException;

public class App 
{
    public static void main( String[] args ) throws RemoteException
    {
        Logger.info( "Hello Word!");

        Map map = new Map();
        map.initMap();

        Logger.info(map.toString());

        /*Server server = new Server(7000);
        server.startServer();*/

        /*Client client = new Client();
        client.connect("localhost", 7000);
        client.start();*/

        //server.writeBroadcastMessage(Messages.CHAT_MESSAGE.setParam("Ciao"));

        RmiServer server = new RmiServer(7000);
        server.startServer();
        //RmiClient client = new RmiClient("localhost", 7000);

        //server.sendMessageToClient(Messages.CHAT_MESSAGE.setParam("Ciao").setRecipient(client));

    }
}
