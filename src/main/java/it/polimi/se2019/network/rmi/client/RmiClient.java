package it.polimi.se2019.network.rmi.client;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.TimeOutException;
import it.polimi.se2019.network.NetworkClient;
import it.polimi.se2019.network.OnServerDisconnectionListener;
import it.polimi.se2019.network.message.*;
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
import java.util.ArrayList;
import java.util.List;

/**
 * defines the RMI client
 */
public class RmiClient implements CallbackInterface, NetworkClient, Serializable
{
    private static final long serialVersionUID = 891993895460488695L;
    private transient ServerInterface server;
    private transient CallbackInterface stub;
    private List<OnServerDisconnectionListener> listenerList;
    private Thread connectionControllerThread;
    private boolean logged;

    private final transient UI ui;

    private transient boolean running;

    /**
     * Creates an RMI client
     * @param ui The user interface for show the model
     */
    public RmiClient(UI ui)
    {
        super();
        this.ui = ui;
        connectionControllerThread = new Thread(this::connectionControllerMethod);
        connectionControllerThread.setName("Connection controller thread");
        connectionControllerThread.setDaemon(true);
        listenerList = new ArrayList<>();
    }

    private void connectionControllerMethod()
    {
        while (running)
        {
            try
            {
                server.isConnected();
                Thread.sleep(700);
            }
            catch (RemoteException | InterruptedException e)
            {
                stop();
                listenerList.forEach(OnServerDisconnectionListener::onServerDisconnection);
            }
        }
    }

    /**
     * send a message to the server
     * @param message the message that will be send
     */
    public void sendMessageToServer(Message message)
    {
        try
        {
            server.sendMessageToServer(message, stub);
        }
        catch (RemoteException e)
        {
            Logger.exception(e);
        }
    }

    @Override
    public boolean isConnected()
    {
        return true;
    }

    @Override
    public void sendAsyncMessage(AsyncMessage message) throws RemoteException
    {
        Thread messageThread = new Thread(() -> message.accept(getUI()));
        messageThread.setName("AsyncMessage ("+message.getMessage()+") thread");
        messageThread.start();
    }

    @Override
    public synchronized Response sendRequest(CancellableActionRequest request) throws RemoteException, CanceledActionException
    {
        try
        {
            getUI().requestFocus();
            Serializable obj = request.apply(getUI());
            return new Response("Response to "+request.getMessage(), request.getMessageId(), obj, Response.Status.OK).setSender(stub);
        }
        catch (CanceledActionException e)
        {
            return new Response("Response to "+request.getMessage(), request.getMessageId(), null, Response.Status.ACTION_CANCELED).setSender(stub);
        }
        catch (TimeOutException e)
        {
            Logger.info("Timeout on "+request);
            return null;
        }
    }

    @Override
    public synchronized Response sendRequest(ActionRequest request) throws RemoteException
    {
        try
        {
            getUI().requestFocus();
            Serializable obj = request.apply(getUI());
            return new Response("Response to "+request.getMessage(), request.getMessageId(), obj, Response.Status.OK).setSender(stub);
        }
        catch (TimeOutException e)
        {
            Logger.info("Timeout on "+request);
            return null;
        }
    }

    public ServerInterface getServer()
    {
        return this.server;
    }

    @Override
    public String getUsername()
    {
        return ui.login();
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

    /**
     * Open a connection with the server and register the client
     * @param serverIp The ip of the server
     * @param serverPort The port of the server
     * @return true if the connection is established correctly
     */
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
            connectionControllerThread.start();
        }
        catch (NotBoundException | RemoteException e)
        {
            Logger.exception(e);
            return false;
        }
        return true;
    }

    @Override
    public void addOnServerDisconnectionListener(OnServerDisconnectionListener listener)
    {
        this.listenerList.add(listener);
    }

    @Override
    public synchronized void stop()
    {
        running = false;
    }
}
