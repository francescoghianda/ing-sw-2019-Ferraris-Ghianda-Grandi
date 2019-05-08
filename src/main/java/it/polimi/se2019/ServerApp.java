package it.polimi.se2019;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.rmi.server.RmiServer;
import it.polimi.se2019.network.socket.server.SocketServer;
import it.polimi.se2019.ui.cli.FormattedInput;
import it.polimi.se2019.ui.cli.Option;
import it.polimi.se2019.ui.cli.Options;
import it.polimi.se2019.ui.cli.Strings;
import it.polimi.se2019.utils.constants.AnsiColor;
import it.polimi.se2019.utils.logging.Logger;

import java.rmi.RemoteException;

public class ServerApp
{
    private static final int SOCKET_MODE = 0;
    private static final int RMI_MODE = 1;
    private static final int BOTH_SERVER_MODE = 2;

    private static final String TITLE = AnsiColor.YELLOW
            + "    _      _                   _ _           "+AnsiColor.WHITE+"  ___                      \n" +
            AnsiColor.YELLOW+"   /_\\  __| |_ _ ___ _ _  __ _| (_)_ _  __ _  "+AnsiColor.WHITE+"/ __| ___ _ ___ _____ _ _ \n" +
            AnsiColor.YELLOW+"  / _ \\/ _` | '_/ -_) ' \\/ _` | | | ' \\/ _` |"+AnsiColor.WHITE+" \\__ \\/ -_) '_\\ V / -_) '_|\n" +
            AnsiColor.YELLOW+" /_/ \\_\\__,_|_| \\___|_||_\\__,_|_|_|_||_\\__,_|"+AnsiColor.WHITE+" |___/\\___|_|  \\_/\\___|_|  \n" +
            "                                                                        ";
    private NetworkServer server1;
    private NetworkServer server2;

    public ServerApp()
    {

    }

    public static void main(String[] args)
    {
        new ServerApp().startServerCli();
    }

    private void startServer(int serverMode, int port1, int port2)
    {
        GameController controller = new GameController();

        if(serverMode == BOTH_SERVER_MODE)
        {
            try
            {
                server1 = new SocketServer(controller);
                server2 = new RmiServer(controller);
            }
            catch (RemoteException e)
            {
                Logger.exception(e);
            }

            server1.startServer(port1);
            server2.startServer(port2);
        }
        else
        {
            if(serverMode == SOCKET_MODE) server1 = new SocketServer(controller);
            else if(serverMode == RMI_MODE)
            {
                try
                {
                    server1 = new RmiServer(controller);
                }
                catch (RemoteException e)
                {
                    Logger.exception(e);
                }
            }

            server1.startServer(port1);
        }

    }

    private void startServerCli()
    {
        Logger.getInstance().enableGameMode(true);
        Logger.cli(TITLE);
        Option<Integer> serverMode = new Options<Integer>("Scegli la modalità di connessione:", true).addOption("Socket", "S", SOCKET_MODE).addOption("RMI", "R", RMI_MODE).addOption("Entrambi", "E", BOTH_SERVER_MODE).show();

        int port1 = Integer.parseInt(new FormattedInput(Strings.GET_SERVER_PORT, FormattedInput.NUMERIC_REGEX, port -> Integer.parseInt(port) >= 1024 && Integer.parseInt(port) <= 65535).setDefaultResponse(serverMode.getValue() == RMI_MODE ? "1099" : "0").show());
        int port2 = 0;
        if(serverMode.getValue() == BOTH_SERVER_MODE)port2 = Integer.parseInt(new FormattedInput(Strings.GET_SERVER_PORT, FormattedInput.NUMERIC_REGEX, port -> Integer.parseInt(port) >= 1024 && Integer.parseInt(port) <= 65535 && Integer.parseInt(port) != port1).setDefaultResponse(serverMode.getValue() == RMI_MODE ? "1099" : "0").show());
        Logger.getInstance().enableGameMode(false);
        startServer(serverMode.getValue(), port1, port2);
    }
}
