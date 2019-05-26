package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.network.message.AsyncMessage;
import it.polimi.se2019.network.message.Message;
import it.polimi.se2019.network.message.Request;
import it.polimi.se2019.network.message.Response;
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

    @Override
    public synchronized void sendMessageToClient(AsyncMessage message)
    {
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
    public Response getResponseTo(Request request)
    {
        try
        {
            return callback.sendRequest(request);
        }
        catch (RemoteException e)
        {
            clientDisconnectionListener.onClientDisconnection(this);
            return null;
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
    public void setLogged(boolean logged)
    {
        //TODO vedi socket
        this.logged = logged;
        this.player = gameController.createPlayer(this);
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
