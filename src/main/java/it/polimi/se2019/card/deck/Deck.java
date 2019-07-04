package it.polimi.se2019.card.deck;

import it.polimi.se2019.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * containes all the cards(weaponcards, ammocards and powerupcards) (is a subclass of Card)
 * @param <T>
 */
public class Deck<T extends Card>
{
	private List<T> cards;
	private static Random random = new Random();

	public Deck()
	{
		cards = Collections.synchronizedList(new ArrayList<>());
	}

	public Deck(List<T> cards)
	{
		this.cards = new ArrayList<>(cards);
	}

	/**
	 * add a card to deck
	 * @param card
	 */
	public void addCard(T card)
	{
		cards.add(card);
	}

	public T getFirstCard()
	{
		return getCard(0);
	}

	public T getRandomCard()
	{
		return getCard(random.nextInt(cards.size()));
	}

	public T getCard(int index)
	{
		if(cards.isEmpty() || index >= cards.size() || index < 0)return null;
		T card = cards.get(index);
		cards.remove(index);
		return card;
	}

	/**
	 * It shuffles specific cards
	 */
	public void shuffle()
	{
		Collections.shuffle(cards);
	}

	/**
	 *
	 * @return the amount of cards contained in the deck
	 */
	public int size()
	{
		return cards.size();
	}

	/**
	 *checks if deck is empty or not
	 * @return a boolean value based on the check's result
	 */
	public boolean isEmpty()
	{
		return cards.isEmpty();
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		cards.forEach(card -> builder.append(card).append("\n"));
		return builder.toString();
	}

}
