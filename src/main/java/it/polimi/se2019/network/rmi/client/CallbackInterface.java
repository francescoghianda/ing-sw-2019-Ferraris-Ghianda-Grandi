package it.polimi.se2019.network.rmi.client;

import it.polimi.se2019.network.message.NetworkMessageClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallbackInterface extends Remote
{
    void sendMessage(NetworkMessageClient<?> message) throws RemoteException;
}
