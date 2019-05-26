package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.network.rmi.client.CallbackInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote
{
    /**
     * Register the client in the server
     * @param rmiClient The callback of the client
     * @throws RemoteException
     */
    void registerClient(CallbackInterface rmiClient) throws RemoteException;
}
