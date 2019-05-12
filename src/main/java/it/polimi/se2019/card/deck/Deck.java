package it.polimi.se2019.card.deck;

import it.polimi.se2019.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck<T extends Card>
{
	private List<T> cards;
	private static Random random = new Random();

	public Deck()
	{
		cards = new ArrayList<>();
	}

	public Deck(List<T> cards)
	{
		this.cards = new ArrayList<>(cards);
	}


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

	public void shuffle()
	{
		Collections.shuffle(cards);
	}

	public int size()
	{
		return cards.size();
	}

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
