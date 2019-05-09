package it.polimi.se2019.card;


import it.polimi.se2019.card.ammo.AmmoParser;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.card.weapon.WeaponParser;

public class DeckFactory
{
	public DeckFactory()
	{

	}

	public static Deck<AmmoCard> createAmmoDeck()
	{
		return new Deck<>(AmmoParser.parseCards());
	}

	public static Deck<WeaponCard> createWeaponDeck()
	{
		WeaponParser parser = new WeaponParser();
		return new Deck<>(parser.parseCards());
	}

}
