package it.polimi.se2019.ui.cli;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Action;
import it.polimi.se2019.ui.GameEvent;
import it.polimi.se2019.ui.NetworkInterface;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.network.NetworkUtils;
import it.polimi.se2019.utils.string.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 */
public class CLI implements UI
{
    private NetworkInterface client;

    public CLI()
    {
        client = new NetworkInterface(this);
    }

    @Override
    public synchronized void startUI()
    {
        GameConsole.startConsole();
        GameConsole.out.println(Strings.TITLE);
        Option<Integer> serverModeOption = new Options<Integer>(Strings.GET_CONNECTION_MODE, true).addOption("Socket", "S", NetworkInterface.SOCKET_MODE).addOption("RMI", "R", NetworkInterface.RMI_MODE).show();
        String serverIp = new FormattedInput(Strings.GET_SERVER_IP, NetworkUtils::isIp).show();
        int serverPort = Integer.parseInt(new FormattedInput(Strings.GET_SERVER_PORT, FormattedInput.NUMERIC_REGEX, s -> NetworkUtils.isValidPort(Integer.parseInt(s))).show());
        GameConsole.out.println(Strings.CONNECTING);
        client.connect(serverIp, serverPort, serverModeOption.getValue());
    }

    @Override
    public String login()
    {
        return new FormattedInput(Strings.GET_USERNAME, FormattedInput.USERNAME_REGEX).show();
    }

    @Override
    public void logged()
    {
        GameConsole.out.println(Strings.LOGGED);
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
    public void requestFocus()
    {
        //Do nothing
    }

    @Override
    public void gameIsStarting()
    {
        GameConsole.out.println(Strings.GAME_STARTING);
    }

    @Override
    public void gameStarted()
    {
        GameConsole.out.println(Strings.GAME_STARTED);
    }

    @Override
    public void showTimerCountdown(int remainSeconds)
    {
        GameConsole.out.println(remainSeconds);
    }

    @Override
    public void youAreFirstPlayer()
    {
        GameConsole.out.println(Strings.YOU_ARE_FIRST_PLAYER);
    }

    @Override
    public void firstPlayerIs(String firstPlayerUsername)
    {
        GameConsole.out.printf(Strings.FIRST_PLAYER_IS+"\n", firstPlayerUsername);
    }

    @Override
    public void connectionRefused()
    {
        GameConsole.out.println(Strings.CONNECTION_REFUSED);
    }

    @Override
    public void roundStart()
    {
        //TODO
    }

    @Override
    public void roundEnd()
    {
        //TODO
    }

    @Override
    public void timeout()
    {
        CancelableReader.cancelAll();
    }

    @Override
    public boolean notEnoughAmmo(boolean askToSellPowerUp)
    {
        //TODO
        return false;
    }

    @Override
    public String chooseOrCancel(Bundle<String, ArrayList<String>> bundle) throws CanceledActionException
    {
        Options<Void> options = new Options<>(bundle.getFirst(), true);
        bundle.getSecond().forEach(option -> options.addOption(option, option.substring(0, 1)));
        return options.show().getOption();
    }

    @Override
    public String choose(Bundle<String, ArrayList<String>> options)
    {
        //TODO
        return null;
    }

    @Override
    public String chooseSpawnPoint(Card option1, Card option2)
    {
        Options<Card> powerUpCardOptions = new Options<>(Strings.CHOOSE_SPAWN_POINT, false);
        powerUpCardOptions.addOption(option1.toString(), "1", option1).addOption(option2.toString(), "2", option2);
        return powerUpCardOptions.show().getValue().getId();
    }

    @Override
    public Bundle<Action, Serializable> chooseActionFrom(Action[] possibleActions)
    {
        //TODO
        return null;
    }

    @Override
    public Coordinates chooseBlock(int maxDistance) throws CanceledActionException
    {
        //TODO
        return null;
    }

    @Override
    public Coordinates chooseBlockFrom(ArrayList<Coordinates> coordinates) throws CanceledActionException
    {
        //TODO
        return null;
    }

    @Override
    public Card chooseWeaponFromPlayer() throws CanceledActionException
    {
        //TODO
        return null;
    }

    @Override
    public Card chooseWeaponFromBlock() throws CanceledActionException
    {
        //TODO
        return null;
    }

    @Override
    public Card choosePowerUp() throws CanceledActionException
    {
        //TODO
        return null;
    }

    @Override
    public ArrayList<Card> chooseWeaponsToReload(ArrayList<Card> weapons)
    {
        //TODO
        return null;
    }

    @Override
    public Card chooseWeaponToReload(ArrayList<Card> weapons)
    {
        //TODO
        return null;
    }

    @Override
    public void update(GameData data)
    {
        //TODO
    }

    @Override
    public void handle(GameEvent event)
    {
        if(event.getEventCode() == GameEvent.IS_YOUR_ROUND)
        {
            GameConsole.out.println(Strings.IS_YOUR_ROUND);
        }
    }
}
