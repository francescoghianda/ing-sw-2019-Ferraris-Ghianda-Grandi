package it.polimi.se2019.player;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.CardData;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.utils.constants.GameColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * creates a player
 */
public class Player
{
	private final GameColor color;
	private boolean startingPlayer;
	private List<WeaponCard> weapons;
	private List<PowerUpCard> powerUps;
	private Block block;
	private GameBoard gameBoard;
	private List<Action> executedAction;
	private GameController gameController;
	private List<Player> damagedPlayers;
	private ClientConnection clientConnection;
	private AtomicBoolean finalFrenzyMode;

	private AtomicBoolean firstRoundPlayed;
	private AtomicBoolean lastRoundPlayed;

	/**
	 * creates and initializes the features of the player
	 * @param color color of the player's pawn
	 * @param gameController current gamecontroller of the game
	 * @param clientConnection connection that the player has established to the client
	 */
	public Player(GameColor color, GameController gameController, ClientConnection clientConnection)
	{
		if(color == null || gameController == null || clientConnection == null)throw new NullPointerException();

		weapons = Collections.synchronizedList(new ArrayList<>());
		powerUps = Collections.synchronizedList(new ArrayList<>());
		executedAction = Collections.synchronizedList(new ArrayList<>());
		damagedPlayers = Collections.synchronizedList(new ArrayList<>());
		gameBoard = new GameBoard();
		finalFrenzyMode = new AtomicBoolean();
		firstRoundPlayed = new AtomicBoolean(false);
		lastRoundPlayed = new AtomicBoolean(false);
		this.color = color;
		this.clientConnection = clientConnection;
		this.gameController = gameController;
	}

	public void setFinalFrenzyMode(boolean finalFrenzyMode)
	{
		this.finalFrenzyMode.set(finalFrenzyMode);
	}

	public boolean isFinalFrenzyMode()
	{
		return finalFrenzyMode.get();
	}

	public boolean isDead()
	{
		return gameBoard.getTotalReceivedDamage() >= 11;
	}

	public boolean isOverkilled()
	{
		return gameBoard.getTotalReceivedDamage() > 11;
	}

	public Player getCauseOfDeath()
	{
		if(!isDead())return null;
		return new ArrayList<>(gameBoard.getReceivedDamage().keySet()).get(11);
	}

	public Player getFirstBloodPlayer()
	{
		List<Player> keys = new ArrayList<>(gameBoard.getReceivedDamage().keySet());
		return keys.isEmpty() ? null : keys.get(0);
	}

	public void resetDamagedPlayers()
	{
		damagedPlayers.clear();
	}

	public boolean isFirstRoundPlayed()
	{
		return firstRoundPlayed.get();
	}

	public void setFirstRoundPlayed(boolean firstRoundPlayed)
	{
		this.firstRoundPlayed.set(firstRoundPlayed);
	}

	public boolean isLastRoundPlayed()
	{
		return lastRoundPlayed.get();
	}

	public void setLastRoundPlayed(boolean lastRoundPlayed)
	{
		this.lastRoundPlayed.set(lastRoundPlayed);
	}

	public void setClientConnection(ClientConnection clientConnection)
	{
		this.clientConnection = clientConnection;
	}

	public VirtualView getView()
	{
		return clientConnection.getVirtualView();
	}

	public void reset()
	{
		executedAction.clear();
		resetDamagedPlayers();
	}

	public boolean hasUnloadedWeapons()
	{
		for(WeaponCard card : weapons)
		{
			if(!card.isLoad())return true;
		}
		return false;
	}

	public ArrayList<WeaponCard> getUnloadedWeapons()
	{
		return weapons.stream().filter(card -> !card.isLoad()).collect(Collectors.toCollection(ArrayList::new));
	}

	public String getUsername()
	{
		return clientConnection.getUser().getUsername();
	}

	public GameController getGameController()
	{
		return this.gameController;
	}

	public GameBoard getGameBoard()
	{
		return this.gameBoard;
	}

	public void addExecutedAction(Action action)
	{
		this.executedAction.add(action);
	}

	public void resetExecutedAction()
	{
		this.executedAction.clear();
	}

	public void addDamagedPlayer(Player player)
	{
		if(!damagedPlayers.contains(player))this.damagedPlayers.add(player);
	}

	public List<Player> getDamagedPlayers()
	{
		return new ArrayList<>(damagedPlayers);
	}

	public List<Block> getVisibleBlocks()
	{
		ArrayList<Block> visibleBlocks = new ArrayList<>(block.getRoom().getBlocks());
		if(block.hasDoor())
		{
			Block[] doors = block.getDoors();
			for(Block door : doors)
			{
				if(door != null)visibleBlocks.addAll(door.getRoom().getBlocks());
			}
		}

		return visibleBlocks;
	}

	public List<Player> getVisiblePlayers()
	{
		List<Block> visibleBlocks = getVisibleBlocks();
		List<Player> visiblePlayers = new ArrayList<>();
		for(Block visibleBlock : visibleBlocks)visiblePlayers.addAll(visibleBlock.getPlayers());
		return visiblePlayers;
	}

	public Action[] getExecutedActions()
	{
		Action[] executed = new Action[executedAction.size()];
		return executedAction.toArray(executed);
	}

	public ClientConnection getClientConnection()
	{
		return this.clientConnection;
	}


	/**
	 * adds a weapon card to the gameboard of the player
	 * @param weaponCard weaponcard added to this pleayer
	 */
	public void addWeaponCard (WeaponCard weaponCard)
	{
		if(weapons.size() < 3)
		{
			weapons.add(weaponCard);
		}
	}

	/**
	 *  adds a powerup card to the gameboard of the player
	 * @param powerUpCard powerup card added to this pleayer
	 */
	public void addPowerUpCard (PowerUpCard powerUpCard)
	{
		if(powerUps.size() < 3)
		{
			powerUps.add(powerUpCard);
		}
	}

	public PowerUpCard getRandomPowerUp()
	{
		if(powerUps.isEmpty())return null;
		else return powerUps.get(new Random().nextInt(powerUps.size()));
	}

	public int weaponsSize()
	{
		return weapons.size();
	}

	public void removeWeapon(WeaponCard weaponCard)
	{
		weapons.remove(weaponCard);
	}

	public int powerUpsSize()
	{
		return powerUps.size();
	}

	public void removePowerUp(PowerUpCard powerUpCard)
	{
		powerUps.remove(powerUpCard);
	}

	public GameColor getColor()
	{
		return color;
	}

	public boolean isStartingPlayer()
	{
		return startingPlayer;
	}

	public void setAsStartingPlayer(boolean startingPlayer)
	{
		this.startingPlayer = startingPlayer;
	}

	public Block getBlock()
	{
		return this.block;
	}

	public void setBlock(Block block)
	{
		if(this.block != null)this.block.removePlayer(this);
		this.block = block;
		block.addPlayer(this);
	}

	public PlayerData getData()
	{
		ArrayList<CardData> powerUpsCards = powerUps.stream().map(Card::getCardData).collect(Collectors.toCollection(ArrayList::new));
		ArrayList<CardData> weaponsCards = weapons.stream().map(Card::getCardData).collect(Collectors.toCollection(ArrayList::new));
		int x = block == null ? -1 : block.getX();
		int y = block == null ? -1 : block.getY();
		return new PlayerData(clientConnection.getUser().getUsername(), color, weaponsCards, powerUpsCards, gameBoard.getData(), x, y, finalFrenzyMode.get());
	}

	@Override
	public String toString()
	{
		return clientConnection.getUser().getUsername()+" ("+color+")";
	}

}
