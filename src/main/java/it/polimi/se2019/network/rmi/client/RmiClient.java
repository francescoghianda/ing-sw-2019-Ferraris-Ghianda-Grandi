package it.polimi.se2019.network.rmi.client;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.network.NetworkClient;
import it.polimi.se2019.network.message.AsyncMessage;
import it.polimi.se2019.network.message.Request;
import it.polimi.se2019.network.message.Response;
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
    //private transient volatile NetworkMessageClient<?> message;
    //private transient volatile NetworkMessageServer<?> responseMessage;

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
    public synchronized void sendAsyncMessage(AsyncMessage message) throws RemoteException
    {
        message.accept(getUI());
    }

    @Override
    public boolean isConnected()
    {
        return true;
    }

    @Override
    public synchronized Response sendRequest(Request request) throws RemoteException
    {
        try
        {
            Serializable obj = request.apply(getUI());
            return new Response("Response to "+request.getMessage(), obj, Response.Status.OK).setSender(stub);
        }
        catch (CanceledActionException e)
        {
            return new Response("Response to "+request.getMessage(), null, Response.Status.ACTION_CANCELED).setSender(stub);
        }
    }

    public ServerInterface getServer()
    {
        return this.server;
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
