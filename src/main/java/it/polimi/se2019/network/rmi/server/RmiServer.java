package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.ClientConnectionInterface;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.message.Messages;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.network.rmi.client.CallbackInterface;
import it.polimi.se2019.player.Player;
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

public class RmiServer extends UnicastRemoteObject implements NetworkServer, ServerInterface, ClientConnectionInterface
{
    public static final String SERVER_NAME = "Server";
    private transient HashMap<CallbackInterface, ClientData> clients;
    private transient GameController gameController;
    private transient boolean running;


    public  RmiServer(GameController controller) throws RemoteException
    {
        super();
        clients = new HashMap<>();
        this.gameController = controller;
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
                registry.rebind("//localhost/"+SERVER_NAME, this);
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
    public synchronized void sendMessage(NetworkMessageServer<?> message) throws RemoteException
    {
        Thread threadMessage = new Thread(() -> message.setClientConnection(this).execute());
        threadMessage.start();
    }

    @Override
    public synchronized void registerClient(CallbackInterface clientStub) throws RemoteException
    {
        Logger.info("Client connected!");
        clients.put(clientStub, new ClientData());
        clientStub.sendMessage(Messages.LOGIN_REQUEST.setRecipient(clientStub));
    }

    @Override
    public synchronized void sendMessageToClient(NetworkMessageClient<?> message)
    {
        try
        {
            message.getRecipient().sendMessage(message);
        }
        catch (RemoteException e)
        {
            Logger.exception(e);
        }
    }

    @Override
    public synchronized void sendBroadcastMessage(NetworkMessageClient<?> message)
    {
        for(CallbackInterface client : clients.keySet())
        {
            try
            {
                client.sendMessage(message);
            }
            catch (RemoteException e)
            {
                Logger.exception(e);
            }
        }
    }

    @Override
    public void setNickname(String nickname, CallbackInterface client)
    {
        clients.get(client).setNickname(nickname);
    }

    @Override
    public String getNickname(CallbackInterface client)
    {
        return clients.get(client).getNickname();
    }

    @Override
    public void setLogged(boolean logged, CallbackInterface client)
    {
        clients.get(client).setLogged(logged);
    }

    @Override
    public boolean isLogged(CallbackInterface client)
    {
        return clients.get(client).isLogged();
    }

    @Override
    public Player getPlayer(CallbackInterface client)
    {
        return clients.get(client).getPlayer();
    }

    @Override
    public List<String> getNicknames()
    {
        List<String> nicknames = new ArrayList<>();
        for(ClientData clientData : clients.values())nicknames.add(clientData.getNickname());
        return nicknames;
    }

    @Override
    public GameController getGameController()
    {
        return gameController;
    }
}
