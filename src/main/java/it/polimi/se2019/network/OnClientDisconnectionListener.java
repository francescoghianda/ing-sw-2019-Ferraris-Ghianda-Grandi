package it.polimi.se2019.network;

public interface OnClientDisconnectionListener
{
    /**
     * The method is invoked when a client lost connection
     * @param disconnectedClient The client that has lost connection
     */
    void onClientDisconnection(ClientConnection disconnectedClient);
}
