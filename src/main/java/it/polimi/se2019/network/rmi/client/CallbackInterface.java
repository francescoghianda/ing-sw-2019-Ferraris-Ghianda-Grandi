package it.polimi.se2019.network.rmi.client;

import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallbackInterface extends Serializable, Remote
{
    void sendMessage(NetworkMessageClient<?> message) throws RemoteException;
    boolean isConnected() throws RemoteException;
    NetworkMessageServer ask(NetworkMessageClient<?> message) throws RemoteException;
}
