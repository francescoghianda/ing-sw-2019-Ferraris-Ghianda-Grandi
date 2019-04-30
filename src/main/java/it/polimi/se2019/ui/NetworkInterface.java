package it.polimi.se2019.ui;

import it.polimi.se2019.network.NetworkClient;
import it.polimi.se2019.network.rmi.client.RmiClient;
import it.polimi.se2019.network.socket.client.SocketClient;

/**
 * defines the interface
 */
public class NetworkInterface
{
    public static final int SOCKET_MODE = 0;
    public static final int RMI_MODE = 1;

    private int connectionMode;
    private NetworkClient client;
    private UI ui;

    public NetworkInterface(UI ui)
    {
        this.ui = ui;
    }

    public void connect(String serverIp, int serverPort, int connectionMode)
    {
        this.connectionMode = connectionMode;

        if (connectionMode == RMI_MODE) client = new RmiClient(ui);
        else client = new SocketClient(ui);
        client.connect(serverIp, serverPort);
    }
}
