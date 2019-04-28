package it.polimi.se2019;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.rmi.server.RmiServer;
import it.polimi.se2019.network.socket.server.SocketServer;
import it.polimi.se2019.ui.cli.FormattedInput;
import it.polimi.se2019.ui.cli.Option;
import it.polimi.se2019.ui.cli.Options;
import it.polimi.se2019.ui.cli.Strings;
import it.polimi.se2019.utils.logging.Logger;

import java.rmi.RemoteException;

public class ServerApp
{
    private static final int SOCKET_MODE = 0;
    private static final int RMI_MODE = 1;
    private static final String TITLE = "    _      _                   _ _             ___                      \n" +
            "   /_\\  __| |_ _ ___ _ _  __ _| (_)_ _  __ _  / __| ___ _ ___ _____ _ _ \n" +
            "  / _ \\/ _` | '_/ -_) ' \\/ _` | | | ' \\/ _` | \\__ \\/ -_) '_\\ V / -_) '_|\n" +
            " /_/ \\_\\__,_|_| \\___|_||_\\__,_|_|_|_||_\\__,_| |___/\\___|_|  \\_/\\___|_|  \n" +
            "                                                                        ";
    private NetworkServer server;

    public ServerApp()
    {

    }

    public static void main(String[] args)
    {
        new ServerApp().startServerCli();
    }

    private void startServer(int serverMode, int port)
    {
        GameController controller = new GameController(server);

        if(serverMode == SOCKET_MODE) server = new SocketServer(controller);
        else
         {
            try
            {
                server = new RmiServer(controller);
            }
            catch (RemoteException e)
            {
                Logger.exception(e);
            }
        }
        server.startServer(port);
    }

    private void startServerCli()
    {
        Logger.getInstance().enableGameMode(true);
        Logger.cli(TITLE);
        Option<Integer> serverMode = new Options<Integer>("Scegli la modalità di connessione:", true).addOption("Socket", "S", SOCKET_MODE).addOption("RMI", "R", RMI_MODE).show();
        int serverPort = Integer.parseInt(new FormattedInput(Strings.GET_SERVER_PORT, FormattedInput.NUMERIC_REGEX, port -> Integer.parseInt(port) >= 1024 && Integer.parseInt(port) <= 65535).setDefaultResponse(serverMode.getValue() == SOCKET_MODE ? "0" : "1099").show());
        Logger.getInstance().enableGameMode(false);
        startServer(serverMode.getValue(), serverPort);
    }
}
