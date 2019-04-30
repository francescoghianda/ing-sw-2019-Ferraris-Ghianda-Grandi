package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.OnClientDisconnectionListener;

import java.rmi.RemoteException;

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
