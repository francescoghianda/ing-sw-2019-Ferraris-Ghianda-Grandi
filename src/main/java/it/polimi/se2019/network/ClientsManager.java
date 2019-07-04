package it.polimi.se2019.network;

import it.polimi.se2019.controller.Match;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientsManager
{
    private static ClientsManager instance;

    private ArrayList<ClientConnection> connectedClients;
    private ArrayList<User> disconnectedClients;

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
        User disconnected;
        if((disconnected = findDisconnectedClient(client.getUser().getUsername())) != null)
        {
            disconnectedClients.remove(disconnected);
            client.getUser().setPlayer(disconnected.getPlayer());
            client.getUser().setMatch(disconnected.getMatch());
            client.getUser().getPlayer().setClientConnection(client);
        }
        if(!connectedClients.contains(client))connectedClients.add(client);
    }

    public void unregisterClient(ClientConnection client)
    {
        if(connectedClients.contains(client))
        {
            connectedClients.remove(client);
            disconnectedClients.add(client.getUser());
        }
    }

    public synchronized void deleteDisconnectedClients(Match match)
    {
        disconnectedClients = disconnectedClients.stream().filter(client -> !client.getMatch().equals(match)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<ClientConnection> getConnectedClients()
    {
        return this.connectedClients;
    }

    public List<String> getConnectedClientsUsername()
    {
        return getAllUsernameFrom(connectedClients.stream().map(ClientConnection::getUser).collect(Collectors.toList()));
    }

    public List<String> getDisconnectedClientsUsername()
    {
        return getAllUsernameFrom(disconnectedClients);
    }

    public void stopAllClients()
    {
        connectedClients.forEach(ClientConnection::stop);
    }

    private List<String> getAllUsernameFrom(List<User> users)
    {
        return users.stream().map(User::getUsername).collect(Collectors.toList());
    }

    private User findDisconnectedClient(String username)
    {
        for(User user : disconnectedClients)
        {
            if(user.getUsername().equals(username))return user;
        }
        return null;
    }
}
