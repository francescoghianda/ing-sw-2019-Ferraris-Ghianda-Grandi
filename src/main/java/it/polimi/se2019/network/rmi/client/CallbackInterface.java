package it.polimi.se2019.network.rmi.client;

import it.polimi.se2019.network.message.AsyncMessage;
import it.polimi.se2019.network.message.Message;
import it.polimi.se2019.network.message.Request;
import it.polimi.se2019.network.message.Response;

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
    Response sendRequest(Request request) throws RemoteException;
}
