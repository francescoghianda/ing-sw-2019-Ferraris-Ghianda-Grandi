package it.polimi.se2019.card.ammo;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.deck.Deck;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.player.Player;

import java.io.IOException;
import java.io.Serializable;

/**
 * class of the model that store ammocards's data
 */
public class AmmoCard extends Card implements Serializable
{
	private final int redAmmo;
	private final int yellowAmmo;
	private final int blueAmmo;
	private final boolean pickPowerUp;

	/**
	 * Constructs a new ammocard
	 * @param redAmmo the value of a red ammocard
	 * @param blueAmmo the value of a blue ammocard
	 * @param yellowAmmo the value of a yellow ammocard
	 * @param pickPowerUp if true, the ammocard contains a powerup
	 * @param id the string the identifies the ammocard
	 */
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

	/**
	 * It adds the ammocard to the player and , if present, the associated powerup. It also controls the accepted amount of ammocards for each player
	 *
	 * @param player indicates the player to whom the ammocard is added
	 * @param powerUpCardDeck refers to the powerup deck from which the first card is extracted
	 */

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
