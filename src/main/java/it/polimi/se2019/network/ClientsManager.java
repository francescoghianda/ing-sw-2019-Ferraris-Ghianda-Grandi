package it.polimi.se2019.network;

import it.polimi.se2019.network.message.NetworkMessageClient;

import java.util.ArrayList;
import java.util.List;

public class ClientsManager
{
    private static ClientsManager instance;

    private ArrayList<ClientConnection> connectedClients;
    private ArrayList<ClientConnection> disconnectedClients;

    private ClientsManager()
    {
        connectedClients = new ArrayList<>();
        disconnectedClients = new ArrayList<>();
    }

    public static ClientsManager getInstance()
    {
        if(instance == null)instance = new ClientsManager();
        return instance;
    }

    public void registerClient(ClientConnection client)
    {
        ClientConnection disconnected;
        if((disconnected = findDisconnectedClient(client)) != null)
        {
            disconnectedClients.remove(disconnected);
            client.setPlayer(disconnected.getPlayer());
        }
        if(!connectedClients.contains(client))connectedClients.add(client);
    }

    public void unregisterClient(ClientConnection client)
    {
        if(connectedClients.contains(client))
        {
            connectedClients.remove(client);
            disconnectedClients.add(client);
        }
    }

    public List<ClientConnection> getConnectedClients()
    {
        return this.connectedClients;
    }

    public void sendBroadcastMessage(NetworkMessageClient<?> message)
    {
        connectedClients.forEach(clientConnection -> clientConnection.sendMessageToClient(message));
    }

    public List<String> getConnectedClientsUsername()
    {
        return getAllUsernameFrom(connectedClients);
    }

    public List<String> getDisconnectedClientsUsername()
    {
        return getAllUsernameFrom(disconnectedClients);
    }

    public void stopAllClients()
    {
        connectedClients.forEach(ClientConnection::stop);
    }

    private List<String> getAllUsernameFrom(List<ClientConnection> clients)
    {
        List<String> clientsUsername = new ArrayList<>();
        clients.forEach(clientConnection -> clientsUsername.add(clientConnection.getUsername()));
        return clientsUsername;
    }

    private ClientConnection findDisconnectedClient(ClientConnection client)
    {
        for(ClientConnection clientConnection : disconnectedClients)
        {
            if(clientConnection.getUsername().equals(client.getUsername()))return clientConnection;
        }
        return null;
    }
}
