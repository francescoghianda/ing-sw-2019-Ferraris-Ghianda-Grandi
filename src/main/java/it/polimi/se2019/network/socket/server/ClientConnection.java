package it.polimi.se2019.network.socket.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.utils.logging.Logger;

import java.io.*;
import java.net.Socket;

public class ClientConnection implements Runnable, NetworkServer
{
    private Thread userThread;
    private volatile boolean running;
    private boolean connected;
    private Socket client;
    private Server server;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private final GameController controller;

    ClientConnection(Socket client, Server server, GameController controller)
    {
        this.controller = controller;

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

    void stop()
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
                NetworkMessageServer message = (NetworkMessageServer)ois.readObject();
                message.setClientConnection(this).execute();
            }
            catch (ClassNotFoundException | NullPointerException | IOException e)
            {
                Logger.exception(e);
            }
        }
    }

    public synchronized void sendMessageToClient(NetworkMessageClient<?> message)
    {
        if(!connected)return;
        try
        {
            oos.writeObject(message);
            oos.flush();
        }
        catch (IOException e)
        {
            running = false;
            connected = false;
            Logger.exception(e);
        }
    }

    private synchronized void notifyAllClient(NetworkMessageClient<?> message)
    {
        for (ClientConnection clientConnection : server.getClients())
        {
            if(clientConnection.isConnected() && !clientConnection.equals(this))
            {
                clientConnection.sendMessageToClient(message);
            }
        }
    }

    @Override
    public synchronized void sendBroadcastMessage(NetworkMessageClient<?> message)
    {
        for (ClientConnection clientConnection : server.getClients())
        {
            if(clientConnection.isConnected())
            {
                clientConnection.sendMessageToClient(message);
            }
        }
    }

    boolean isConnected()
    {
        return this.connected;
    }

    public Server getServer()
    {
        return server;
    }

    public GameController getGameCotroller()
    {
        return controller;
    }
}
