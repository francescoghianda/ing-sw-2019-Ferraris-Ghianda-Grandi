package it.polimi.se2019.network.socket.client;

import it.polimi.se2019.network.NetworkClient;
import it.polimi.se2019.network.message.NetworkMessageClient;
import it.polimi.se2019.network.message.NetworkMessageServer;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.logging.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable, NetworkClient
{
    private Socket socket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private Thread thread;
    private boolean running;
    private boolean connectedToServer;
    private UI ui;

    public Client(UI ui)
    {
        this.ui = ui;
        thread = new Thread(this);
    }

    private void start()
    {
        if(!running)thread.start();
    }

    @Override
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
            Logger.exception(e);
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
            Logger.exception(e);
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
            Logger.exception(e);
            return false;
        }
        start();
        return true;
    }

    public void sendMessageToServer(NetworkMessageServer<?> message)
    {
        try
        {
            oos.writeObject(message);
            oos.flush();
        }
        catch (IOException e)
        {
            Logger.exception(e);
        }
    }

    @Override
    public String getUsername()
    {
        Scanner s = new Scanner(System.in);
        return s.nextLine();
    }

    @Override
    public NetworkMessageClient<?> getResponseTo(NetworkMessageServer<?> messageServer, NetworkMessageClient<?> currentMessage)
    {
        try
        {
            sendMessageToServer(messageServer);
            return (NetworkMessageClient) ois.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            Logger.exception(e);
        }

        return null;
    }

}
