package it.polimi.se2019.player;

import it.polimi.se2019.card.WeaponCard;
import it.polimi.se2019.map.Block;

import java.util.ArrayList;
import java.util.HashMap;


public class Player
{

	private Color color;
	private HashMap<Player, Integer> receivedDamage;
	private int skulls;
	private int redAmmo;
	private int blueAmmo;
	private int yellowAmmo;
	private boolean startingPlayer;
	private ArrayList<WeaponCard> weapons;
	private Block block;
	private int aviableActions;

	public Player()
	{

	}

	public Color getColor()
	{
		return null;
	}

	public int getTotalReceivedDamage()
	{
		return 0;
	}


	public int getReceivedDamage(Player player)
	{
		return 0;
	}

	public int getRedAmmo()
	{
		return 0;
	}

	public int getBlueAmmo()
 	{
		return 0;
	}

	public int getYellowAmmo()
	{
		return 0;
	}

	public int getSkulls()
	{
		return 0;
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
	public void setRedAmmo(int redAmmo)
	{

	}

	public void setYellowAmmo(int yellowAmmo)
	{

	}
	public void setBlueAmmo(int blueAmmo)
	{
		
	}

}
