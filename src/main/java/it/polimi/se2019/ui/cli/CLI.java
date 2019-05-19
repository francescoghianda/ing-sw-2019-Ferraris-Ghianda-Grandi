package it.polimi.se2019.ui.cli;

import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.ui.GameEvent;
import it.polimi.se2019.ui.NetworkInterface;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.network.NetworkUtils;

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
        GameConsole.startConsole();
        GameConsole.out.println(CliString.TITLE);
        Option<Integer> serverModeOption = new Options<Integer>(CliString.GET_CONNECTION_MODE, true).addOption("Socket", "S", NetworkInterface.SOCKET_MODE).addOption("RMI", "R", NetworkInterface.RMI_MODE).show();
        String serverIp = new FormattedInput(CliString.GET_SERVER_IP, NetworkUtils::isIp).show();
        int serverPort = Integer.parseInt(new FormattedInput(CliString.GET_SERVER_PORT, FormattedInput.NUMERIC_REGEX, s -> NetworkUtils.isValidPort(Integer.parseInt(s))).show());
        GameConsole.out.println(CliString.CONNECTING);
        client.connect(serverIp, serverPort, serverModeOption.getValue());
    }

    private String input(String question)
    {
        GameConsole.nextLine(question);
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
        GameConsole.out.println(CliString.LOGGED);
    }

    @Override
    public String selectPlayer()
    {
        //TODO
        return null;
    }

    @Override
    public String selectBlock()
    {
        //TODO
        return null;
    }

    @Override
    public void gameIsStarting()
    {
        GameConsole.out.println(CliString.GAME_STARTING);
    }

    @Override
    public void gameStarted()
    {

    }

    @Override
    public void showTimerCountdown(int remainSeconds)
    {
        GameConsole.out.println(remainSeconds);
    }

    @Override
    public void youAreFirstPlayer()
    {
        GameConsole.out.println(CliString.YOU_ARE_FIRST_PLAYER);
    }

    @Override
    public void firstPlayerIs(String firstPlayerUsername)
    {
        GameConsole.out.printf(CliString.FIRST_PLAYER_IS+"\n", firstPlayerUsername);
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
    public PowerUpCard chooseSpawnPoint(PowerUpCard option1, PowerUpCard option2)
    {
        Options<PowerUpCard> powerUpCardOptions = new Options<>(CliString.CHOOSE_SPAWN_POINT, false);
        powerUpCardOptions.addOption(option1.toString(), "1", option1).addOption(option2.toString(), "2", option2);
        return powerUpCardOptions.show().getValue();
    }

    @Override
    public void update()
    {

    }

    @Override
    public void handle(GameEvent event)
    {
        if(event.getEventCode() == GameEvent.IS_YOUR_ROUND)
        {
            GameConsole.out.println(CliString.IS_YOUR_ROUND);
        }
    }
}
