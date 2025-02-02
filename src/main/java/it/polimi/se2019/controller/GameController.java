package it.polimi.se2019.controller;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.CardData;
import it.polimi.se2019.card.ammo.AmmoCard;
import it.polimi.se2019.card.deck.Deck;
import it.polimi.se2019.card.deck.DeckFactory;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.controller.action.*;
import it.polimi.se2019.controller.settings.MatchSettings;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.map.Map;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.User;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.network.message.ConnectionErrorException;
import it.polimi.se2019.player.*;
import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.constants.GameMode;
import it.polimi.se2019.utils.logging.Logger;
import it.polimi.se2019.utils.timer.Timer;
import it.polimi.se2019.utils.timer.TimerListener;
import it.polimi.se2019.utils.xml.NotValidXMLException;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * defines the gamecontroller chich includes the definition of the map, the gamemode, the initial timer,
 * the ammocard deck,  the weapon card deck, the powerupcard deck, the remaining skulls, the deaths , the round manager,
 * the last final frenzy player and the match
 */

public class GameController implements TimerListener
{
    private Map map;
    private GameMode gameMode;
    private int startTimerSeconds;

    private Deck<AmmoCard> ammoCardDeck;
    private Deck<WeaponCard> weaponCardDeck;
    private Deck<PowerUpCard> powerUpCardDeck;

    private AtomicInteger remainingSkulls;
    private List<Death> deaths;

    private RoundManager roundManager;
    private Player lastFinalFrenzyPlayer;

    private final Match match;

    /**
     * creates decks for the game controller of the specific match in normal gamemode
     * @param match
     */
    public GameController(Match match)
    {
        this.match = match;
        gameMode = GameMode.NORMAL;

        MatchSettings settings = MatchSettings.getInstance();

        remainingSkulls = new AtomicInteger(settings.getSkullNumber());
        startTimerSeconds = settings.getStartTimerSeconds();
        deaths = Collections.synchronizedList(new ArrayList<>());
        map = Map.createMap(settings.getMapNumber());

        createDecks();
    }

    /**
     * Manage the round ,going to the next one
     */
    private void nextRound()
    {
        Player currentPlayer = roundManager.next();

        if(!currentPlayer.getClientConnection().isConnected() || !currentPlayer.getClientConnection().isLogged())
        {
            if(currentPlayer.equals(lastFinalFrenzyPlayer))return;
            nextRound();
            return;
        }

        if(gameMode == GameMode.FINAL_FRENZY_BEFORE_FP && roundManager.isFirstPlayer(currentPlayer))gameMode = GameMode.FINAL_FRENZY_AFTER_FP;

        try
        {
            currentPlayer.getView().roundStart();
            notifyOtherClients(currentPlayer, virtualView -> virtualView.showNotification("È il turno di "+currentPlayer.getUsername()));

            if(roundManager.isFirstRound() || !currentPlayer.isFirstRoundPlayed()) firstRound(currentPlayer);

            for(int i = 0; i < gameMode.getPlayableAction(); i++)
            {
                currentPlayer.resetExecutedAction();
                boolean continueRound = executeAction(currentPlayer);
                if(!continueRound)break;
            }

            if(gameMode == GameMode.NORMAL)new ReloadAction(this, currentPlayer, ReloadAction.RELOAD_ALL).execute();
        }
        catch (ConnectionErrorException e)
        {
            Logger.error("Connection error during "+currentPlayer.getUsername()+"'s round!");
            notifyOtherClients(currentPlayer, virtualView -> virtualView.showNotification(currentPlayer.getUsername()+" si è disconnesso!"));
        }
        catch (TimeOutException e)
        {
            Logger.info("Player "+currentPlayer.getUsername()+" has finished his time");
        }

        currentPlayer.getView().roundEnd();
        currentPlayer.resetExecutedAction();
        refillMap();
        sendBroadcastUpdate();
        if(isFinalFrenzy())currentPlayer.setLastRoundPlayed(true);
        handleDeadPlayers(currentPlayer);
        if(isFinalFrenzy() && currentPlayer.equals(lastFinalFrenzyPlayer))return;
        checkForFinalFrenzy(currentPlayer);
        if(match.connectedPlayerSize() <= 0 || match.connectedPlayerSize() < MatchSettings.getInstance().getMinPlayers())return;
        nextRound();

    }

    /**
     *
     * @return true if the match gameMode is final frenzy
     */

    private boolean isFinalFrenzy()
    {
        return gameMode == GameMode.FINAL_FRENZY_AFTER_FP || gameMode == GameMode.FINAL_FRENZY_BEFORE_FP;
    }

    /**
     * Terminate the match
     */
    private void endMatch()
    {
        match.setState(Match.State.ENDED);
        Logger.info("The match "+match.getMatchId()+" is ended");

        ClientsManager.getInstance().deleteDisconnectedClients(match);
        match.getPlayers().stream().filter(player -> player.getGameBoard().getTotalReceivedDamage() > 0).forEach(player -> new CountPointsAction(this, player).execute());
        new CountKillShotTrackAction(this, deaths).execute();
        ArrayList<PlayerData> scoreBoard = getPlayers().stream().sorted((p1, p2) -> Integer.compare(p2.getGameBoard().getPoints(), p1.getGameBoard().getPoints())).map(Player::getData).collect(Collectors.toCollection(ArrayList::new));

        sendBroadCastMessage(ui -> ui.showNotification("Partita terminata"));
        sendBroadCastMessage(ui -> ui.showNotification("Vince "+scoreBoard.get(0).getUsername()));

        match.getPlayers().stream().filter(player -> player.getClientConnection().isConnected()).forEach(player ->
        {
            Thread thread = new Thread(() ->
            {
                boolean playAgain = player.getView().showScoreBoardAndChooseIfPlayAgain(scoreBoard);

                if(playAgain)
                {
                    User user = player.getClientConnection().getUser();
                    user.setMatch(MatchManager.getInstance().getMatch());
                    user.setPlayer(user.getMatch().createPlayer(player.getClientConnection()));
                    player.getView().logged();
                }
                else
                {
                    ClientsManager.getInstance().unregisterClient(player.getClientConnection());
                    player.getClientConnection().stop();
                }

            });

            thread.start();
        });


    }

    /**
     *
     * @param color the color of the player who wants to search
     * @return return an Optional containing the player of that color if exist, otherwise an empty Optional
     */
    public Optional<Player> findPlayerByColor(GameColor color)
    {
        return match.getPlayers().stream().filter(player -> player.getColor() == color).findFirst();
    }

    private void checkForFinalFrenzy(Player currentPlayer)
    {
        if(remainingSkulls.get() > 0 || gameMode.isFinalFrenzy())return;
        gameMode = GameMode.FINAL_FRENZY_BEFORE_FP;

        lastFinalFrenzyPlayer = currentPlayer;

        match.getPlayers().forEach(player ->
        {
            if(player.getGameBoard().getTotalReceivedDamage() <= 0)
            {
                player.setFinalFrenzyMode(true);
            }
        });

        sendBroadcastUpdate();
    }

    private boolean executeAction(Player currentPlayer)
    {
        Action[] possibleActions;

        while ((possibleActions = ActionsGroup.getPossibleActions(currentPlayer)).length > 0)
        {
            Bundle<Action, Serializable> chosenBundle = currentPlayer.getView().chooseActionFrom(possibleActions);

            Action chosen = chosenBundle.getFirst();
            Object optionalObject = chosenBundle.getSecond();

            if(chosen == Action.END_ROUND)return false;
            if(chosen == Action.END_ACTION)break;

            try
            {
                executeAction(chosen, currentPlayer, optionalObject);
            }
            catch (CanceledActionException e)
            {
                Logger.warning("The action "+chosen+" of player "+currentPlayer+" was canceled. Cause: "+e.getCanceledCause());
            }
            catch (ImpossibleActionException e)
            {
                Logger.warning("Impossible action: "+chosen+"; "+e.cause()+" - Player: "+currentPlayer);
                currentPlayer.getView().notifyImpossibleAction();
            }


            sendBroadcastUpdate();


        }
        return true;
    }

    private void executeAction(Action action, Player player, Object optionalObject) throws CanceledActionException, ImpossibleActionException
    {
        ControllerAction controllerAction;

        switch (action)
        {
            case MOVE:
                controllerAction = new MoveAction(this, player, selectBlock(player));
                break;
            case GRAB:
                controllerAction = new GrabAction(this, player);
                break;
            case FIRE:
                controllerAction = new FireAction(this, player);
                break;
            case USE_POWER_UP:
                controllerAction = new UsePowerUpAction(this, player, PowerUpCard.findById(((CardData)optionalObject).getId()));
                break;
            case RELOAD:
                controllerAction = new ReloadAction(this, player, ReloadAction.RELOAD_ONE);
                break;
            default:
                controllerAction = null;
                break;
        }

        if(controllerAction != null)controllerAction.execute();
    }

    /**
     * Spawn the player for the first time
     * @param player the player to be spawned
     */
    private void firstRound(Player player)
    {
        PowerUpCard option1 = powerUpCardDeck.getFirstCard();
        PowerUpCard option2 = powerUpCardDeck.getFirstCard();
        try
        {
            String chosenId = player.getView().chooseSpawnPoint(option1.getCardData(), option2.getCardData());
            PowerUpCard chosen = option1.getId().equals(chosenId) ? option1 : option2;
            Block spawnPoint = map.findRoomByColor(chosen.getColor()).getSpawnPoint();
            powerUpCardDeck.addCard(chosen);
            player.addPowerUpCard(chosen.equals(option1) ? option2 : option1);
            player.setBlock(spawnPoint);
            player.setFirstRoundPlayed(true);
            sendBroadcastUpdate();
        }
        catch (ConnectionErrorException | TimeOutException e)
        {
            powerUpCardDeck.addCard(option1);
            powerUpCardDeck.addCard(option2);
            throw e;
        }

    }

    /**
     * Check for each block of the map, if there is a card missing and insert a new card from the deck
     */
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

    private void handleDeadPlayers(Player currentPlayer)
    {
        List<Player> deadPlayers = match.getPlayers().stream().filter(Player::isDead).collect(Collectors.toList());

        if(deadPlayers.size() > 1)currentPlayer.getGameBoard().addPoints(1);

        deadPlayers.forEach(player ->
        {
            sendBroadCastMessage(virtualView -> virtualView.showNotification(player.getUsername()+" è stato ucciso da "+currentPlayer.getUsername()));
            new CountPointsAction(this, player).execute();
            if(!gameMode.isFinalFrenzy())
            {
                player.getGameBoard().addSkull();
                remainingSkulls.decrementAndGet();
            }
            deaths.add(new Death(currentPlayer.getColor(), player.getColor(), player.isOverkilled()));
        });

        sendBroadcastUpdate();

        deadPlayers.forEach(player ->
        {
            if(!player.isLastRoundPlayed())
            {
                new RespawnAction(this, player).execute();
                if(isFinalFrenzy())player.setFinalFrenzyMode(true);
            }
        });
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

    /**
     *
     * @param username the username of the player who wants to search
     * @return return an Optional containing the player with that username if exist, otherwise an empty Optional
     */
    public Optional<Player> findPlayerByUsername(String username)
    {
        for(Player player : match.getPlayers())
        {
            if(player.getUsername().equals(username))return Optional.of(player);
        }
        return Optional.empty();
    }

    public void playerReconnected(ClientConnection client)
    {
        sendUpdate(client.getUser().getPlayer());
        client.getVirtualView().gameStarted();
        notifyOtherClients(client.getUser().getPlayer(), virtualView -> virtualView.showNotification(client.getUser().getUsername()+" si è riconnesso"));
    }

    /**
     * Create and start a timer for the start of the match
     */
    void startGameTimer()
    {
        Timer.destroyTimer("starting_game_timer");
        Timer timer = Timer.createTimer("starting_game_timer", startTimerSeconds);
        timer.addTimerListener(this);
        timer.start();
    }

    private void shuffleDecks()
    {
        ammoCardDeck.shuffle();
        weaponCardDeck.shuffle();
        powerUpCardDeck.shuffle();
    }


    private void selectStartingPlayer()
    {
        match.getPlayers().shuffle();

        Player firstPlayer = match.getPlayers().get(0);

        firstPlayer.getView().showNotification("Sei il primo giocatore!");

        firstPlayer.setAsStartingPlayer(true);
        firstPlayer.getView().youAreFirstPlayer();
        String username = firstPlayer.getClientConnection().getUser().getUsername();
        notifyOtherClients(firstPlayer, view -> view.firstPlayerIs(username));
        roundManager = new RoundManager(match.getPlayers());
    }

    private Block selectBlock(Player player) throws CanceledActionException
    {
        int maxMoves = ActionsGroup.getMaxMoves(player);
        Coordinates coords = player.getView().chooseBlock(maxMoves);
        return map.getBlock(coords.getX(), coords.getY());
    }

    private void startGame()
    {
        if(match.getPlayers().isEmpty())throw new StartGameWithoutPlayerException();
        shuffleDecks();
        refillMap();
        sendBroadcastUpdate();
        selectStartingPlayer();
        nextRound();
        endMatch();
    }

    public Map getMap()
    {
        return map;
    }

    public GameMode getGameMode()
    {
        return gameMode;
    }

    private GameData getData(Player player)
    {
        return new GameData(map.getData(), player.getData(), remainingSkulls.get(), new ArrayList<>(deaths), powerUpCardDeck.size(), weaponCardDeck.size(), gameMode, match.getData());
    }

    public Deck<PowerUpCard> getPowerUpCardDeck()
    {
        return powerUpCardDeck;
    }

    public Deck<WeaponCard> getWeaponCardDeck()
    {
        return weaponCardDeck;
    }

    public Deck<AmmoCard> getAmmoCardDeck()
    {
        return ammoCardDeck;
    }

    public List<Player> getPlayers()
    {
        return match.getPlayers().toArrayList();
    }

    public void sendUpdate(Player player)
    {
        player.getView().update(getData(player));
    }

    public void sendBroadcastUpdate()
    {
        match.getPlayers().forEach(this::sendUpdate);
    }

    public void notifyOtherClients(Player player, Consumer<VirtualView> consumer)
    {
        match.getPlayers().forEach(client ->
        {
            if(!client.equals(player))consumer.accept(client.getView());
        });
    }

    private void sendBroadCastMessage(Consumer<VirtualView> consumer)
    {
        match.getPlayers().forEach(player -> consumer.accept(player.getView()));
    }

    @Override
    public void onTimerStart(String timerName)
    {
        sendBroadCastMessage(VirtualView::gameIsStarting);
    }

    @Override
    public void onTimerTick(String timerName, int remainingTime)
    {
        sendBroadCastMessage((virtualView -> virtualView.showTimerCountdown(remainingTime)));
    }

    @Override
    public void onTimerEnd(String timerName)
    {
        try
        {
            sendBroadCastMessage((virtualView -> virtualView.showTimerCountdown(0)));
            Thread.sleep(1000);
            sendBroadCastMessage(VirtualView::gameStarted);
        }
        catch (InterruptedException e)
        {
            Logger.exception(e);
            Thread.currentThread().interrupt();
        }

        startGame();
    }
}
