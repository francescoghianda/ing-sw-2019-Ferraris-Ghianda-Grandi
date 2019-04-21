package it.polimi.se2019.network.socket.client;

import it.polimi.se2019.network.socket.message.NetworkMessageClient;
import it.polimi.se2019.network.socket.message.NetworkMessageServer;
import it.polimi.se2019.utils.logging.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable
{
    private Socket socket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private Thread thread;
    private boolean running;
    private boolean connectedToServer;

    public Client()
    {
        thread = new Thread(this);
    }

    public void start()
    {
        if(!running)thread.start();
    }

    public void stop()
    {
        try
        {
            running = false;
            ois.close();
            oos.close();
            socket.close();
        }
        catch (IOException e)
        {
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void run()
    {
        running = true;

        try
        {
            while(running)
            {
                NetworkMessageClient incomeMessage = (NetworkMessageClient) ois.readObject();
                incomeMessage.setClient(this).execute();
            }

            oos.close();
            ois.close();

        }
        catch (IOException | ClassNotFoundException e)
        {
            Logger.error(e.getMessage());
            running = false;
        }
    }

    public boolean connect(String serverIP, int serverPort)
    {
        if(connectedToServer)
        {
            Logger.warning("Already connected to a server!");
            return false;
        }
        try
        {
            socket = new Socket(InetAddress.getByName(serverIP), serverPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            connectedToServer = true;
            Logger.info("Connected!");
        }
        catch (IOException e)
        {
            Logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public void writeToServer(NetworkMessageServer<?> message)
    {
        try
        {
            oos.writeObject(message);
            oos.flush();
        }
        catch (IOException e)
        {
            Logger.error(e.getMessage());
        }
    }

}
