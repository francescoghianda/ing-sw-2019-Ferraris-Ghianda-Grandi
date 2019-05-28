package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.network.message.*;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.player.Player;

import java.rmi.RemoteException;

/**
 * Class that manage the connection with the client and his data
 */
public class RmiClientConnection implements ClientConnection
{
    private final CallbackInterface callback;
    private final RmiServer server;

    private String username;
    private boolean logged;
    private Player player;
    private GameController gameController;

    private OnClientDisconnectionListener clientDisconnectionListener;

    private ClientsManager clientsManager;

    private boolean connected;

    /**
     * Create a new connection with the client
     * @param callback The callback of the client
     * @param server The current RMI server
     * @param gameController The current game controller
     */
    public RmiClientConnection(CallbackInterface callback, RmiServer server, GameController gameController)
    {
        this.gameController = gameController;
        this.callback = callback;
        this.server = server;
        this.connected = true;
        this.clientsManager = ClientsManager.getInstance();
    }

    public CallbackInterface getCallback()
    {
        return this.callback;
    }

    public RmiClientConnection setOnClientDisconnectionListener(OnClientDisconnectionListener listener)
    {
        this.clientDisconnectionListener = listener;
        return this;
    }

    @Override
    public void notifyOtherClients(AsyncMessage message)
    {
        clientsManager.getConnectedClients().forEach(clientConnection ->
        {
            if(!clientConnection.equals(this))clientConnection.sendMessageToClient(message);
        });
    }

    public void setConnected(boolean connected)
    {
        this.connected = connected;
    }

    public boolean isConnected()
    {
        return connected;
    }

    @Override
    public synchronized void sendMessageToClient(AsyncMessage message)
    {
        if(!connected)return;
        try
        {
            callback.sendAsyncMessage(message);
        }
        catch (RemoteException e)
        {
            clientDisconnectionListener.onClientDisconnection(this);
        }
    }

    @Override
    public Response getResponseTo(CancellableActionRequest request) throws CanceledActionException
    {
        if(!connected)throw new CanceledActionException(CanceledActionException.Cause.ERROR);
        try
        {
            Response response = callback.sendRequest(request);
            if(response.getStatus() == Response.Status.ACTION_CANCELED)throw new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER);
            return response;
        }
        catch (RemoteException e)
        {
            clientDisconnectionListener.onClientDisconnection(this);
            connected = false;
            throw new ConnectionErrorException();
        }
    }

    @Override
    public Response getResponseTo(ActionRequest request)
    {
        if(!connected)return null;
        try
        {
            return callback.sendRequest(request);
        }
        catch (RemoteException e)
        {
            clientDisconnectionListener.onClientDisconnection(this);
            connected = false;
            throw  new ConnectionErrorException();
        }
    }

    @Override
    public void stop()
    {
        //TODO
    }

    @Override
    public void setUsername(String username)
    {
        this.username = username;
    }

    @Override
    public String getUsername()
    {
        return this.username;
    }

    @Override
    public void setLogged(boolean logged, boolean reconnected)
    {
        this.logged = logged;
        if(!reconnected && logged)this.player = gameController.createPlayer(this);
    }

    @Override
    public boolean isLogged()
    {
        return logged;
    }

    @Override
    public Player getPlayer()
    {
        return player;
    }

    @Override
    public GameController getGameController()
    {
        return gameController;
    }

    @Override
    public NetworkServer getServer()
    {
        return server;
    }

    @Override
    public void setPlayer(Player player)
    {
        this.player = player;
    }
}
