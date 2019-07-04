package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.OnClientDisconnectionListener;

import java.rmi.RemoteException;

/**
 * Thread which checks if the  RMI client remains connected
 */
public class ConnectionController implements Runnable
{
    private final ClientConnection connectionInterface;
    private Thread thread;
    private volatile boolean running;
    private OnClientDisconnectionListener listener;


    ConnectionController(ClientConnection callbackInterface)
    {
        this.connectionInterface = callbackInterface;
    }

    /**
     * Starts the connection controller
     */
    void start()
    {
        if(!running)
        {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    ConnectionController setOnClientDisconnectionListener(OnClientDisconnectionListener listener)
    {
        this.listener = listener;
        return this;
    }

    /**
     * Stops the connection controller
     */
    void stop()
    {
        running = false;
    }

    @Override
    public void run()
    {
        try
        {
            while (running)
            {
                Thread.sleep(500);
                if(!((RmiClientConnection)connectionInterface).getCallback().isConnected())
                {
                    listener.onClientDisconnection(connectionInterface);
                    break;
                }
            }
        }
        catch (InterruptedException | RemoteException e)
        {
            listener.onClientDisconnection(connectionInterface);
            stop();
            Thread.currentThread().interrupt();
        }

    }
}
