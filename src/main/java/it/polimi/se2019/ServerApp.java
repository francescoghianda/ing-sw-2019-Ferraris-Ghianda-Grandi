package it.polimi.se2019;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.rmi.server.RmiServer;
import it.polimi.se2019.network.socket.server.SocketServer;
import it.polimi.se2019.ui.cli.FormattedInput;
import it.polimi.se2019.ui.cli.Option;
import it.polimi.se2019.ui.cli.Options;
import it.polimi.se2019.ui.cli.CliString;
import it.polimi.se2019.utils.constants.Ansi;
import it.polimi.se2019.utils.logging.LogMessage;
import it.polimi.se2019.utils.logging.Logger;
import it.polimi.se2019.utils.logging.LoggerOutputStream;
import it.polimi.se2019.utils.network.NetworkUtils;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.rmi.RemoteException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class ServerApp
{
    private static final int SOCKET_MODE = 0;
    private static final int RMI_MODE = 1;
    private static final int BOTH_SERVER_MODE = 2;

    private static final String TITLE = Ansi.YELLOW
            + "    _      _                   _ _           "+ Ansi.WHITE+"  ___                      \n" +
            Ansi.YELLOW+"   /_\\  __| |_ _ ___ _ _  __ _| (_)_ _  __ _  "+ Ansi.WHITE+"/ __| ___ _ ___ _____ _ _ \n" +
            Ansi.YELLOW+"  / _ \\/ _` | '_/ -_) ' \\/ _` | | | ' \\/ _` |"+ Ansi.WHITE+" \\__ \\/ -_) '_\\ V / -_) '_|\n" +
            Ansi.YELLOW+" /_/ \\_\\__,_|_| \\___|_||_\\__,_|_|_|_||_\\__,_|"+ Ansi.WHITE+" |___/\\___|_|  \\_/\\___|_|  \n" +
            "                                                                        ";
    private NetworkServer server1;
    private NetworkServer server2;

    public ServerApp()
    {

    }

    public static void main(String[] args) throws IOException
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
            if(serverMode == SOCKET_MODE)
            {
                server1 = new SocketServer(controller);
                server1.startServer(port1);
            }
            else if(serverMode == RMI_MODE)
            {
                try
                {
                    server1 = new RmiServer(controller);
                    server1.startServer(port2);
                }
                catch (RemoteException e)
                {
                    Logger.exception(e);
                }
            }
        }

    }

    private void startServerCli() throws IOException
    {
        Properties settings = new Properties();
        File settingsFile = new File("settings/serverSettings.xml");

        String socketPortStr = "";
        String rmiPortStr = "";
        String modeStr = "";

        if(!settingsFile.exists())
        {
            settingsFile.getParentFile().mkdirs();
            settingsFile.createNewFile();
            PrintWriter pw = new PrintWriter(new FileWriter(settingsFile));
            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
            pw.println("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
            pw.println("<properties>");
            pw.println("</properties>");
            pw.flush();
            pw.close();
        }

        settings.loadFromXML(new FileInputStream(settingsFile));


        socketPortStr = settings.getProperty("socketPort");
        rmiPortStr = settings.getProperty("rmiPort");
        modeStr = settings.getProperty("mode", ".");

        boolean input = false;

        int mode;
        switch (modeStr)
        {
            case "s":
                mode = SOCKET_MODE;
                break;
            case "r":
                mode = RMI_MODE;
                break;
            case "b":
                mode = BOTH_SERVER_MODE;
                break;
            default:
                mode = -1;
        }

        LoggerOutputStream outputStream = new LoggerOutputStream(AnsiConsole.out, LogMessage::getMessage, true);
        Logger.getInstance().disableConsole();
        Logger.getInstance().addOutput(outputStream);
        Logger.info(TITLE);
        Option<Integer> serverMode;
        if(mode == -1)
        {
            input = true;
            serverMode = new Options<Integer>("Scegli la modalità di connessione:", true).addOption("Socket", "S", SOCKET_MODE).addOption("RMI", "R", RMI_MODE).addOption("Entrambi", "E", BOTH_SERVER_MODE).show();
        }
        else serverMode = new Option<>("","", mode);

        int port1;
        int socketPort = 0;
        if(serverMode.getValue() == SOCKET_MODE || serverMode.getValue() == BOTH_SERVER_MODE)
        {
            if(socketPortStr == null || !NetworkUtils.isValidPort(socketPortStr))
            {
                input = true;
                socketPort = Integer.parseInt(new FormattedInput(CliString.GET_SERVER_PORT, FormattedInput.NUMERIC_REGEX, port -> Integer.parseInt(port) >= 1024 && Integer.parseInt(port) <= 65535).setDefaultResponse(serverMode.getValue() == RMI_MODE ? "1099" : "0").show());
            }
            else socketPort = Integer.parseInt(socketPortStr);
        }
        port1 = socketPort;

        int rmiPort = 0;
        if(serverMode.getValue() == RMI_MODE || serverMode.getValue() == BOTH_SERVER_MODE)
        {
            if(rmiPortStr == null || !NetworkUtils.isValidPort(rmiPortStr))
            {
                input = true;
                rmiPort = Integer.parseInt(new FormattedInput(CliString.GET_SERVER_PORT, FormattedInput.NUMERIC_REGEX, port -> Integer.parseInt(port) >= 1024 && Integer.parseInt(port) <= 65535 && Integer.parseInt(port) != port1).setDefaultResponse(serverMode.getValue() == RMI_MODE ? "1099" : "0").show());
            }
            else rmiPort = Integer.parseInt(rmiPortStr);
        }

        if(input)
        {
            Options<Character> saveSettingsOptions = new Options<>("Vuoi salvare le impostazioni attuali?", true);
            saveSettingsOptions.addOption("Si", "s", 's').addOption("No", "n", 'n');
            Character selected = saveSettingsOptions.show().getValue();
            if(selected == 's')
            {
                if(serverMode.getValue() == SOCKET_MODE || serverMode.getValue() == BOTH_SERVER_MODE)
                {
                    settings.setProperty("mode", "s");
                    settings.setProperty("socketPort", String.valueOf(socketPort));
                }
                if(serverMode.getValue() == RMI_MODE || serverMode.getValue() == BOTH_SERVER_MODE)
                {
                    settings.setProperty("mode", "r");
                    settings.setProperty("rmiPort", String.valueOf(rmiPort));
                }
                if(serverMode.getValue() == BOTH_SERVER_MODE)settings.setProperty("mode", "b");

                settings.storeToXML(new FileOutputStream("settings/serverSettings.xml"), "mode = s | r | b");
            }
        }

        Logger.getInstance().enableConsole();
        Logger.getInstance().removeOutput(outputStream);
        startServer(serverMode.getValue(), socketPort, rmiPort);
    }
}
