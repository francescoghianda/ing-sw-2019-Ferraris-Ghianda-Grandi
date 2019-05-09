package it.polimi.se2019.ui.cli;

import it.polimi.se2019.map.Block;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.ui.NetworkInterface;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 */
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
    public synchronized void startUI()
    {
        Logger.getInstance().enableGameMode(true);
        Logger.cli(CliString.TITLE);
        Option<Integer> serverModeOption = new Options<Integer>(CliString.GET_CONNECTION_MODE, true).addOption("Socket", "S", NetworkInterface.SOCKET_MODE).addOption("RMI", "R", NetworkInterface.RMI_MODE).show();
        String serverIp = new FormattedInput(CliString.GET_SERVER_IP, FormattedInput.IP_REGEX).show();
        int serverPort = Integer.parseInt(new FormattedInput(CliString.GET_SERVER_PORT, FormattedInput.NUMERIC_REGEX, port -> Integer.parseInt(port) >= 1024 && Integer.parseInt(port) <= 65535).show());
        Logger.cli(CliString.CONNECTING);
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
        return new FormattedInput(CliString.GET_USERNAME, FormattedInput.USERNAME_REGEX).show();
    }

    @Override
    public void logged()
    {
        Logger.cli(CliString.LOGGED);
    }

    @Override
    public Player selectPlayer()
    {
        //TODO
        return null;
    }

    @Override
    public Block selectBlock()
    {
        //TODO
        return null;
    }

    @Override
    public void gameIsStarting()
    {
        Logger.cli(CliString.GAME_STARTING);
    }

    @Override
    public void gameStarted()
    {

    }

    @Override
    public void showTimerCountdown(int remainSeconds)
    {
        Logger.cli(remainSeconds);
    }

    @Override
    public void youAreFirstPlayer()
    {
        Logger.cli(CliString.YOU_ARE_FIRST_PLAYER);
    }

    @Override
    public void firstPlayerIs(String firstPlayerUsername)
    {
        Logger.cli(String.format(CliString.FIRST_PLAYER_IS, firstPlayerUsername));
    }

    @Override
    public void connectionRefused()
    {

    }

    @Override
    public String choose(Bundle<String, ArrayList<String>> bundle)
    {
        Options<Void> options = new Options<>(bundle.getFirst(), true);
        bundle.getSecond().forEach(option -> options.addOption(option, option.substring(0, 1)));
        return options.show().getOption();
    }

    @Override
    public void update()
    {

    }
}
