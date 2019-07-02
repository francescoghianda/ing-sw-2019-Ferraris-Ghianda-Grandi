package it.polimi.se2019.ui.cli;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.CardData;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.map.BlockData;
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
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 *
 */
public class CLI implements UI
{
    private NetworkInterface client;
    private GameData gameData;

    public CLI()
    {
        client = new NetworkInterface(this);
    }

    @Override
    public synchronized void startUI()
    {
        GameConsole.startConsole();
        GameConsole.println(Strings.TITLE);
        Option<Integer> serverModeOption = new Options<Integer>(Strings.GET_CONNECTION_MODE, true).addOption("Socket", NetworkInterface.SOCKET_MODE).addOption("RMI", NetworkInterface.RMI_MODE).show();
        String serverIp = new FormattedInput(Strings.GET_SERVER_IP, NetworkUtils::isIp).show();
        int serverPort = Integer.parseInt(new FormattedInput(Strings.GET_SERVER_PORT, FormattedInput.NUMERIC_REGEX, s -> NetworkUtils.isValidPort(Integer.parseInt(s))).show());
        GameConsole.println(Strings.CONNECTING);
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
        GameConsole.println(Strings.LOGGED);
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
        GameConsole.println(Strings.GAME_STARTING);
    }

    @Override
    public void gameStarted()
    {
        GameConsole.println(Strings.GAME_STARTED);
    }

    @Override
    public void showTimerCountdown(int remainSeconds)
    {
        GameConsole.println(remainSeconds);
    }

    @Override
    public void youAreFirstPlayer()
    {
        GameConsole.println(Strings.YOU_ARE_FIRST_PLAYER);
    }

    @Override
    public void firstPlayerIs(String firstPlayerUsername)
    {
        GameConsole.printf(Strings.FIRST_PLAYER_IS+"\n", firstPlayerUsername);
    }

    @Override
    public void connectionRefused()
    {
        GameConsole.println(Strings.CONNECTION_REFUSED);
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
    public void notifyImpossibleAction()
    {
        //TODO
    }

    @Override
    public void showNotification(String text)
    {

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
        bundle.getSecond().forEach(options::addOption);
        return options.show().getOption();
    }

    @Override
    public String choose(Bundle<String, ArrayList<String>> options)
    {
        //TODO
        return null;
    }

    @Override
    public String chooseSpawnPoint(CardData option1, CardData option2)
    {
        Options<CardData> powerUpCardOptions = new Options<>(Strings.CHOOSE_SPAWN_POINT, false);
        powerUpCardOptions.addOption(option1.toString(), option1).addOption(option2.toString(), option2);
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

        List<BlockData> blocks = gameData.getMap().getBlocksAsList();
        int playerX = gameData.getPlayer().getX();
        int playerY = gameData.getPlayer().getY();
        BlockData playerBlock = gameData.getMap().getBlock(playerX, playerY);

        List<Coordinates> coordinates = blocks.stream().filter(blockData ->
        {
           int distance = gameData.getMap().getDistance(playerBlock, blockData);
           return distance <= maxDistance;
        }).map(BlockData::getCoordinates).collect(Collectors.toList());


        Options<Coordinates> coordinatesOptions = new Options<>("aa", false);

        coordinatesOptions.addOptions(coordinates, Coordinates::toString);

        return coordinatesOptions.showCancellable().getValue();
    }

    @Override
    public Coordinates chooseBlockFrom(ArrayList<Coordinates> coordinates) throws CanceledActionException
    {
        //TODO
        return null;
    }

    @Override
    public CardData chooseWeaponFromPlayer() throws CanceledActionException
    {
        //TODO
        return null;
    }

    @Override
    public CardData chooseWeaponFromBlock() throws CanceledActionException
    {
        //TODO
        return null;
    }

    @Override
    public CardData choosePowerUp() throws CanceledActionException
    {
        //TODO
        return null;
    }

    @Override
    public ArrayList<CardData> chooseWeaponsToReload(ArrayList<CardData> weapons)
    {
        //TODO
        return null;
    }

    @Override
    public CardData chooseWeaponToReload(ArrayList<CardData> weapons)
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
            GameConsole.println(Strings.IS_YOUR_ROUND);
        }
    }
}
