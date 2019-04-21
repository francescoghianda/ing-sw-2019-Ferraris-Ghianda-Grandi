package it.polimi.se2019.network.rmi.client;

import it.polimi.se2019.network.NetworkClient;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.network.rmi.server.ServerInterface;
import it.polimi.se2019.utils.logging.Logger;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiClient implements CallbackInterface, NetworkClient, Serializable
{
    private ServerInterface server;

    public RmiClient(String serverIp, int port)
    {
        try
        {
            Registry registry = LocateRegistry.getRegistry(serverIp);
            server = (ServerInterface) registry.lookup("Server");

            server.registerClient(this);
        }
        catch (NotBoundException | RemoteException e)
        {
            Logger.exception(e);
        }
    }

    @Override
    public void sendMessage(NetworkMessageClient<?> message) throws RemoteException
    {
        message.setClient(this).execute();
    }

    public ServerInterface getServer()
    {
        return this.server;
    }

    @Override
    public void sendMessageToServer(NetworkMessageServer<?> message)
    {
        try
        {
            server.sendMessage(message.setSender(this));
        }
        catch (RemoteException e)
        {
            Logger.exception(e);
        }
    }
}
