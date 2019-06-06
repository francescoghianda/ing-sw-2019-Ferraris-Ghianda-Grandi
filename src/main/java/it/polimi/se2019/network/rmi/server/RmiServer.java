package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.utils.logging.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer extends UnicastRemoteObject implements NetworkServer, ServerInterface, OnClientDisconnectionListener
{
    public static final String SERVER_NAME = "Server";
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
        new ConnectionController(clientConnection).setOnClientDisconnectionListener(this).start();
        clientConnection.getVirtualView().login();
    }

    @Override
    public void onClientDisconnection(ClientConnection disconnectedClient)
    {
        Logger.warning("Client "+disconnectedClient.getUser().getUsername()+" has disconnected!");
        disconnectedClient.setLogged(false, false);

        clientsManager.unregisterClient(disconnectedClient);
    }
}
