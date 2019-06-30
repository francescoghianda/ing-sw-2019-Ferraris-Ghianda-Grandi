package it.polimi.se2019.network.socket.server;

import it.polimi.se2019.controller.*;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.OnClientDisconnectionListener;
import it.polimi.se2019.network.User;
import it.polimi.se2019.network.message.*;
import it.polimi.se2019.player.VirtualView;
import it.polimi.se2019.utils.logging.Logger;
import it.polimi.se2019.utils.timer.Timer;
import it.polimi.se2019.utils.timer.TimerAdapter;
import it.polimi.se2019.utils.timer.TimerListener;

import java.io.*;
import java.net.Socket;
import java.sql.Time;

public class SocketClientConnection implements Runnable, ClientConnection
{
    private Thread userThread;
    private volatile boolean running;
    private volatile boolean connected;
    private volatile boolean logged;
    private Socket client;
    private SocketServer server;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private ClientsManager clientsManager;

    private MessageHandler messageHandler;

    private OnClientDisconnectionListener clientDisconnectionListener;

    private volatile boolean timeout;
    private volatile boolean getResponse;
    private volatile Response response;
    private volatile String requestId;

    private Thread requestThread;
    private Timer requestTimeOutTimer;

    private final User user;
    private final VirtualView view;

    SocketClientConnection(Socket client, SocketServer server) throws IOException
    {
        this.clientsManager = ClientsManager.getInstance();

        this.client = client;
        this.server = server;
        this.connected = true;

        client.setTcpNoDelay(true);

        this.user = new User();
        this.view = new VirtualView(this);
        this.messageHandler = new MessageHandler(this);

        try
        {
            this.ois = new ObjectInputStream(client.getInputStream());
            this.oos = new ObjectOutputStream(client.getOutputStream());
        }
        catch (IOException e)
        {
            client.close();
            throw new IOException(e.getCause());
        }

    }

    void start()
    {
        if(!running || !userThread.isAlive())
        {
            this.userThread = new Thread(this);
            this.running = true;
            this.userThread.start();
            view.login();
        }
    }

    public void stop()
    {
        try
        {
            running = false;
            connected = false;
            client.close();
            ///TODO informare gli altri client della disconnessione
        }
        catch (IOException e)
        {
            Logger.exception(e);
        }
    }

    public void run()
    {
        while(running)
        {
            try
            {
                Message message = (Message) ois.readObject();

                if(message.getType() == Message.Type.RESPONSE)
                {
                    if(getResponse && ((Response)message).getRequestId().equals(requestId))
                    {
                        synchronized (this)
                        {
                            response = (Response) message;
                            getResponse = false;
                            this.notifyAll();
                        }
                    }
                }
                else
                {
                    Thread messageThread = new Thread(() -> messageHandler.handle(message));
                    messageThread.setDaemon(true);
                    messageThread.setName("Thread handling message "+message);
                    messageThread.start();
                }

            }
            catch (ClassNotFoundException | NullPointerException | IOException e)
            {
                Thread.currentThread().interrupt();
                running = false;
                connected = false;
                if(requestThread != null)requestThread.interrupt();
                else lostConnection();
            }
        }
    }

    public void sendMessageToClient(AsyncMessage message)
    {
        sendMessageToClient((Message) message);
    }

    @Override
    public void notifyOtherClients(AsyncMessage message)
    {
        clientsManager.getConnectedClients().forEach(clientConnection ->
        {
            if(!clientConnection.equals(this))clientConnection.sendMessageToClient(message);
        });
    }

    private synchronized void sendMessageToClient(Message message)
    {
        if(!connected)return;
        try
        {
            oos.writeUnshared(message);
            oos.flush();
        }
        catch (IOException e)
        {
            lostConnection();
            //userThread.interrupt();
            Logger.exception(e);
            throw new ConnectionErrorException();
        }
    }

    @Override
    public User getUser()
    {
        return this.user;
    }

    @Override
    public VirtualView getVirtualView()
    {
        return this.view;
    }

    @Override
    public synchronized Response getResponseTo(CancellableActionRequest request, TimeoutTime timeoutTime) throws CanceledActionException, ConnectionErrorException, TimeOutException
    {
        if(!connected)throw new ConnectionErrorException();
        try
        {
            sendRequest(request, timeoutTime);
            if(response.getStatus() == Response.Status.ACTION_CANCELED)throw new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER);
            return response;
        }
        catch (InterruptedException e)
        {
            requestThread = null;
            lostConnection();
            throw new ConnectionErrorException();
        }
    }

    @Override
    public synchronized Response getResponseTo(ActionRequest request, TimeoutTime timeoutTime) throws ConnectionErrorException, TimeOutException
    {
        if(!connected)throw new ConnectionErrorException();
        try
        {
            sendRequest(request, timeoutTime);
            return response;
        }
        catch (InterruptedException e)
        {
            requestThread = null;
            lostConnection();
            throw new ConnectionErrorException();
        }
    }

    private synchronized void sendRequest(Message request, TimeoutTime timeoutTime) throws InterruptedException, ConnectionErrorException, TimeOutException
    {
        requestThread = Thread.currentThread();
        requestId = request.getMessageId();
        getResponse = true;
        timeout = false;

        if(!timeoutTime.isIndeterminate())startTimer(request.getMessage(), timeoutTime.getSeconds());

        sendMessageToClient(request);
        while (getResponse)
        {
            if(timeout)
            {
                timeout = false;
                getResponse = false;
                requestThread = null;
                throw new TimeOutException();
            }
            this.wait();
        }
        if(requestTimeOutTimer != null)requestTimeOutTimer.stop();
        requestThread = null;
    }

    private synchronized void startTimer(String requestMessage, int timeoutSeconds)
    {
        String timerName = getUser().getUsername()+"-"+requestMessage+"-timer";
        Timer.destroyTimer(timerName);
        requestTimeOutTimer = Timer.createTimer(timerName, timeoutSeconds);
        requestTimeOutTimer.addTimerListener(new TimerAdapter() {
            @Override
            public void onTimerEnd(String timerName)
            {
                synchronized (SocketClientConnection.this)
                {
                    timeout = true;
                    SocketClientConnection.this.notifyAll();
                }
            }
        });
        requestTimeOutTimer.start();
    }

    private void lostConnection()
    {
        stop();
        connected = false;
        Logger.warning("Client "+user.getUsername()+" has disconnected!");
        ClientsManager.getInstance().unregisterClient(this);
    }

    @Override
    public void setLogged(boolean logged, boolean reconnected)
    {
        this.logged = logged;
        if(!reconnected && logged)
        {
            user.setMatch(MatchManager.getInstance().getMatch());
            user.setPlayer(user.getMatch().createPlayer(this));
        }
    }

    @Override
    public boolean isLogged()
    {
        return this.logged;
    }


    @Override
    public GameController getGameController()
    {
        return user.getMatch().getGameController();
    }

    @Override
    public boolean isConnected()
    {
        return this.connected;
    }

    @Override
    public void setConnected(boolean connected)
    {
        this.connected = connected;
    }

    @Override
    public Match getMatch()
    {
        return user.getMatch();
    }

    @Override
    public SocketServer getServer()
    {
        return server;
    }

    public SocketClientConnection setOnClientDisconnectionListener(OnClientDisconnectionListener listener)
    {
        this.clientDisconnectionListener = listener;
        return this;
    }
}
