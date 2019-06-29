package it.polimi.se2019.controller;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.ammo.AmmoCard;
import it.polimi.se2019.card.cost.Cost;
import it.polimi.se2019.card.deck.Deck;
import it.polimi.se2019.card.deck.DeckFactory;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.card.weapon.OptionalEffect;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.controller.settings.MatchSettings;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.map.Map;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.network.message.ConnectionErrorException;
import it.polimi.se2019.player.*;
import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.constants.GameMode;
import it.polimi.se2019.utils.list.ListChangeAdapter;
import it.polimi.se2019.utils.list.ListChangeEvent;
import it.polimi.se2019.utils.list.ObservableList;
import it.polimi.se2019.utils.logging.Logger;
import it.polimi.se2019.utils.timer.Timer;
import it.polimi.se2019.utils.timer.TimerListener;
import it.polimi.se2019.utils.xml.NotValidXMLException;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class GameController implements TimerListener
{
    private Random random;
    private List<GameColor> availablePlayerColors;
    private ObservableList<Player> players;
    private Map map;
    private GameMode gameMode;
    private ClientsManager clientsManager;
    private Timer timer;
    private int startTimerSeconds;

    private Deck<AmmoCard> ammoCardDeck;
    private Deck<WeaponCard> weaponCardDeck;
    private Deck<PowerUpCard> powerUpCardDeck;

    private int deaths;
    private int remainingSkulls;

    private RoundManager roundManager;

    private int playersForStart;

    private boolean gameStarted;

    private final Match match;

    public GameController(Match match)
    {
        this.match = match;
        this.clientsManager = ClientsManager.getInstance();
        availablePlayerColors = new ArrayList<>(Arrays.asList(GameColor.values()));
        availablePlayerColors.remove(GameColor.RED);
        gameMode = GameMode.NORMAL;

        MatchSettings settings = MatchSettings.getInstance();

        remainingSkulls = settings.getSkullNumber();
        playersForStart = settings.getMaxPlayers();
        startTimerSeconds = settings.getStartTimerSeconds();
        deaths = 0;
        map = Map.createMap(settings.getMapNumber());

        random = new Random();
        players = new ObservableList<>();

        players.addChangeListener(new ListChangeAdapter<Player>() {
            @Override
            public void onChanged(Player element, ListChangeEvent eventType)
            {
                if(eventType == ListChangeEvent.ELEMENT_ADDED && players.size() >= playersForStart)
                {
                    match.startGame();
                }
            }
        });

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

    public void playerReconnected(ClientConnection client)
    {
        sendUpdate(client.getUser().getPlayer());
        client.getVirtualView().gameStarted();

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public List<Player> getPlayers()
    {
        return players.toArrayList();
    }

    public Optional<Player> findPlayerByUsername(String username)
    {
        for(Player player : players)
        {
            if(player.getUsername().equals(username))return Optional.of(player);
        }
        return Optional.empty();
    }

    private void nextRound()
    {
        Player currentPlayer = roundManager.next();

        try
        {
            currentPlayer.getView().roundStart();

            if(roundManager.isFirstRound() || !currentPlayer.isFirstRoundPlayed()) firstRound(currentPlayer);

            boolean continueRound = executeAction(currentPlayer);
            if(continueRound)
            {
                currentPlayer.resetExecutedAction();
                executeAction(currentPlayer);
            }

            reloadAllWeapons(currentPlayer);
        }
        catch (ConnectionErrorException e)
        {
            Logger.error("Connection error during "+currentPlayer.getUsername()+"'s round!");
        }
        catch (TimeOutException e)
        {
            Logger.info("Player "+currentPlayer.getUsername()+" has finished his time");
        }
        finally
        {
            currentPlayer.getView().roundEnd();

            currentPlayer.resetExecutedAction();

            refillMap();
            sendBroadcastUpdate();

            respawnKilledPlayers();

            nextRound();
        }
    }

    private List<WeaponCard> getUnloadedWeapons(Player player)
    {
        List<WeaponCard> unloadedWeapons = player.getUnloadedWeapons();
        return unloadedWeapons.stream().filter(weapon -> player.getGameBoard().canPay(weapon.getReloadCost())).collect(Collectors.toList());
    }

    private void reloadAllWeapons(Player player)
    {
        if(!player.hasUnloadedWeapons())return;
        List<WeaponCard> unloadedWeapons = getUnloadedWeapons(player);
        if(unloadedWeapons.isEmpty())return;

        ArrayList<Card> toReload = player.getView().chooseWeaponsToReload(new ArrayList<>(unloadedWeapons));
        if(!unloadedWeapons.containsAll(toReload))return;
        List<WeaponCard> toReloadWeapons = toReload.stream().map(card -> WeaponCard.findCardById(card.getId())).collect(Collectors.toList());

        /*int totalRedCost = toReloadWeapons.stream().map(WeaponCard::getReloadCost).map(Cost::getRedAmmo).reduce(0, Integer::sum);
        int totalBlueCost = toReloadWeapons.stream().map(WeaponCard::getReloadCost).map(Cost::getBlueAmmo).reduce(0, Integer::sum);
        int totalYellowCost = toReloadWeapons.stream().map(WeaponCard::getReloadCost).map(Cost::getYellowAmmo).reduce(0, Integer::sum);*/

        toReloadWeapons.forEach(weaponCard ->
        {
            try
            {
                player.getGameBoard().pay(weaponCard.getReloadCost());
                weaponCard.reload();
            }
            catch (NotEnoughAmmoException e)
            {
                Logger.warning("Weapon "+weaponCard+" cannot be reloaded by player "+player);
            }
        });
    }

    private void reloadWeapon(Player player)
    {
        if(!player.hasUnloadedWeapons())return;
        List<WeaponCard> unloadedWeapons = getUnloadedWeapons(player);
        if(unloadedWeapons.isEmpty())return;

        WeaponCard chosen = WeaponCard.findCardById(player.getView().chooseWeaponToReload(new ArrayList<>(unloadedWeapons)).getId());
        try
        {
            player.getGameBoard().pay(chosen.getReloadCost());
            chosen.reload();
            player.addExecutedAction(Action.RELOAD);
        }
        catch (NotEnoughAmmoException e)
        {
            Logger.warning("Weapon "+chosen+" cannot be reloaded by player "+player);
        }
    }

    private void respawnKilledPlayers()
    {
        players.forEach(player ->
        {
            if(player.getGameBoard().getTotalReceivedDamage() >= 11)
            {
                countPoints(player);
                respawn(player);
            }
        });
    }

    private void countPoints(Player killedPlayer)
    {
        LinkedHashMap<Player, Integer> damage = killedPlayer.getGameBoard().getReceivedDamage();


    }

    private void respawn(Player player)
    {
        PowerUpCard powerUp = player.powerUpsSize() <= 0 ? powerUpCardDeck.getFirstCard() : null;

        try
        {
            String chosenId = player.getView().chooseSpawnPoint(powerUp, null);
            PowerUpCard chosenPowerUp = PowerUpCard.findById(chosenId);
            Block spawnPoint = map.findRoomByColor(chosenPowerUp.getColor()).getSpawnPoint();

            if(powerUp == null)player.removePowerUp(chosenPowerUp);
            powerUpCardDeck.addCard(chosenPowerUp);

            player.getGameBoard().resetDamageAndMarks();
            player.setBlock(spawnPoint);
            sendBroadcastUpdate();
        }
        catch (ConnectionErrorException | TimeOutException e)
        {
            if(powerUp != null)
            {
                Block spawnPoint = map.findRoomByColor(powerUp.getColor()).getSpawnPoint();
                powerUpCardDeck.addCard(powerUp);
                player.setBlock(spawnPoint);
            }
            else
            {
                PowerUpCard powerUpCard = player.getRandomPowerUp();
                player.removePowerUp(powerUpCard);
                powerUpCardDeck.addCard(powerUpCard);
                Block spawnPoint = map.findRoomByColor(powerUpCard.getColor()).getSpawnPoint();
                player.setBlock(spawnPoint);
            }
            player.getGameBoard().resetDamageAndMarks();
            sendBroadcastUpdate();
        }

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
                continue;
            }
            catch (ImpossibleActionException e)
            {
                Logger.warning("Impossible action: "+chosen+"; "+e.cause()+" - Player: "+currentPlayer);
            }

            sendBroadcastUpdate();
        }
        return true;
    }

    private void executeAction(Action action, Player player, Object optionalObject) throws CanceledActionException, ImpossibleActionException
    {
        switch (action)
        {
            case MOVE:
                Block destinationBlock = selectBlock(player);
                movePlayer(player, destinationBlock);
                break;
            case GRAB:
                grab(player);
                break;
            case FIRE:
                fire(player);
                break;
            case USE_POWER_UP:
                usePowerUp(player, PowerUpCard.findById(((Card)optionalObject).getId()));
                break;
        }
    }

    private void fire(Player player) throws ImpossibleActionException, CanceledActionException
    {
        WeaponCard chosenWeapon = WeaponCard.findCardById(player.getView().chooseWeaponFromPlayer().getId());

        if(chosenWeapon == null || !chosenWeapon.isLoad())throw new ImpossibleActionException(ImpossibleActionException.Cause.WEAPON_NOT_LOADED);

        try
        {
            if(chosenWeapon.hasEnabledOptionalEffect())useOptionalEffect(player, chosenWeapon);
            if(chosenWeapon.hasAlternateFireMode())chooseFireMode(player, chosenWeapon);

            chosenWeapon.fire(player, this);

            if(chosenWeapon.hasEnabledOptionalEffect())useOptionalEffect(player, chosenWeapon);
        }
        finally
        {
            if(chosenWeapon.isUsed())
            {
                chosenWeapon.setLoad(false);
                player.addExecutedAction(Action.FIRE);
            }
            chosenWeapon.reset();
        }
    }

    private void chooseFireMode(Player player, WeaponCard weapon)
    {
        String chosenFireMode = player.getView().choose("Scegli la modalità di fuoco", "Modalità Base", "Modalità Alternativa");
        if(chosenFireMode.equals("Modalità Base")) weapon.setFireMode(WeaponCard.Mode.BASIC);
        else weapon.setFireMode(WeaponCard.Mode.ALTERNATE_FIRE);
    }

    private void useOptionalEffect(Player player, WeaponCard weapon)
    {
        if(weapon.getEnabledOptionalEffects().isEmpty())return;

        String message = "Vuoi usare un effetto opzionale?";
        while(!weapon.getEnabledOptionalEffects().isEmpty())
        {
            if(player.getView().choose(message, "Si", "No").equals("Si"))
            {
                ArrayList<String> optionalEffectNames = weapon.getEnabledOptionalEffects().stream().map(OptionalEffect::getName).collect(Collectors.toCollection(ArrayList::new));
                String chosenEffect = "";
                try
                {
                    chosenEffect = player.getView().chooseOrCancel(Bundle.of("Che effetto vuoi usare?", optionalEffectNames));
                    weapon.useOptionalEffect(player, weapon.getOptionalEffect(chosenEffect), this);
                }
                catch (CanceledActionException e)
                {
                    Logger.warning("Effect "+chosenEffect+" was not executed cause"+e.getCanceledCause()+" - "+player);
                    continue;
                }
                catch (ImpossibleActionException e)
                {
                    Logger.warning("Effect "+chosenEffect+" was not executed cause"+e.cause()+" - "+player);
                    continue;
                }
                finally
                {
                    message = "Vuoi usare un altro effetto opzionale?";
                }
                weapon.getOptionalEffect(chosenEffect).setEnabled(false);
            }
            else
            {
                break;
            }
        }

    }

    private Block selectBlock(Player player) throws CanceledActionException
    {
        int maxMoves = ActionsGroup.getMaxMoves(player);
        Coordinates coords = player.getView().chooseBlock(maxMoves);
        return map.getBlock(coords.getX(), coords.getY());
    }

    private void firstRound(Player player)
    {
        PowerUpCard option1 = powerUpCardDeck.getFirstCard();
        PowerUpCard option2 = powerUpCardDeck.getFirstCard();
        try
        {
            String chosenId = player.getView().chooseSpawnPoint(option1, option2);
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

    private void startGame()
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
        timer = Timer.createTimer("startCountdown", startTimerSeconds);
        timer.addTimerListener(this);
        timer.start();
    }

    private void selectStartingPlayer()
    {
        players.shuffle();

        Player firstPlayer = players.get(0);

        firstPlayer.setAsStartingPlayer(true);
        firstPlayer.getView().youAreFirstPlayer();
        String username = firstPlayer.getClientConnection().getUser().getUsername();
        notifyOtherClients(firstPlayer, view -> view.firstPlayerIs(username));
        roundManager = new RoundManager(players);
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

        availablePlayerColors.remove(color);

        player.getGameBoard().setRedAmmo(3);
        player.getGameBoard().setBlueAmmo(3);
        player.getGameBoard().setYellowAmmo(3);

        players.add(player);

        sendUpdate(player);

        return player;
    }

    public void startGameTimer()
    {
        startTimer();
    }

    private void sendUpdate(Player player)
    {
        player.getView().update(getData(player));
    }

    private GameData getData(Player player)
    {
        return new GameData(map.getData(), player.getData(), remainingSkulls, deaths, powerUpCardDeck.size(), weaponCardDeck.size());
    }

    public void sendBroadcastUpdate()
    {
        players.forEach(this::sendUpdate);
    }

    private void notifyOtherClients(Player player, Consumer<VirtualView> consumer)
    {
        players.forEach(client ->
        {
            if(!client.equals(player))consumer.accept(client.getView());
        });
    }

    private void sendBroadCastMessage(Consumer<VirtualView> consumer)
    {
        players.forEach(player -> consumer.accept(player.getView()));
    }

    private void movePlayer(Player player, Block block)
    {
        int distance = block.getDistanceFrom(player.getBlock());

        List<Block> path = player.getBlock().getRandomPathTo(block).getBlocks();

        path.forEach(pathBlock ->
        {
            try
            {
                player.setBlock(pathBlock);
                sendBroadcastUpdate();
                Thread.sleep(400);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        });

        player.setBlock(block);
        for(int i = 0; i < distance; i++)player.addExecutedAction(Action.MOVE);
    }


    private void usePowerUp(Player player, PowerUpCard powerUp) throws ImpossibleActionException, CanceledActionException
    {
        if(powerUp.getUse() != PowerUpCard.Use.ALWAYS)throw new ImpossibleActionException(ImpossibleActionException.Cause.INVALID_POWER_UP);
        powerUp.apply(player, this);
        player.removePowerUp(powerUp);
        powerUpCardDeck.addCard(powerUp);
    }

    private WeaponCard selectWeaponFromPlayer(Player player) throws CanceledActionException
    {
        Card chosen = player.getView().chooseWeaponFromPlayer();
        return WeaponCard.findCardById(chosen.getId());
    }

    private WeaponCard selectWeaponFromBlock(Player player) throws CanceledActionException
    {
        Card chosen = player.getView().chooseWeaponFromBlock();
        return WeaponCard.findCardById(chosen.getId());
    }

    private void notEnoughAmmo(Player player) throws CanceledActionException
    {
        if(player.powerUpsSize() > 0)
        {
            boolean sellPowerUp = player.getView().notEnoughAmmo(true);
            if(!sellPowerUp)return;
            Card chosen = player.getView().choosePowerUp();
            PowerUpCard powerUpCard = PowerUpCard.findById(chosen.getId());

            switch (powerUpCard.getColor())
            {
                case BLUE:
                    player.getGameBoard().addBlueAmmo(1);
                    break;
                case RED:
                    player.getGameBoard().addRedAmmo(1);
                    break;
                case YELLOW:
                    player.getGameBoard().addYellowAmmo(1);
                    break;
            }
            player.removePowerUp(powerUpCard);
            powerUpCardDeck.addCard(powerUpCard);
        }
        else
        {
            player.getView().notEnoughAmmo(false);
        }
    }

    private void grab(Player player) throws CanceledActionException
    {
        Block playerBlock = player.getBlock();
        if(playerBlock.isSpawnPoint() && playerBlock.isWeaponCardPresent())
        {
            WeaponCard weaponCard = selectWeaponFromBlock(player);

            try
            {
                player.getGameBoard().pay(weaponCard.getBuyCost());
                if(player.weaponsSize() >= 3)
                {
                    try
                    {
                        WeaponCard substituteWeapon = selectWeaponFromPlayer(player);
                        player.removeWeapon(substituteWeapon);
                        weaponCard.reload();
                        player.addWeaponCard(weaponCard);
                        playerBlock.replaceWeaponCard(weaponCard, substituteWeapon);
                    }
                    catch (CanceledActionException e)
                    {
                        player.getGameBoard().reverseLastPay();
                        throw new CanceledActionException(e.getCanceledCause());
                    }
                }
                else
                {
                    playerBlock.removeWeaponCard(weaponCard);
                    weaponCard.reload();
                    player.addWeaponCard(weaponCard);
                }
                player.addExecutedAction(Action.GRAB);
            }
            catch (NotEnoughAmmoException e)
            {
                notEnoughAmmo(player);
            }

        }
        else if(playerBlock.getAmmoCard() != null)
        {
            playerBlock.getAmmoCard().apply(player, powerUpCardDeck);
            ammoCardDeck.addCard(playerBlock.getAmmoCard());
            playerBlock.removeAmmoCard();
            player.addExecutedAction(Action.GRAB);
        }
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
