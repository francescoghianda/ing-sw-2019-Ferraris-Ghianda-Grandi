package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.network.rmi.client.CallbackInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote
{
    /**
     * Execute the message received from the client
     * @param message
     * @throws RemoteException
     */
    void sendMessage(NetworkMessageServer<?> message) throws RemoteException;

    /**
     * Register the client in the server
     * @param rmiClient The callback of the client
     * @throws RemoteException
     */
    void registerClient(CallbackInterface rmiClient) throws RemoteException;
}
