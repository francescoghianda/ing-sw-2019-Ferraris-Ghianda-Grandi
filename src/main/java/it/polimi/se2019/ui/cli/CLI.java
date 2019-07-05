package it.polimi.se2019.ui.cli;

import it.polimi.se2019.card.CardData;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.map.BlockData;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.network.OnServerDisconnectionListener;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Action;
import it.polimi.se2019.player.PlayerData;
import it.polimi.se2019.ui.GameEvent;
import it.polimi.se2019.ui.NetworkInterface;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.network.NetworkUtils;
import it.polimi.se2019.utils.string.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class CLI implements UI, OnServerDisconnectionListener
{
    private NetworkInterface client;
    private GameData gameData;

    private volatile boolean firstUpdate;

    public CLI()
    {
        client = new NetworkInterface(this);
    }

    @Override
    public synchronized void startUI()
    {
        GameConsole.startConsole();
        GameConsole.println(Strings.TITLE);
        firstUpdate = true;
        Option<Integer> serverModeOption = new Options<Integer>(Strings.GET_CONNECTION_MODE, true).addOption("Socket", NetworkInterface.SOCKET_MODE).addOption("RMI", NetworkInterface.RMI_MODE).show();
        String serverIp = new FormattedInput(Strings.GET_SERVER_IP, NetworkUtils::isIp).show();
        int serverPort = Integer.parseInt(new FormattedInput(Strings.GET_SERVER_PORT, FormattedInput.NUMERIC_REGEX, s -> NetworkUtils.isValidPort(Integer.parseInt(s))).show());
        GameConsole.println(Strings.CONNECTING);
        client.connect(serverIp, serverPort, serverModeOption.getValue());
        client.addOnServerDeisconnectionListener(this);
    }

    @Override
    public  String login()
    {
        return new FormattedInput(Strings.GET_USERNAME, FormattedInput.USERNAME_REGEX).show();
    }

    @Override
    public void logged()
    {
        GameConsole.println(Strings.LOGGED);
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
            GameConsole.println(Strings.IS_YOUR_ROUND);
    }

    @Override
    public void roundEnd()
    {
            GameConsole.println(Strings.ROUND_FINISHED);
    }

    @Override
    public void timeout()
    {
        CancelableReader.cancelAll();
    }

    @Override
    public void notifyImpossibleAction()
    {
            GameConsole.println(Strings.IMPOSSIBLE_ACTION);
    }

    @Override
    public void showNotification(String text)
    {
        GameConsole.println(text);
    }

    @Override
    public void closeConnection()
    {

    }

    @Override
    public boolean showScoreBoardAndChooseIfPlayAgain(ArrayList<PlayerData> scoreBoard)
    {
        if(gameData.getPlayer().getUsername().equals(scoreBoard.get(0).getUsername()))
        {
            GameConsole.printlnColored(GameColor.GREEN, "Hai vinto!");
        }
        else
        {
            GameConsole.printlnColored(GameColor.RED, "Hai perso");
        }

        GameConsole.println("Classifica:");
        GameConsole.println("");
        int i = 0;
        for(PlayerData playerData : scoreBoard)
        {
            GameConsole.println(i+") "+playerData.getUsername()+" con "+playerData.getGameBoard().getPoints()+" punti");
            i++;
        }

        return ask("Vuoi giocare ancora? ", true);
    }

    private boolean ask(String question, boolean firstDefault)
    {
        Options<String> options = new Options<>(question, firstDefault);
        options.addOption("Si").addOption("No");
        String answer = options.show().getOption();
        return answer.equals("Si");
    }

    @Override
    public boolean notEnoughAmmo(boolean askToSellPowerUp)
    {
        GameConsole.println("Non hai abbastanza munizioni");
        if(!askToSellPowerUp)return false;

        return ask("Vuoi vendere un power-up?", false);
    }

    @Override
    public String chooseOrCancel(Bundle<String, ArrayList<String>> bundle) throws CanceledActionException
    {
        Options<Void> options = new Options<>(bundle.getFirst(), true);
        bundle.getSecond().forEach(options::addOption);
        return options.showCancellable().getOption();
    }

    @Override
    public String choose(Bundle<String, ArrayList<String>> bundle)
    {
        Options<Void> options = new Options<>(bundle.getFirst(),false);
        bundle.getSecond().forEach(options::addOption);
        return options.show().getOption();
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
        Options<Action> actionOptions = new Options<>(Strings.CHOOSE_ACTION, true);

        actionOptions.addOptions(Arrays.asList(possibleActions), this::actionToString);

        actionOptions.addOption("Termina azione", Action.END_ACTION);
        actionOptions.addOption("Termina turno", Action.END_ROUND);

        Action chosenAction = actionOptions.show().getValue();
        CardData card = null;

        if (chosenAction == Action.USE_POWER_UP)
        {
            Options<CardData> powerUpOptions = new Options<>(Strings.SELECT_A_POWERUP, false);
            powerUpOptions.addOptions(gameData.getPlayer().getPowerUps(), CardData::toString);
            card = powerUpOptions.show().getValue();
        }

        return Bundle.ofSecondNullable(chosenAction, card);
    }

    @Override
    public Coordinates chooseBlock(int maxDistance) throws CanceledActionException
    {
        List<BlockData> blocks = gameData.getMap().getBlocksAsList();
        int playerX = gameData.getPlayer().getX();
        int playerY = gameData.getPlayer().getY();
        BlockData playerBlock = gameData.getMap().getBlock(playerX,playerY);
        List<Coordinates> coordinatesList = blocks.stream().filter(blockData->
        {
            int distance = gameData.getMap().getDistance(playerBlock,blockData);
            return distance <= maxDistance;
        }).map(BlockData::getCoordinates).collect(Collectors.toList());
        Options<Coordinates> coordinatesOptions = new Options<>(Strings.CHOOSE_THE_BLOCK, false);
        coordinatesOptions.addOptions(coordinatesList,Coordinates::toString);
        return coordinatesOptions.showCancellable().getValue();
    }

    @Override
    public Coordinates chooseBlockFrom(ArrayList<Coordinates> coordinates) throws CanceledActionException
    {
        Options<Coordinates> coordinatesOptions = new Options<> (Strings.CHOOSE_BLOCK_FROM, false);
        coordinatesOptions.addOptions(coordinates,Coordinates::toString);
        return coordinatesOptions.showCancellable().getValue();
    }

    @Override
    public CardData chooseWeaponFromPlayer() throws CanceledActionException
    {
        List<CardData> playerWeapon = gameData.getPlayer().getWeapons();
        Options<CardData> weaponOption = new Options<>(Strings.SELECT_A_WEAPON, false);
        weaponOption.addOptions(playerWeapon, CardData::toString);
        return weaponOption.showCancellable().getValue();
    }

    @Override
    public CardData chooseWeaponFromBlock() throws CanceledActionException
    {
        int playerX = gameData.getPlayer().getX();
        int playerY = gameData.getPlayer().getY();
        List<CardData> weaponFromBlock = gameData.getMap().getBlock(playerX,playerY).getWeaponCards();
        Options<CardData> weaponFromB = new Options<>(Strings.SELECT_A_POWERUP,false);
        weaponFromB.addOptions(weaponFromBlock, CardData::toString);
        return weaponFromB.showCancellable().getValue();
    }

    @Override
    public CardData choosePowerUp() throws CanceledActionException
    {
        List<CardData> playerPowerUps = gameData.getPlayer().getPowerUps();
        Options<CardData> powerupOption = new Options<>(Strings.SELECT_A_POWERUP, false);
        powerupOption.addOptions(playerPowerUps,CardData::toString);
        return powerupOption.showCancellable().getValue();

    }

    @Override
    public ArrayList<CardData> chooseWeaponsToReload(ArrayList<CardData> weapons)
    {
        Options<CardData> weaponsToReloadOptions = new Options<> (Strings.CHOOSE_WEAPON_TO_RELOAD, false);
        weaponsToReloadOptions.addOptions(weapons,CardData::toString);
        weaponsToReloadOptions.addOption("Annulla", null);
        return weaponsToReloadOptions.showMultipleSelectable().values();
    }

    @Override
    public CardData chooseWeaponToReload(ArrayList<CardData> weapons)
    {
        Options<CardData> weaponToReloadOptions = new Options<> (Strings.CHOOSE_WEAPON_TO_RELOAD, false);
        weaponToReloadOptions.addOptions(weapons,CardData::toString);
        weaponToReloadOptions.addOption("Annulla", null);
        return weaponToReloadOptions.show().getValue();
    }

    @Override
    public CardData chooseWeaponToSwap() throws CanceledActionException
    {
        ArrayList<CardData> weapons = gameData.getPlayer().getWeapons();

        Options<CardData> options = new Options<>("Seleziona l'arma che vuoi scambiare: ", false);
        options.addOptions(weapons, CardData::toString);
        return options.showCancellable().getValue();
    }

    @Override
    public void update(GameData data)
    {
        gameData = data;
        if(!firstUpdate)
        {
            GameConsole.clear();
            GameConsole.println(gameData.getMap().getCliMap());
        }
        firstUpdate = false;

    }

    @Override
    public void handle(GameEvent event)
    {
        if(event.getEventCode() == GameEvent.IS_YOUR_ROUND)
        {
            GameConsole.println(Strings.IS_YOUR_ROUND);
        }
    }


    private String actionToString(Action action)
    {
        switch (action)
        {
            case FIRE:
                return "Spara";
            case MOVE:
                return "Muoviti";
            case GRAB:
                return "Raccogli";
            case RELOAD:
                return "Ricarica";
            case USE_POWER_UP:
                return "Usa power-up";
        }
        return "";
    }

    @Override
    public void onServerDisconnection()
    {
        GameConsole.println("Hai perso la connesione col server!");
        System.exit(0);
    }
}
