package it.polimi.se2019.network.socket.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.network.message.Messages;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.player.Player;
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
    private volatile NetworkMessageServer response;

    SocketClientConnection(Socket client, SocketServer server, GameController controller)
    {
        this.controller = controller;
        this.clientsManager = ClientsManager.getInstance();

        try
        {
            this.client = client;
            this.server = server;
            this.connected = true;
            this.ois = new ObjectInputStream(client.getInputStream());
            this.oos = new ObjectOutputStream(client.getOutputStream());
        }
        catch (IOException e)
        {
            Logger.exception(e);
        }
    }

    void start()
    {
        if(!running || !userThread.isAlive())
        {
            this.userThread = new Thread(this);
            this.running = true;
            this.userThread.start();
        }
    }

    public void stop()
    {
        try
        {
            running = false;
            connected = false;
            client.close();
            ois.close();
            oos.close();
            ///TODO informare gli altri client della disconnessione
        }
        catch (IOException e)
        {
            Logger.exception(e);
        }
    }

    public void run()
    {

        login();

        while(running)
        {
            try
            {

                NetworkMessageServer message = (NetworkMessageServer)ois.readObject();

                if(!getResponse)
                {
                    Thread thread = new Thread(() -> message.setClientConnection(this).execute());
                    thread.start();
                }
                else
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
                //Logger.exception(e);
            }
        }
    }


    private void login()
    {
        sendMessageToClient(Messages.LOGIN_REQUEST);
    }

    public void sendMessageToClient(NetworkMessageClient<?> message)
    {
        if(!connected)return;
        try
        {
            oos.writeObject(message);
            oos.flush();
        }
        catch (IOException e)
        {
            lostConnection();
            //Logger.exception(e);
        }
    }

    @Override
    public void notifyOtherClients(NetworkMessageClient<?> message)
    {
        clientsManager.getConnectedClients().forEach(clientConnection ->
        {
            if(!clientConnection.equals(this))clientConnection.sendMessageToClient(message);
        });
    }

    @Override
    public synchronized NetworkMessageServer getResponseTo(NetworkMessageClient<?> messageToClient)
    {
        try
        {
            getResponse = true;
            sendMessageToClient(messageToClient);
            while (getResponse)
            {
                this.wait();
            }
            return response;
            //return (NetworkMessageServer) ois.readObject();
        }
        catch (InterruptedException e)
        {
            //Logger.exception(e);
            lostConnection();
            Thread.currentThread().interrupt();
        }

        return null;
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
    public void setLogged(boolean logged)
    {
        //TODO spostare la creazione del giocatore in un metodo apposito (anche su rmi)
        this.logged = logged;
        this.player = controller.createPlayer(this);
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

    boolean isConnected()
    {
        return this.connected;
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
