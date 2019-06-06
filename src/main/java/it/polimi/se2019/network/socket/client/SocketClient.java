package it.polimi.se2019.network.socket.client;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.network.NetworkClient;
import it.polimi.se2019.network.message.*;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.logging.Logger;
import javafx.concurrent.Task;

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

                if(incomeMessage.getType() == Message.Type.REQUEST)
                {

                    Task<Void> messageTask = new Task<Void>()
                    {
                        @Override
                        protected Void call()
                        {
                            if(incomeMessage instanceof CancellableActionRequest)
                            {
                                try
                                {
                                    Serializable obj = ((CancellableActionRequest)incomeMessage).apply(getUI());
                                    sendMessageToServer(new Response("Response to "+incomeMessage.getMessage(), obj, Response.Status.OK));
                                }
                                catch (CanceledActionException e)
                                {
                                    sendMessageToServer(new Response("ACTION_CANCELED", null, Response.Status.ACTION_CANCELED));
                                }
                            }
                            else
                            {
                                Serializable obj = ((ActionRequest)incomeMessage).apply(getUI());
                                sendMessageToServer(new Response("Response to "+incomeMessage.getMessage(), obj, Response.Status.OK));
                            }

                            return null;
                        }
                    };

                    Thread messageThread = new Thread(messageTask);
                    messageThread.setDaemon(true);
                    messageThread.setName("Request ("+incomeMessage.getMessage()+") thread");
                    messageThread.start();

                }
                else if(incomeMessage.getType() == Message.Type.ASYNC_MESSAGE)
                {
                    Task<Void> messageTask = new Task<Void>()
                    {
                        @Override
                        protected Void call()
                        {
                            ((AsyncMessage)incomeMessage).accept(getUI());
                            return null;
                        }
                    };
                    messageTask.exceptionProperty().addListener((observable, oldValue, newValue) ->
                    {
                        newValue.printStackTrace();
                    });
                    Thread messageThread = new Thread(messageTask);
                    messageThread.setName("AsyncMessage ("+incomeMessage.getMessage()+") thread");
                    messageThread.start();
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
