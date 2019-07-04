package it.polimi.se2019.network.socket.client;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.TimeOutException;
import it.polimi.se2019.network.NetworkClient;
import it.polimi.se2019.network.message.*;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.logging.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

/**
 * defines a client connected via socket connection
 */
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
                Message incomeMessage = (Message) ois.readUnshared();

                if(incomeMessage.getType() == Message.Type.REQUEST)
                {
                    Thread messageThread = new Thread(() ->
                    {
                        try
                        {
                            if(incomeMessage instanceof CancellableActionRequest)
                            {
                                try
                                {
                                    getUI().requestFocus();
                                    Serializable obj = ((CancellableActionRequest)incomeMessage).apply(getUI());
                                    sendMessageToServer(new Response("Response to "+incomeMessage.getMessage(), incomeMessage.getMessageId(), obj, Response.Status.OK));
                                }
                                catch (CanceledActionException e)
                                {
                                    sendMessageToServer(new Response("ACTION_CANCELED", incomeMessage.getMessageId(), null, Response.Status.ACTION_CANCELED));
                                }
                            }
                            else
                            {
                                getUI().requestFocus();
                                Serializable obj = ((ActionRequest)incomeMessage).apply(getUI());
                                sendMessageToServer(new Response("Response to "+incomeMessage.getMessage(), incomeMessage.getMessageId(), obj, Response.Status.OK));
                            }
                        }
                        catch (TimeOutException e)
                        {
                            Logger.info("Timeout on "+incomeMessage);
                        }
                    });
                    messageThread.setDaemon(true);
                    messageThread.setName("Request ("+incomeMessage.getMessage()+") thread");
                    messageThread.start();

                }
                else if(incomeMessage.getType() == Message.Type.ASYNC_MESSAGE)
                {
                    Thread messageThread = new Thread(() -> ((AsyncMessage)incomeMessage).accept(getUI()));
                    messageThread.setName("AsyncMessage ("+incomeMessage.getMessage()+") thread");
                    messageThread.start();
                }
            }

            socket.close();

        }
        catch (IOException | ClassNotFoundException e)
        {
            Logger.exception(e);
            stop();
        }
    }

    /**
     * creates the connection with teh server
     * @param serverIP is the ip of the server to connect to
     * @param serverPort The port of the server to connect to
     * @return a boolean value based on the success of the connection
     */
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

    public void sendAsyncMessageToServer(AsyncMessage message)
    {
        sendMessageToServer(message);
    }

    /**
     * sends a message from client to server
     * @param message
     */
    private synchronized void sendMessageToServer(Message message)
    {
        try
        {
            oos.writeUnshared(message);
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
        return ui.login();
    }

    @Override
    public void invalidNickname()
    {

    }

    @Override
    public void setLogged(boolean logged)
    {
        this.logged = logged;
    }

    @Override
    public UI getUI()
    {
        return ui;
    }

}
