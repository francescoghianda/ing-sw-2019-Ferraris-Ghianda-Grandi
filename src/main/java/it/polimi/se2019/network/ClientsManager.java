package it.polimi.se2019.network;

import it.polimi.se2019.controller.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClientsManager
{
    private static ClientsManager instance;

    private List<ClientConnection> connectedClients;
    private List<User> disconnectedClients;

    private ClientsManager()
    {
        connectedClients = Collections.synchronizedList( new ArrayList<>());
        disconnectedClients = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * if not prensent, crate a new intance of ClientsManager
     * @return the instance of clients manager
     */
    public static ClientsManager getInstance()
    {
        if(instance == null)instance = new ClientsManager();
        return instance;
    }

    /**
     * register a new client
     * @param client the new ClientConnection to be registered
     */
    public synchronized void registerClient(ClientConnection client)
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

    /**
     * unregister a client
     * if the client is in a match that is currently running, the data of the client will be saved
     * @param client the client that lost the connection
     */
    public synchronized void unregisterClient(ClientConnection client)
    {
        if(connectedClients.contains(client))
        {
            connectedClients.remove(client);
            if(client.getMatch().getState() == Match.State.RUNNING)disconnectedClients.add(client.getUser());
        }
    }

    public synchronized void deleteDisconnectedClients(Match match)
    {
        disconnectedClients = disconnectedClients.stream().filter(client -> !client.getMatch().equals(match)).collect(Collectors.toCollection(ArrayList::new));
    }

    public synchronized List<ClientConnection> getConnectedClients()
    {
        return this.connectedClients;
    }

    public synchronized List<String> getConnectedClientsUsername()
    {
        return getAllUsernameFrom(connectedClients.stream().map(ClientConnection::getUser).collect(Collectors.toList()));
    }

    public synchronized List<String> getDisconnectedClientsUsername()
    {
        return getAllUsernameFrom(disconnectedClients);
    }

    public void stopAllClients()
    {
        connectedClients.forEach(ClientConnection::stop);
    }

    private synchronized List<String> getAllUsernameFrom(List<User> users)
    {
        return users.stream().map(User::getUsername).collect(Collectors.toList());
    }

    private synchronized User findDisconnectedClient(String username)
    {
        for(User user : disconnectedClients)
        {
            if(user.getUsername().equals(username))return user;
        }
        return null;
    }
}
