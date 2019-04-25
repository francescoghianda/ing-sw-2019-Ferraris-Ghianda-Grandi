package it.polimi.se2019.network.socket.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.utils.logging.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server implements NetworkServer, Runnable
{
    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean running;
    private ArrayList<ClientConnection> clients;
    private GameController gameController;

    public Server(GameController controller)
    {
        this.serverThread = new Thread(this);
        this.clients = new ArrayList<>();
        this.gameController = controller;
    }

    @Override
    public void startServer(int port)
    {
        if(!running)
        {
            Logger.info("Server starting...");

            try
            {
                if(serverSocket == null || serverSocket.isClosed())serverSocket = new ServerSocket(port);
                serverThread = new Thread(this);
                Logger.info("Server socket started! (IP: "+InetAddress.getLocalHost().getHostAddress()+", Port: "+port+")");
                serverThread.start();
            }
            catch (IOException e)
            {
                Logger.exception(e);
            }
        }
        else
        {
            Logger.error("Server already running!");
        }
    }

    @Override
    public void stopServer()
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
            Logger.exception(e);
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
                ClientConnection client = new ClientConnection(serverSocket.accept(), this, gameController);
                Logger.info("Client connected!");
                clients.add(client);
                client.start();
            }
        }
        catch (IOException e)
        {
            Logger.exception(e);
        }
    }

    public synchronized List<ClientConnection> getClients()
    {
        return this.clients;
    }

    public synchronized void writeBroadcastMessage(NetworkMessageClient<?> message)
    {
        for (ClientConnection client : clients)
        {
            if (client.isConnected())
            {
                client.sendMessageToClient(message);
            }
        }
    }
}
