package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.controller.*;
import it.polimi.se2019.network.*;
import it.polimi.se2019.network.message.*;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.player.VirtualView;

import java.rmi.RemoteException;
import java.util.concurrent.*;

/**
 * Class that manages the connection with the client and his data
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
     * Creates a new connection with the client
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
    public void sendMessageToClient(AsyncMessage message)
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
    public synchronized Response getResponseTo(CancellableActionRequest request, TimeoutTime timeoutTime) throws CanceledActionException, ConnectionErrorException, TimeOutException
    {
        if(!connected)throw new ConnectionErrorException();
        try
        {
            FutureTask<Response> task = new FutureTask<>(() -> callback.sendRequest(request));

            Response response = sendRequest(task, timeoutTime);

            if(response.getStatus() == Response.Status.ACTION_CANCELED)throw new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER);
            return response;
        }
        catch (InterruptedException | ExecutionException e)
        {
            clientDisconnectionListener.onClientDisconnection(this);
            connected = false;
            throw new ConnectionErrorException();
        }
    }

    @Override
    public synchronized Response getResponseTo(ActionRequest request, TimeoutTime timeoutTime) throws ConnectionErrorException, TimeOutException
    {
        if(!connected)throw new ConnectionErrorException();
        try
        {
            FutureTask<Response> task = new FutureTask<>(() -> callback.sendRequest(request));
            return sendRequest(task, timeoutTime);
        }
        catch (InterruptedException | ExecutionException e)
        {
            clientDisconnectionListener.onClientDisconnection(this);
            connected = false;
            throw new ConnectionErrorException();
        }
    }

    private Response sendRequest(FutureTask<Response> task, TimeoutTime timeoutTime) throws InterruptedException, ExecutionException, TimeOutException
    {
        Thread taskThread = new Thread(task);
        taskThread.setDaemon(true);
        taskThread.start();

        Response response;

        if(timeoutTime.isIndeterminate())
        {
            response = task.get();
        }
        else
        {
            try
            {
                response = task.get(timeoutTime.getSeconds(), TimeUnit.SECONDS);
            }
            catch (TimeoutException e)
            {
                throw new TimeOutException();
            }
        }

        return response;
    }

    /**
     * handles the message with the rmi client
     * @param message
     */
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
            user.setPlayer(user.getMatch().createPlayer(this));
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
