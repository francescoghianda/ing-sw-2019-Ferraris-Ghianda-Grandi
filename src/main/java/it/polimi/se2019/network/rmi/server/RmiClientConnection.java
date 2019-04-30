package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
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
    public void notifyOtherClients(NetworkMessageClient<?> message)
    {
        server.getClientsCallback().forEach(callbackInterface ->
        {
            if(!callbackInterface.equals(callback))sendMessageToClient(message);
        });
    }


    @Override
    public synchronized void sendMessageToClient(NetworkMessageClient<?> message)
    {
        try
        {
            callback.sendMessage(message);
        }
        catch (RemoteException e)
        {
            clientDisconnectionListener.onClientDisconnection(this);
        }
    }



    @Override
    public NetworkMessageServer getResponseTo(NetworkMessageClient<?> messageToClient)
    {
        try
        {
            return callback.ask(messageToClient);
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
        this.logged = logged;
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
