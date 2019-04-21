package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.network.rmi.client.CallbackInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RmiServer extends UnicastRemoteObject implements ServerInterface, NetworkServer
{
    private ArrayList<CallbackInterface> clients;

    public  RmiServer(int port) throws RemoteException
    {
        super();
        clients = new ArrayList<>();
    }

    public void startServer()
    {
        try
        {
            Naming.rebind("//localhost/Server", this);
        }
        catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(NetworkMessageServer<?> message) throws RemoteException
    {
        message.setClientConnection(this).execute();
    }

    @Override
    public void registerClient(CallbackInterface rmiClient) throws RemoteException
    {
        clients.add(rmiClient);
    }

    public void sendMessageToClient(NetworkMessageClient<?> message)
    {
        try
        {
            message.getRecipient().sendMessage(message);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public void sendBroadcastMessage(NetworkMessageClient<?> message)
    {
        for(CallbackInterface client : clients)
        {
            try
            {
                client.sendMessage(message);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
    }
}
