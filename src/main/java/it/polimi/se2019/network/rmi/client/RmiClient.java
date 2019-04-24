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
import java.util.Scanner;

public class RmiClient implements CallbackInterface, NetworkClient, Serializable
{
    private ServerInterface server;
    private static volatile boolean getIncomeMessage;
    private static volatile boolean incomeMessageReceived;
    private static volatile NetworkMessageClient<?> message;

    private static final SerializableObject lock = new SerializableObject();

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
        if(!getIncomeMessage)
        {
            Thread threadMessage = new Thread(() -> message.setClient(this).execute());
            threadMessage.start();
        }
        else
        {
            synchronized (lock)
            {
                RmiClient.message = message;
                incomeMessageReceived = true;
                lock.notifyAll();
            }

        }
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

    @Override
    public String getUsername()
    {
        Scanner s = new Scanner(System.in);
        return s.nextLine();
    }

    @Override
    public NetworkMessageClient<?> getResponseTo(NetworkMessageServer<?> messageServer, NetworkMessageClient<?> currMessage)
    {
        synchronized (lock)
        {
            try
            {
                getIncomeMessage = true;
                sendMessageToServer(messageServer.setSender(currMessage.getRecipient()));

                while (!incomeMessageReceived) lock.wait();

                getIncomeMessage = false;
                incomeMessageReceived = false;

                return message;
            }
            catch (Exception e)
            {
                Logger.exception(e);
            }
            return null;
        }
    }
}
