package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.network.message.Message;
import it.polimi.se2019.network.rmi.client.CallbackInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote
{
    /**
     * Registers the client in the server
     * @param rmiClient is the callback of the client
     * @throws RemoteException
     */
    void registerClient(CallbackInterface rmiClient) throws RemoteException;
    void sendMessageToServer(Message message, CallbackInterface sender) throws RemoteException;
}
