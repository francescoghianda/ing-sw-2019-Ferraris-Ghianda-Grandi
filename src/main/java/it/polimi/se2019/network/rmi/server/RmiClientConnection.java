package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.controller.Match;
import it.polimi.se2019.controller.MatchManager;
import it.polimi.se2019.network.*;
import it.polimi.se2019.network.message.*;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.player.VirtualView;

import java.rmi.RemoteException;

/**
 * Class that manage the connection with the client and his data
 */
public class RmiClientConnection implements ClientConnection
{
    private final CallbackInterface callback;
    private final RmiServer server;

    private boolean logged;

    private OnClientDisconnectionListener clientDisconnectionListener;

    private ClientsManager clientsManager;

    private final User user;
    private final VirtualView view;

    private boolean connected;

    private MessageHandler messageHandler;

    /**
     * Create a new connection with the client
     * @param callback The callback of the client
     * @param server The current RMI server
     */
    public RmiClientConnection(CallbackInterface callback, RmiServer server)
    {
        this.callback = callback;
        this.server = server;
        this.connected = true;
        this.user = new User();
        this.view = new VirtualView(this);
        this.messageHandler = new MessageHandler(this);
        this.clientsManager = ClientsManager.getInstance();
    }

    CallbackInterface getCallback()
    {
        return this.callback;
    }

    RmiClientConnection setOnClientDisconnectionListener(OnClientDisconnectionListener listener)
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
        if(!connected)throw new ConnectionErrorException();
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
        if(!connected)throw new ConnectionErrorException();
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

    public void handleMessage(Message message)
    {
        Thread messageThread = new Thread(()-> messageHandler.handle(message));
        messageThread.setDaemon(true);
        messageThread.setName("Thread handling "+message);
        messageThread.start();
    }

    @Override
    public void stop()
    {
        //TODO
    }

    @Override
    public User getUser()
    {
        return user;
    }

    public VirtualView getVirtualView()
    {
        return view;
    }

    @Override
    public void setLogged(boolean logged, boolean reconnected)
    {
        this.logged = logged;
        if(!reconnected && logged)
        {
            user.setMatch(MatchManager.getInstance().getMatch());
            user.setPlayer(user.getMatch().getGameController().createPlayer(this));
        }
    }

    @Override
    public boolean isLogged()
    {
        return logged;
    }

    @Override
    public GameController getGameController()
    {
        return user.getMatch().getGameController();
    }

    @Override
    public Match getMatch()
    {
        return user.getMatch();
    }

    @Override
    public NetworkServer getServer()
    {
        return server;
    }

}
