package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.network.message.AsyncMessage;
import it.polimi.se2019.network.message.Request;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.logging.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class RmiServer extends UnicastRemoteObject implements NetworkServer, ServerInterface, OnClientDisconnectionListener
{
    public static final String SERVER_NAME = "Server";
    private transient HashMap<CallbackInterface, ClientConnection> clients;
    private transient GameController gameController;
    private transient boolean running;
    private transient ClientsManager clientsManager;


    /**
     * Create a RMI server
     * @param controller The current game controller
     * @throws RemoteException
     */
    public  RmiServer(GameController controller) throws RemoteException
    {
        super();
        clients = new HashMap<>();
        this.gameController = controller;
        clientsManager = ClientsManager.getInstance();
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
    public synchronized void registerClient(CallbackInterface clientStub) throws RemoteException
    {
        Logger.info("Client connected!");
        ClientConnection clientConnection = new RmiClientConnection(clientStub, this, gameController).setOnClientDisconnectionListener(this);
        clients.put(clientStub, clientConnection);
        clientsManager.registerClient(clientConnection);
        new ConnectionController(clientConnection).setOnClientDisconnectionListener(this).start();
        login(clientStub);
    }

    private void login(CallbackInterface clientStub)
    {
        ClientConnection client = clients.get(clientStub);
        String username = (String) client.getResponseTo(new Request("username", UI::getUsername)).getContent();

        while(ClientsManager.getInstance().getConnectedClientsUsername().contains(username))
        {
            username = (String) client.getResponseTo(new Request("invalid_username", UI::getUsername)).getContent();
        }

        client.sendMessageToClient(new AsyncMessage("logged", UI::logged));
        client.setUsername(username);
        client.setLogged(true);
        if(ClientsManager.getInstance().getDisconnectedClientsUsername().contains(username))
        {
            client.getServer().clientReconnected(client);
            Logger.warning("Client "+username+" has reconnected!");
        }
    }

    @Override
    public void clientReconnected(ClientConnection clientConnection)
    {
        clients.put(((RmiClientConnection)clientConnection).getCallback(), clientConnection);
        clientsManager.registerClient(clientConnection);
    }

    @Override
    public void onClientDisconnection(ClientConnection disconnectedClient)
    {
        Logger.warning("Client "+disconnectedClient.getUsername()+" has disconnected!");
        disconnectedClient.setLogged(false);

        clients.remove(((RmiClientConnection)disconnectedClient).getCallback());
        clientsManager.unregisterClient(disconnectedClient);
    }
}
