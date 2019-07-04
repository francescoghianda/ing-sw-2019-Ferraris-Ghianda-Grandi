package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.network.message.Message;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.utils.logging.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RmiServer extends UnicastRemoteObject implements NetworkServer, ServerInterface, OnClientDisconnectionListener
{
    public static final String SERVER_NAME = "Server";
    private transient boolean running;
    private transient ClientsManager clientsManager;

    private List<RmiClientConnection> clients;

    /**
     * Creates an RMI server
     * @throws RemoteException
     */
    public  RmiServer() throws RemoteException
    {
        super();
        clients = new ArrayList<>();
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

    public synchronized void sendMessageToServer(Message message, CallbackInterface sender) throws RemoteException
    {
        Optional<RmiClientConnection> clientConnection = clients.stream().filter(client -> client.getCallback().equals(sender)).findFirst();
        clientConnection.ifPresent(rmiClientConnection -> rmiClientConnection.handleMessage(message));
    }

    @Override
    public synchronized void registerClient(CallbackInterface clientStub) throws RemoteException
    {
        Logger.info("Client connected!");
        RmiClientConnection clientConnection = new RmiClientConnection(clientStub, this).setOnClientDisconnectionListener(this);
        clients.add(clientConnection);
        new ConnectionController(clientConnection).setOnClientDisconnectionListener(this).start();
        clientConnection.getVirtualView().login();
    }

    @Override
    public void onClientDisconnection(ClientConnection disconnectedClient)
    {
        Logger.warning("Client "+disconnectedClient.getUser().getUsername()+" has disconnected!");
        disconnectedClient.setLogged(false, false);
        clients.remove(disconnectedClient);
        clientsManager.unregisterClient(disconnectedClient);
    }
}
