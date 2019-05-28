package it.polimi.se2019.network.socket.server;

import it.polimi.se2019.card.cardscript.LogicExpression;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.utils.logging.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class SocketServer implements NetworkServer, Runnable, OnClientDisconnectionListener
{
    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean running;
    private GameController gameController;
    private ClientsManager clientsManager;

    public SocketServer(GameController controller)
    {
        this.serverThread = new Thread(this);
        this.gameController = controller;
        clientsManager = ClientsManager.getInstance();
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

            clientsManager.stopAllClients();

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
    public void clientReconnected(ClientConnection clientConnection)
    {
        clientsManager.registerClient(clientConnection);
    }

    @Override
    public final void run()
    {
        this.running = true;

        while(running)
        {
            try
            {
                Logger.info("Waiting for clients...");
                SocketClientConnection client = new SocketClientConnection(serverSocket.accept(), this, gameController);
                Logger.info("Client connected!");
                clientsManager.registerClient(client.setOnClientDisconnectionListener(this));
                client.start();
            }
            catch (IOException e)
            {
                Logger.exception(e);
                Logger.info("Failed to register new client!");
            }

        }

    }


    @Override
    public void onClientDisconnection(ClientConnection disconnectedClient)
    {
        clientsManager.unregisterClient(disconnectedClient);
    }
}
