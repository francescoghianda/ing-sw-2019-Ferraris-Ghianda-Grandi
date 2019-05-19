package it.polimi.se2019.controller;

import it.polimi.se2019.card.ammo.AmmoCard;
import it.polimi.se2019.card.deck.Deck;
import it.polimi.se2019.card.deck.DeckFactory;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.card.weapon.OptionalEffect;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Map;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.network.message.Messages;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.constants.GameMode;
import it.polimi.se2019.utils.logging.Logger;
import it.polimi.se2019.utils.timer.Timer;
import it.polimi.se2019.utils.timer.TimerListener;
import it.polimi.se2019.utils.xml.NotValidXMLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class GameController implements TimerListener
{
    private Random random;
    private List<GameColor> availablePlayerColors;
    private List<Player> players;
    private Map map;
    private GameMode gameMode;
    private ClientsManager clientsManager;
    private Timer timer;

    private Deck<AmmoCard> ammoCardDeck;
    private Deck<WeaponCard> weaponCardDeck;
    private Deck<PowerUpCard> powerUpCardDeck;

    private int deaths;
    private int remainingSkulls;

    private RoundManager roundManager;

    private int playersForStart = 1;

    public GameController()
    {
        this.clientsManager = ClientsManager.getInstance();
        availablePlayerColors = new ArrayList<>(Arrays.asList(GameColor.values()));
        availablePlayerColors.remove(GameColor.RED);
        gameMode = GameMode.NORMAL;
        remainingSkulls = 8;
        deaths = 0;
        map = Map.createMap();
        random = new Random();
        players = new ArrayList<>();
        createDecks();
    }

    private void createDecks()
    {
        try
        {
            DeckFactory deckFactory = DeckFactory.newInstance("/xml/decks/decks.xml");
            ammoCardDeck = deckFactory.createAmmoDeck();
            weaponCardDeck = deckFactory.createWeaponDeck();
            powerUpCardDeck = deckFactory.createPowerUpDeck();
        }
        catch (NotValidXMLException e)
        {
            Logger.exception(e);
            System.exit(1);
        }
    }

    private void nextRound()
    {
        Player currentPlayer = roundManager.next();

        //System.out.println(roundManager.isFirstRound());
        firstRound(currentPlayer);

        //if(roundManager.isFirstRound())firstRound(currentPlayer);

    }

    private void firstRound(Player player)
    {
        PowerUpCard first = powerUpCardDeck.getFirstCard();
        PowerUpCard second = powerUpCardDeck.getFirstCard();
        String chosenId = (String) player.getResponseTo(Messages.CHOOSE_SPAWN_POINT.setParam(new Bundle<>(first, second))).getParam();

        PowerUpCard chosen = first.getId().equals(chosenId) ? first : second;

        Block spawnPoint = map.findRoomByColor(chosen.getColor()).getSpawnPoint();
        powerUpCardDeck.addCard(chosen);
        player.addPowerUpCard(chosen.equals(first) ? second : first);
        player.setBlock(spawnPoint);
        sendBroadcastUpdate();
    }

    public void startGame()
    {
        if(players.isEmpty())throw new StartGameWithoutPlayerException();
        shuffleDecks();
        refillMap();
        sendBroadcastUpdate();
        selectStartingPlayer();
        nextRound();
    }

    private void refillMap()
    {
        List<Block> blocks = map.getAllBlocks();
        blocks.forEach(block ->
        {
            if(block.isSpawnPoint())
            {
                while(!block.isAllWeaponsPresent() && !weaponCardDeck.isEmpty())
                {
                    block.addWeaponCard(weaponCardDeck.getFirstCard());
                }
            }
            else
            {
                if(!block.isAmmoCardPresent())block.setAmmoCard(ammoCardDeck.getFirstCard());
            }
        });
    }

    private void shuffleDecks()
    {
        ammoCardDeck.shuffle();
        weaponCardDeck.shuffle();
        powerUpCardDeck.shuffle();
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

        sendUpdate(player);

        if(players.size() >= playersForStart) startTimer();

        return player;
    }

    public void sendUpdate(Player player)
    {
        player.sendMessageToClient(Messages.UPDATE_DATA.setParam(getData(player)));
    }

    public GameData getData(Player player)
    {
        return new GameData(map.getData(), player.getData(), remainingSkulls, deaths);
    }

    public void sendBroadcastUpdate()
    {
        players.forEach(this::sendUpdate);
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
