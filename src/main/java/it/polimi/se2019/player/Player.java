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

	public Player(GameColor color)
	{
		weapons = new ArrayList<>();
		powerUps = new ArrayList<>();
		this.color = color;
	}

	public void addWeaponCard (WeaponCard weaponCard)
	{
		if(weapons.size() < 3)
		{
			weapons.add(weaponCard);
		}
	}

	public void addPowerUpCard (PowerUpCard powerUpCard)
	{
		if(powerUps.size() < 3)
		{
			powerUps.add(powerUpCard);
		}
	}

	public GameColor getColor()
	{
		return color;
	}

	public boolean isStartingPlayer()
	{
		return startingPlayer;
	}

	public Block getBlock()
	{
		return this.block;
	}


	public void setBlock(Block block)
	{
		this.block = block;
	}

	private boolean pathPossible(int currX, int currY, int destX, int destY)
	{
		return false;
	}

	private void execAction(ActionsGroup actionsGroup)
	{

	}

}
