package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.ClientConnectionInterface;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.network.message.Messages;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.utils.logging.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RmiServer extends UnicastRemoteObject implements NetworkServer, ServerInterface, OnClientDisconnectionListener
{
    public static final String SERVER_NAME = "Server";
    private transient HashMap<CallbackInterface, ClientConnectionInterface> clients;
    private transient HashMap<String, ClientConnectionInterface> disconnectedClients;
    private transient GameController gameController;
    private transient boolean running;


    public  RmiServer(GameController controller) throws RemoteException
    {
        super();
        clients = new HashMap<>();
        this.gameController = controller;
        disconnectedClients = new HashMap<>();
    }

    @Override
    public void startServer(int port)
    {
        if(!running)
        {
            try
            {
                Logger.info("Server starting...");
                Registry registry = LocateRegistry.createRegistry(port);
                registry.rebind(SERVER_NAME, this);
                Logger.info("Server RMI started! (IP: "+ InetAddress.getLocalHost().getHostAddress()+", Port: "+port+")");
                Logger.info("Waiting for clients...");
                running = true;
            }
            catch (RemoteException | UnknownHostException e)
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
        //TODO
    }

    @Override
    public List<String> getDisconnectedClientsUsername()
    {
        return new ArrayList<>(disconnectedClients.keySet());
    }

    @Override
    public void clientReconnected(String username, CallbackInterface client)
    {
        clients.get(client).setPlayer(disconnectedClients.get(username).getPlayer());
        disconnectedClients.remove(username);
    }

    @Override
    public synchronized void sendMessage(NetworkMessageServer<?> message) throws RemoteException
    {
        Thread threadMessage = new Thread(() -> message.setClientConnection(clients.get(message.getSender())).execute());
        threadMessage.start();
    }

    @Override
    public synchronized void registerClient(CallbackInterface clientStub) throws RemoteException
    {
        Logger.info("Client connected!");
        ClientConnectionInterface connectionInterface = new RmiClientConnection(clientStub, this, gameController).setOnClientDisconnectionListener(this);
        clients.put(clientStub, connectionInterface);
        new ConnectionController(connectionInterface).setOnClientDisconnectionListener(this).start();
        clients.get(clientStub).sendMessageToClient(Messages.LOGIN_REQUEST);
    }

    List<CallbackInterface> getClientsCallback()
    {
        return new ArrayList<>(clients.keySet());
    }

    @Override
    public synchronized void sendBroadcastMessage(NetworkMessageClient<?> message)
    {
        clients.values().forEach(clientConnection -> clientConnection.sendMessageToClient(message));
    }

    @Override
    public List<String> getConnectedClientsUsername()
    {
        List<String> allUsername = new ArrayList<>();
        for(ClientConnectionInterface connection : clients.values())allUsername.add(connection.getUsername());
        return allUsername;
    }

    @Override
    public void onClientDisconnection(ClientConnectionInterface disconnectedClient)
    {
        Logger.warning("Client "+disconnectedClient.getUsername()+" has disconnected!");
        disconnectedClient.setLogged(false);
        disconnectedClients.put(disconnectedClient.getUsername(), disconnectedClient);
        clients.remove(((RmiClientConnection)disconnectedClient).getCallback());
    }
}
