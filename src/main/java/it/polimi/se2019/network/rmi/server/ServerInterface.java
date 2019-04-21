package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.network.rmi.client.CallbackInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote
{
    void sendMessage(NetworkMessageServer<?> message) throws RemoteException;
    void registerClient(CallbackInterface rmiClient) throws RemoteException;
}
