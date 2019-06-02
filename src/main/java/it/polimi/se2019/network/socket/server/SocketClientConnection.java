package it.polimi.se2019.network.socket.server;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.network.message.*;
import it.polimi.se2019.ui.UI;
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
    private Player player;
    private String nickname;

    private ClientsManager clientsManager;

    private OnClientDisconnectionListener clientDisconnectionListener;

    private volatile boolean getResponse;
    private volatile Response response;

    private Thread requestThread;

    SocketClientConnection(Socket client, SocketServer server, GameController controller) throws IOException
    {
        this.controller = controller;
        this.clientsManager = ClientsManager.getInstance();

        this.client = client;
        this.server = server;
        this.connected = true;

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
            login();
        }
    }

    public void stop()
    {
        try
        {
            running = false;
            connected = false;
            client.close();
            if(ois != null)ois.close();
            if(oos != null)oos.close();
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
                lostConnection();
                if(requestThread != null)requestThread.interrupt();
            }
        }
    }


    private void login()
    {
        String username = (String) getResponseTo(RequestFactory.newActionRequest("username", UI::getUsername)).getContent();

        while(ClientsManager.getInstance().getConnectedClientsUsername().contains(username))
        {
            username = (String) getResponseTo(RequestFactory.newActionRequest("invalid_username", UI::getUsername)).getContent();
        }

        sendMessageToClient(new AsyncMessage("logged", UI::logged));
        setUsername(username);
        boolean reconnected = false;
        if(ClientsManager.getInstance().getDisconnectedClientsUsername().contains(username))
        {
            getServer().clientReconnected(this);
            reconnected = true;
            controller.playerReconnected(getPlayer());
            Logger.warning("Client "+username+" has reconnected!");
        }
        setLogged(true, reconnected);
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
            oos.reset();
            oos.writeObject(message);
            oos.flush();
        }
        catch (IOException e)
        {
            lostConnection();
            Logger.exception(e);
            throw new ConnectionErrorException();
        }
    }

    @Override
    public synchronized Response getResponseTo(CancellableActionRequest request) throws CanceledActionException
    {
        if(!connected)throw new CanceledActionException(CanceledActionException.Cause.ERROR);
        try
        {
            getResponse = true;
            sendMessageToClient(request);
            while (getResponse)
            {
                this.wait();
            }
            if(response.getStatus() == Response.Status.ACTION_CANCELED)throw new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER);
            return response;
        }
        catch (InterruptedException e)
        {
            lostConnection();
            connected = false;
            userThread.interrupt();
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
            return response;
        }
        catch (InterruptedException e)
        {
            lostConnection();
            connected = false;
            userThread.interrupt();
            throw new ConnectionErrorException();
        }
    }

    private void lostConnection()
    {
        stop();
        Logger.warning("Client "+nickname+" has disconnected!");
        clientDisconnectionListener.onClientDisconnection(this);
    }

    @Override
    public void setUsername(String nickname)
    {
        this.nickname = nickname;
    }

    @Override
    public String getUsername()
    {
        return this.nickname;
    }

    @Override
    public void setLogged(boolean logged, boolean reconnected)
    {
        this.logged = logged;
        if(!reconnected && logged)this.player = controller.createPlayer(this);
    }

    @Override
    public boolean isLogged()
    {
        return this.logged;
    }

    @Override
    public Player getPlayer()
    {
        return player;
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

    @Override
    public void setPlayer(Player player)
    {
        this.player = player;
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
