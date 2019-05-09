package it.polimi.se2019.controller;

import it.polimi.se2019.card.PowerUpCard;
import it.polimi.se2019.card.weapon.OptionalEffect;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Map;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.message.Chooser;
import it.polimi.se2019.network.message.Messages;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.constants.GameMode;
import it.polimi.se2019.utils.logging.Logger;
import it.polimi.se2019.utils.timer.Timer;
import it.polimi.se2019.utils.timer.TimerListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class GameController implements TimerListener
{
    //private static GameController instance;
    private Random random;
    private List<GameColor> availablePlayerColors;
    private List<Player> players;
    private Map map;
    private GameMode gameMode;
    private ClientsManager clientsManager;
    private Timer timer;

    private RoundManager roundManager;

    private int playersForStart = 2;

    public GameController()
    {
        this.clientsManager = ClientsManager.getInstance();
        availablePlayerColors = new ArrayList<>(Arrays.asList(GameColor.values()));
        availablePlayerColors.remove(GameColor.RED);
        gameMode = GameMode.NORMAL;
        random = new Random();
        map = Map.createMap();
        players = new ArrayList<>();
    }

    /*public static GameController getInstance()
    {
        if(instance == null)instance = new GameController();
        return instance;
    }*/

    private void nextRound()
    {
        Player currentPlayer = roundManager.next();

        Chooser chooser = new Chooser("Come stai?", "Bene", "Male");

        Logger.info(chooser.getResponse(currentPlayer));
    }

    public void startGame()
    {
        if(players.isEmpty())throw new StartGameWithoutPlayerException();
        selectStartingPlayer();
        nextRound();
    }

    private void startTimer()
    {
        Timer.destroyTimer("startCountdown");
        timer = Timer.createTimer("startCountdown", 10);
        timer.addTimerListener(this);
        timer.start();
    }

    private void selectStartingPlayer()
    {
        Player firstPlayer = players.get(random.nextInt(players.size()));
        firstPlayer.setAsStartingPlayer(true);
        firstPlayer.sendMessageToClient(Messages.YOU_ARE_FIRST_PLAYER);
        firstPlayer.notifyOtherClients(Messages.FIRST_PLAYER_IS.setParam(firstPlayer.getClientConnection().getUsername()));
        roundManager = new RoundManager(players, firstPlayer);
    }

    public GameMode getGameMode()
    {
        return this.gameMode;
    }

    /**
     * creates a new player for the client
     * @param server connection between server and client for the player created
     * @return the created player
     */
    public Player createPlayer(ClientConnection server)
    {

        if(availablePlayerColors.isEmpty())throw new TooManyPlayerException();
        GameColor color = availablePlayerColors.get(random.nextInt(availablePlayerColors.size()));
        Player player = new Player(color, this, server);
        players.add(player);
        availablePlayerColors.remove(color);

        if(players.size() >= playersForStart) startTimer();

        return player;
    }

    public void movePlayer(Player player, Block block)
    {

    }

    public void useWeapon(Player player, WeaponCard weapon)
    {
        if(player.getWeapons().contains(weapon) && weapon.isLoad())
        {
            List<OptionalEffect> enabledFffects = weapon.getEnabledOptionalEffects();
        }

        weapon.setLoad(false);
    }

    public void usePowerUp(Player player, PowerUpCard powerUp)
    {

    }

    public void grab(Player player)
    {

    }

    public void reload(Player player, WeaponCard weapon)
    {

    }

    public void reloadAll(Player player)
    {

    }

    @Override
    public void onTimerStart(String timerName)
    {
        clientsManager.sendBroadcastMessage(Messages.GAME_IS_STARTING);
    }

    @Override
    public void onTimerTick(String timerName, int remainingTime)
    {
        clientsManager.sendBroadcastMessage(Messages.TIMER_SECONDS.setParam(remainingTime));
    }

    @Override
    public void onTimerEnd(String timerName)
    {
        try
        {
            clientsManager.sendBroadcastMessage(Messages.TIMER_SECONDS.setParam(0));
            Thread.sleep(1000);
            clientsManager.sendBroadcastMessage(Messages.GAME_IS_STARTED);
        }
        catch (InterruptedException e)
        {
            Logger.exception(e);
            Thread.currentThread().interrupt();
        }

        startGame();
    }
}
