package it.polimi.se2019.network.rmi.client;

import it.polimi.se2019.network.NetworkClient;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.network.rmi.server.RmiServer;
import it.polimi.se2019.network.rmi.server.ServerInterface;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.logging.Logger;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * A RMI client
 */
public class RmiClient implements CallbackInterface, NetworkClient, Serializable
{
    private transient ServerInterface server;
    private transient CallbackInterface stub;
    private transient volatile boolean getIncomeMessage;
    private transient volatile boolean incomeMessageReceived;
    private transient volatile boolean waitResponse;
    private transient volatile NetworkMessageClient<?> message;
    private transient volatile NetworkMessageServer<?> responseMessage;

    private boolean logged;

    private final transient UI ui;

    private transient boolean running;

    /**
     * Create a RMI client
     * @param ui The user interface for show the model
     */
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

    @Override
    public boolean isConnected()
    {
        return true;
    }

    @Override
    public synchronized NetworkMessageServer ask(NetworkMessageClient<?> message) throws RemoteException
    {
        try
        {
            waitResponse = true;
            Thread threadMessage = new Thread(() -> message.setClient(this).execute());
            threadMessage.start();
            while (waitResponse)this.wait();
            return responseMessage;
        }
        catch (InterruptedException e)
        {
            Logger.exception(e);
            Thread.currentThread().interrupt();
            throw new RemoteException();
        }
    }

    public ServerInterface getServer()
    {
        return this.server;
    }

    @Override
    public synchronized void sendMessageToServer(NetworkMessageServer<?> message)
    {
        if(waitResponse)
        {
            responseMessage = message.setSender(stub);
            waitResponse = false;
            this.notifyAll();
        }
        else
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
    }

    @Override
    public String getUsername()
    {
        return ui.getUsername();
    }

    @Override
    public void invalidNickname()
    {
        //TODO
    }

    @Override
    public void setLogged(boolean logged)
    {
        this.logged = logged;
        if(logged)ui.logged();
    }

    @Override
    public UI getUI()
    {
        return ui;
    }

    @Override
    public synchronized NetworkMessageClient<?> getResponseTo(NetworkMessageServer<?> messageServer)
    {
        try
        {
            getIncomeMessage = true;
            sendMessageToServer(messageServer.setSender(stub));

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

            Registry serverRegistry = LocateRegistry.getRegistry(serverIp, serverPort);
            server = (ServerInterface) serverRegistry.lookup(RmiServer.SERVER_NAME);
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
    }
}
