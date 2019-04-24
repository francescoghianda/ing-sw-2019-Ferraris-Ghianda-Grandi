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
import java.util.HashMap;

public class RmiServer extends UnicastRemoteObject implements ServerInterface, NetworkServer
{
    private transient HashMap<CallbackInterface, ClientData> clients;
    private transient GameController gameController;

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
    public synchronized void sendMessage(NetworkMessageServer<?> message) throws RemoteException
    {
        Thread threadMessage = new Thread(() -> message.setClientConnection(this).execute());
        threadMessage.start();
    }

    @Override
    public synchronized void registerClient(CallbackInterface clientStub) throws RemoteException
    {
        clients.put(clientStub, new ClientData());
        clientStub.sendMessage(Messages.LOGIN_REQUEST.setRecipient(clientStub));
    }

    @Override
    public synchronized void sendMessageToClient(NetworkMessageClient<?> message)
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
    public synchronized void sendBroadcastMessage(NetworkMessageClient<?> message)
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
