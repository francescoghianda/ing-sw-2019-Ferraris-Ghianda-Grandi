package it.polimi.se2019.network.rmi.client;

import it.polimi.se2019.network.NetworkClient;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.network.rmi.server.ServerInterface;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.logging.Logger;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiClient implements CallbackInterface, NetworkClient, Serializable
{
    private transient ServerInterface server;
    private transient CallbackInterface stub;
    private transient volatile boolean getIncomeMessage;
    private transient volatile boolean incomeMessageReceived;
    private transient volatile NetworkMessageClient<?> message;

    private final transient UI ui;

    private transient boolean running;


    public RmiClient(UI ui)
    {
        super();
        this.ui = ui;
    }

    @Override
    public synchronized void sendMessage(NetworkMessageClient<?> message) throws RemoteException
    {
        if(!getIncomeMessage)
        {
            Thread threadMessage = new Thread(() -> message.setClient(this).execute());
            threadMessage.start();
        }
        else
        {
            this.message = message;
            incomeMessageReceived = true;
            this.notifyAll();
        }
    }

    public ServerInterface getServer()
    {
        return this.server;
    }

    @Override
    public synchronized void sendMessageToServer(NetworkMessageServer<?> message)
    {
        try
        {
            server.sendMessage(message.setSender(stub));
        }
        catch (RemoteException e)
        {
            Logger.exception(e);
        }
    }

    @Override
    public String getUsername()
    {
        return ui.getUsername();
    }

    @Override
    public synchronized NetworkMessageClient<?> getResponseTo(NetworkMessageServer<?> messageServer, NetworkMessageClient<?> currMessage)
    {
        try
        {
            getIncomeMessage = true;
            sendMessageToServer(messageServer.setSender(currMessage.getRecipient()));

            while (!incomeMessageReceived) this.wait();

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

    @Override
    public boolean connect(String serverIp, int serverPort)
    {
        try
        {
            stub = (CallbackInterface) UnicastRemoteObject.exportObject(this, 0);

            Registry registry = LocateRegistry.getRegistry(serverIp);
            server = (ServerInterface) registry.lookup("Server");
            server.registerClient(stub);
            running = true;
        }
        catch (NotBoundException | RemoteException e)
        {
            Logger.exception(e);
            return false;
        }
        return true;
    }

    @Override
    public synchronized void stop()
    {
        running = false;
        this.notifyAll();
    }

    private synchronized void waitCallbacks()
    {
        try
        {
            while(running)this.wait();
        }
        catch (InterruptedException e)
        {
            Logger.exception(e);
        }
    }
}
