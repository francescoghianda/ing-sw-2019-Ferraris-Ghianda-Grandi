package it.polimi.se2019.network.socket.server;

import it.polimi.se2019.utils.logging.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements Runnable
{
    private int port;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean running;
    private ArrayList<ClientConnection> clients;

    public Server(int port)
    {
        this.port = port;
        this.serverThread = new Thread(this);
        this.clients = new ArrayList<>();
    }

    public void startServer()
    {
        if(!running)
        {
            Logger.info("Server starting...");

            try
            {
                if(serverSocket == null || serverSocket.isClosed())serverSocket = new ServerSocket(port);
                serverThread = new Thread(this);
                serverThread.start();
                Logger.info("Server started! (IP: "+InetAddress.getLocalHost().getHostAddress()+", Port: "+port+")");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Logger.error("Server already running!");
        }
    }

    public void stop()
    {
        try
        {
            Logger.info("Stopping server...");

            for (ClientConnection client : clients) client.stop();

            running = false;
            serverSocket.close();
            serverThread.interrupt();

            Logger.info("Server stopped!");
            System.exit(1);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public final void run()
    {
        this.running = true;

        try
        {
            while(running)
            {
                Logger.info("Waiting for clients...");
                ClientConnection client = new ClientConnection(serverSocket.accept(), this);
                Logger.info("Client connected!");
                clients.add(client);
                client.start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<ClientConnection> getClients()
    {
        return this.clients;
    }

    public synchronized void writeBroadcastMessage(NetworkMessageClient<?> message)
    {
        for (ClientConnection client : clients)
        {
            if (client.isConnected())
            {
                client.writeMessage(message);
            }
        }
    }
}
