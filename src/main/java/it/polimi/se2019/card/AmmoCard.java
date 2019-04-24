package it.polimi.se2019.card;

import it.polimi.se2019.player.Player;

public class AmmoCard extends Card
{

	private final int redAmmo;
	private final int yellowAmmo;
	private final int blueAmmo;
	private final boolean pickPowerUp;

	public AmmoCard(int id, String name, String description, int redAmmo, int yellowAmmo, int blueAmmo, boolean pickPowerUp)
	{
		super();
		this.redAmmo = redAmmo;
		this.yellowAmmo = yellowAmmo;
		this.blueAmmo = blueAmmo;
		this.pickPowerUp = pickPowerUp;
	}

	public void apply(Player player)
	{
		player.getGameBoard().addRedAmmo(this.redAmmo);
		player.getGameBoard().addYellowAmmo(this.yellowAmmo);
		player.getGameBoard().addBlueAmmo(this.blueAmmo);
	}

	public int getRedAmmo()
	{
		return this.redAmmo;
	}

	public int getYellowAmmo()
	{
		return this.yellowAmmo;
	}

	public int getBlueAmmo()
	{
		return this.blueAmmo;
	}

	public boolean isPickPowerUp()
	{
		return pickPowerUp;
	}

	public String toString()
	{
		return "";
	}

}
