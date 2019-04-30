package it.polimi.se2019.network.rmi.client;

import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallbackInterface extends Serializable, Remote
{
    /**
     * Execute the message received from the server
     * @param message The message received
     * @throws RemoteException
     */
    void sendMessage(NetworkMessageClient<?> message) throws RemoteException;
    boolean isConnected() throws RemoteException;

    /**
     * Execute the message received from the server and return a response message
     * @param message The message received
     * @return The response message
     * @throws RemoteException
     */
    NetworkMessageServer ask(NetworkMessageClient<?> message) throws RemoteException;
}
