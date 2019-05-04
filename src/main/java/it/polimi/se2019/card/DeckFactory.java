package it.polimi.se2019.card;


import it.polimi.se2019.card.ammo.AmmoParser;

public class DeckFactory
{
	public DeckFactory()
	{

	}

	public static Deck<AmmoCard> createAmmoDeck()
	{
		return new Deck<>(AmmoParser.parseCards());
	}

}
