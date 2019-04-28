package it.polimi.se2019.ui.cli;

import it.polimi.se2019.ui.NetworkInterface;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.logging.Logger;

import java.util.Scanner;

public class CLI implements UI
{
    private final Scanner scanner;
    private NetworkInterface client;


    public CLI()
    {
        scanner = new Scanner(System.in);
        client = new NetworkInterface(this);
    }

    @Override
    public synchronized void init()
    {
        Logger.getInstance().enableGameMode(true);
        Logger.cli(Strings.TITLE);
        Option<Integer> serverModeOption = new Options<Integer>(Strings.GET_CONNECTION_MODE, true).addOption("Socket", "S", NetworkInterface.SOCKET_MODE).addOption("RMI", "R", NetworkInterface.RMI_MODE).show();
        String serverIp = new FormattedInput(Strings.GET_SERVER_IP, FormattedInput.IP_REGEX).show();
        int serverPort = Integer.parseInt(new FormattedInput(Strings.GET_SERVER_PORT, FormattedInput.NUMERIC_REGEX, port -> Integer.parseInt(port) >= 1024 && Integer.parseInt(port) <= 65535).show());
        Logger.cli(Strings.CONNECTING);
        client.connect(serverIp, serverPort, serverModeOption.getValue());
    }

    private String input(String question)
    {
        Logger.inputCli(question);
        return scanner.nextLine();
    }

    @Override
    public String getUsername()
    {
        return new FormattedInput(Strings.GET_USERNAME, FormattedInput.USERNAME_REGEX).show();
    }

    @Override
    public void logged()
    {
        Logger.cli(Strings.LOGGED);
    }

    @Override
    public void update()
    {

    }
}
