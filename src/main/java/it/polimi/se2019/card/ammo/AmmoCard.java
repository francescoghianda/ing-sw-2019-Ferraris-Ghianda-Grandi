package it.polimi.se2019.card.ammo;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.deck.Deck;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.player.Player;

import java.io.Serializable;

public class AmmoCard extends Card implements Serializable
{
	private final int redAmmo;
	private final int yellowAmmo;
	private final int blueAmmo;
	private final boolean pickPowerUp;

	public AmmoCard(int redAmmo, int blueAmmo, int yellowAmmo, boolean pickPowerUp, String id)
	{
		super();
		setId(id);
		this.redAmmo = redAmmo;
		this.blueAmmo = blueAmmo;
		this.yellowAmmo = yellowAmmo;
		this.pickPowerUp = pickPowerUp;
	}

	@Override
	public void setId(String id)
	{
		super.setId("AMC"+id);
	}

	public void apply(Player player, Deck<PowerUpCard> powerUpCardDeck)
	{
		player.getGameBoard().addRedAmmo(this.redAmmo);
		player.getGameBoard().addYellowAmmo(this.yellowAmmo);
		player.getGameBoard().addBlueAmmo(this.blueAmmo);

		if(pickPowerUp && player.powerUpsSize() < 3) player.addPowerUpCard(powerUpCardDeck.getFirstCard());
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
		return "AmmoCard #"+getId()+": [Red: "+redAmmo+" Blue: "+blueAmmo+" Yellow: "+yellowAmmo+" PowerUp: "+pickPowerUp+"]";
	}

}
