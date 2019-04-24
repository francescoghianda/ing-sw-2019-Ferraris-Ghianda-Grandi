package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.message.Messages;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.logging.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class RmiServer extends UnicastRemoteObject implements ServerInterface, NetworkServer
{
    private HashMap<CallbackInterface, ClientData> clients;
    private GameController gameController;

    public  RmiServer(int port) throws RemoteException
    {
        super();
        clients = new HashMap<>();
        gameController = new GameController();
    }

    public void startServer()
    {
        try
        {
            Naming.rebind("//localhost/Server", this);
        }
        catch (RemoteException | MalformedURLException e)
        {
            Logger.exception(e);
        }
    }

    @Override
    public void sendMessage(NetworkMessageServer<?> message) throws RemoteException
    {
        Thread threadMessage = new Thread(() -> message.setClientConnection(this).execute());
        threadMessage.start();
    }

    @Override
    public void registerClient(CallbackInterface rmiClient) throws RemoteException
    {
        clients.put(rmiClient, new ClientData());
        rmiClient.sendMessage(Messages.LOGIN_REQUEST.setRecipient(rmiClient));
    }

    @Override
    public void sendMessageToClient(NetworkMessageClient<?> message)
    {
        try
        {
            message.getRecipient().sendMessage(message);
        }
        catch (RemoteException e)
        {
            Logger.exception(e);
        }
    }

    @Override
    public void sendBroadcastMessage(NetworkMessageClient<?> message)
    {
        for(CallbackInterface client : clients.keySet())
        {
            try
            {
                client.sendMessage(message);
            }
            catch (RemoteException e)
            {
                Logger.exception(e);
            }
        }
    }

    @Override
    public Player getPlayer(CallbackInterface client)
    {
        return clients.get(client).getPlayer();
    }

    @Override
    public GameController getGameController()
    {
        return gameController;
    }
}
