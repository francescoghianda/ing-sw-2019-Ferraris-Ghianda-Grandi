package it.polimi.se2019.network.socket.client;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.network.NetworkClient;
import it.polimi.se2019.network.message.AsyncMessage;
import it.polimi.se2019.network.message.Message;
import it.polimi.se2019.network.message.Request;
import it.polimi.se2019.network.message.Response;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.logging.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

public class SocketClient implements Runnable, NetworkClient
{
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Thread thread;
    private boolean running;
    private boolean connectedToServer;
    private boolean logged;
    private UI ui;

    public SocketClient(UI ui)
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
                Message incomeMessage = (Message) ois.readObject();
                //incomeMessage.setClient(this).execute();
                if(incomeMessage.getType() == Message.Type.REQUEST)
                {
                    try
                    {
                        Serializable obj = ((Request)incomeMessage).apply(getUI());
                        sendMessageToServer(new Response("Response to "+incomeMessage.getMessage(), obj, Response.Status.OK));
                    }
                    catch (CanceledActionException e)
                    {
                        sendMessageToServer(new Response("ACTION_CANCELED", null, Response.Status.ACTION_CANCELED));
                    }
                }
                else if(incomeMessage.getType() == Message.Type.ASYNC_MESSAGE)
                {
                    ((AsyncMessage)incomeMessage).accept(getUI());
                }
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

    private void sendMessageToServer(Message message)
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
        return ui.getUsername();
    }

    @Override
    public void invalidNickname()
    {

    }

    @Override
    public void setLogged(boolean logged)
    {
        this.logged = logged;
        if(logged)ui.logged();
    }

    @Override
    public UI getUI()
    {
        return ui;
    }


    public Response getResponseTo(Request messageServer)
    {
        try
        {
            sendMessageToServer(messageServer);
            return (Response) ois.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            Logger.exception(e);
        }

        return null;
    }

}
