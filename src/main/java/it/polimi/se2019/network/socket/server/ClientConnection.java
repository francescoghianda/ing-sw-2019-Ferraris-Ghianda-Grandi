package it.polimi.se2019.network.socket.server;

import it.polimi.se2019.network.socket.message.NetworkMessageClient;
import it.polimi.se2019.network.socket.message.NetworkMessageServer;
import it.polimi.se2019.player.Player;

import java.io.*;
import java.net.Socket;

public class ClientConnection implements Runnable
{
    private Thread userThread;
    private volatile boolean running;
    private boolean connected;
    private Socket client;
    private Server server;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Player player;

    ClientConnection(Socket client, Server server)
    {
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
            e.printStackTrace();
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
            e.printStackTrace();
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
                e.printStackTrace();
            }
        }
    }

    synchronized void writeMessage(NetworkMessageClient<?> message)
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
            e.printStackTrace();
        }
    }

    private synchronized void notifyAllClient(NetworkMessageClient<?> message)
    {
        for (ClientConnection clientConnection : server.getClients())
        {
            if(clientConnection.isConnected() && !clientConnection.equals(this))
            {
                clientConnection.writeMessage(message);
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

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }
}
