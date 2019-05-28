package it.polimi.se2019.network.rmi.client;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.network.message.*;

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
    void sendAsyncMessage(AsyncMessage message) throws RemoteException;
    boolean isConnected() throws RemoteException;

    /**
     * Execute the message received from the server and return a response message
     * @param request The message received
     * @return The response message
     * @throws RemoteException
     */
    Response sendRequest(CancellableActionRequest request) throws RemoteException, CanceledActionException;

    Response sendRequest(ActionRequest request) throws RemoteException;
}
