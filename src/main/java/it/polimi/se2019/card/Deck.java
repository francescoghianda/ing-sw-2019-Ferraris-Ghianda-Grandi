package it.polimi.se2019.card;

import java.util.ArrayList;
import java.util.Random;

public class Deck<T extends Card>
{
	private ArrayList<T> cards;
	private static Random random = new Random();

	public Deck()
	{
		cards = new ArrayList<>();
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
		for(int i = 0; i < cards.size(); i++)
		{
			invert(i, random.nextInt(cards.size()));
		}
	}

	private void invert(int index1, int index2)
	{
		T card1 = cards.get(index1);
		cards.set(index1, cards.get(index2));
		cards.set(index2, card1);
	}

	public boolean isEmpty()
	{
		return cards.isEmpty();
	}

}
