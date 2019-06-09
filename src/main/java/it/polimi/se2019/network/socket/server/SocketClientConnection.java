package it.polimi.se2019.network.socket.server;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.network.User;
import it.polimi.se2019.network.message.*;
import it.polimi.se2019.player.VirtualView;
import it.polimi.se2019.utils.logging.Logger;

import java.io.*;
import java.net.Socket;

public class SocketClientConnection implements Runnable, ClientConnection
{
    private Thread userThread;
    private volatile boolean running;
    private volatile boolean connected;
    private volatile boolean logged;
    private Socket client;
    private SocketServer server;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private final GameController controller;

    private ClientsManager clientsManager;

    private OnClientDisconnectionListener clientDisconnectionListener;

    private volatile boolean getResponse;
    private volatile Response response;

    private Thread requestThread;

    private final User user;
    private final VirtualView view;

    SocketClientConnection(Socket client, SocketServer server, GameController controller) throws IOException
    {
        this.controller = controller;
        this.clientsManager = ClientsManager.getInstance();

        this.client = client;
        this.server = server;
        this.connected = true;

        this.user = new User();
        this.view = new VirtualView(this);

        try
        {
            this.ois = new ObjectInputStream(client.getInputStream());
            this.oos = new ObjectOutputStream(client.getOutputStream());
        }
        catch (IOException e)
        {
            client.close();
            throw new IOException(e.getCause());
        }

    }

    void start()
    {
        if(!running || !userThread.isAlive())
        {
            this.userThread = new Thread(this);
            this.running = true;
            this.userThread.start();
            view.login();
        }
    }

    public void stop()
    {
        try
        {
            running = false;
            connected = false;
            client.close();
            ///TODO informare gli altri client della disconnessione
        }
        catch (IOException e)
        {
            Logger.exception(e);
        }
    }

    public void run()
    {
        while(running)
        {
            try
            {
                Response message = (Response) ois.readObject();

                if(getResponse)
                {
                    synchronized (this)
                    {
                        response = message;
                        getResponse = false;
                        this.notifyAll();
                    }
                }

            }
            catch (ClassNotFoundException | NullPointerException | IOException e)
            {
                Thread.currentThread().interrupt();
                running = false;
                connected = false;
                if(requestThread != null)requestThread.interrupt();
                else lostConnection();
            }
        }
    }

    public void sendMessageToClient(AsyncMessage message)
    {
        sendMessageToClient((Message) message);
    }

    @Override
    public void notifyOtherClients(AsyncMessage message)
    {
        clientsManager.getConnectedClients().forEach(clientConnection ->
        {
            if(!clientConnection.equals(this))clientConnection.sendMessageToClient(message);
        });
    }

    private void sendMessageToClient(Message message)
    {
        if(!connected)return;
        try
        {
            oos.writeUnshared(message);
            oos.flush();
        }
        catch (IOException e)
        {
            lostConnection();
            //userThread.interrupt();
            Logger.exception(e);
            throw new ConnectionErrorException();
        }
    }

    @Override
    public User getUser()
    {
        return this.user;
    }

    @Override
    public VirtualView getVirtualView()
    {
        return this.view;
    }

    @Override
    public synchronized Response getResponseTo(CancellableActionRequest request) throws CanceledActionException
    {
        if(!connected)throw new CanceledActionException(CanceledActionException.Cause.ERROR);
        try
        {
            requestThread = Thread.currentThread();
            getResponse = true;
            sendMessageToClient(request);
            while (getResponse)
            {
                this.wait();
            }
            requestThread = null;
            if(response.getStatus() == Response.Status.ACTION_CANCELED)throw new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER);
            return response;
        }
        catch (InterruptedException e)
        {
            requestThread = null;
            lostConnection();
            throw new ConnectionErrorException();
        }
    }

    @Override
    public synchronized Response getResponseTo(ActionRequest request)
    {
        if(!connected)return null;
        try
        {
            requestThread = Thread.currentThread();
            getResponse = true;
            sendMessageToClient(request);
            while (getResponse)
            {
                this.wait();
            }
            requestThread = null;
            return response;
        }
        catch (InterruptedException e)
        {
            requestThread = null;
            lostConnection();
            throw new ConnectionErrorException();
        }
    }

    private void lostConnection()
    {
        stop();
        connected = false;
        Logger.warning("Client "+user.getUsername()+" has disconnected!");
        ClientsManager.getInstance().unregisterClient(this);
    }

    @Override
    public void setLogged(boolean logged, boolean reconnected)
    {
        this.logged = logged;
        if(!reconnected && logged)user.setPlayer(controller.createPlayer(this));
    }

    @Override
    public boolean isLogged()
    {
        return this.logged;
    }


    @Override
    public GameController getGameController()
    {
        return controller;
    }

    @Override
    public boolean isConnected()
    {
        return this.connected;
    }

    @Override
    public void setConnected(boolean connected)
    {
        this.connected = connected;
    }

    @Override
    public SocketServer getServer()
    {
        return server;
    }

    public GameController getGameCotroller()
    {
        return controller;
    }

    public SocketClientConnection setOnClientDisconnectionListener(OnClientDisconnectionListener listener)
    {
        this.clientDisconnectionListener = listener;
        return this;
    }
}
