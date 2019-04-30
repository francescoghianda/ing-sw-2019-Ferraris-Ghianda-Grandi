package it.polimi.se2019.network.socket.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.utils.logging.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SocketServer implements NetworkServer, Runnable, OnClientDisconnectionListener
{
    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean running;
    private ArrayList<ClientConnection> clients;
    private GameController gameController;

    private HashMap<String, ClientConnection> disconnectedClients;

    public SocketServer(GameController controller)
    {
        this.serverThread = new Thread(this);
        this.clients = new ArrayList<>();
        this.disconnectedClients = new HashMap<>();
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
    public List<String> getConnectedClientsUsername()
    {
        List<String> allUsername = new ArrayList<>();
        clients.forEach(clientConnection -> allUsername.add(clientConnection.getUsername()));
        return allUsername;
    }

    @Override
    public List<String> getDisconnectedClientsUsername()
    {
        return new ArrayList<>(this.disconnectedClients.keySet());
    }

    @Override
    public void clientReconnected(String username, CallbackInterface client)
    {
        ClientConnection clientConnection = null;
        for(ClientConnection connection : clients)if(connection.getUsername().equals(username))clientConnection = connection;
        if(clientConnection != null)clientConnection.setPlayer(disconnectedClients.get(username).getPlayer());
        disconnectedClients.remove(username);
    }

    @Override
    public void sendBroadcastMessage(NetworkMessageClient<?> message)
    {
        clients.forEach(clientConnection -> clientConnection.sendMessageToClient(message));
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
                SocketClientConnection client = new SocketClientConnection(serverSocket.accept(), this, gameController);
                Logger.info("Client connected!");
                clients.add(client.setOnClientDisconnectionListener(this));
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

    @Override
    public void onClientDisconnection(ClientConnection disconnectedClient)
    {
        disconnectedClients.putIfAbsent(disconnectedClient.getUsername(), disconnectedClient);
        clients.remove(disconnectedClient);
    }
}
