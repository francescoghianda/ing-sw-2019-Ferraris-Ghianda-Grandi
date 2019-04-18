package it.polimi.se2019.player;

import it.polimi.se2019.card.PowerUpCard;
import it.polimi.se2019.card.WeaponCard;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.utils.constants.GameColor;

import java.util.ArrayList;


public class Player
{

	private GameColor color;
	private boolean startingPlayer;
	private ArrayList<WeaponCard> weapons;
	private ArrayList<PowerUpCard> powerUps;
	private Block block;
	private GameBoard gameboard;

	public Player()
	{

	}

	public void addWeaponCard (WeaponCard weaponCard)
	{

	}

	public void addPowerUpCard (PowerUpCard powerUpCard)
	{

	}

	public GameColor getColor()
	{
		return null;
	}

	public boolean isStartingPlayer()
	{
		return false;
	}

	private void hit(Player player, WeaponCard weapon)
	{

	}

	private void grab()
	{

	}

	private void move(int direction)
	{

	}

	private void execAction(ActionsGroup actionsGroup)
	{

	}

}
