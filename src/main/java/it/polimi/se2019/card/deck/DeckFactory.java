package it.polimi.se2019.card.deck;

import it.polimi.se2019.card.ammo.AmmoCard;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.utils.xml.NotValidXMLException;
import it.polimi.se2019.utils.xml.XMLDeckReader;

/**
 * Creates decks for different types of card
 */
public class DeckFactory
{
	private XMLDeckReader deckReader;

	private DeckFactory(String filePath) throws NotValidXMLException
	{
		deckReader = new XMLDeckReader(filePath);
	}

	public static DeckFactory newInstance(String filePath) throws NotValidXMLException
	{
		 return new DeckFactory(filePath);
	}

	/**
	 * created a deck of ammocards
	 * @return
	 */
	public Deck<AmmoCard> createAmmoDeck()
	{
		return new Deck<>(deckReader.parseAmmoCards());
	}

	/**
	 *
	 * created a deck of weaponcards
	 * @return
	 */

	public Deck<WeaponCard> createWeaponDeck()
	{
		return new Deck<>(deckReader.parseWeaponCards());
	}
/**
 * created a deck of powerupCards
 */

	public Deck<PowerUpCard> createPowerUpDeck()
	{
		return new Deck<>(deckReader.parsePowerUpCards());
	}

}
