package it.polimi.se2019.card;

public abstract class Card
{

	private int id;
	private String name;
	private String description;

	public Card()
	{

	}

	public int getId()
	{
		return 0;
	}

	public String getName()
	{
		return "";
	}

	public String getDescriprion()
	{
		return "";
	}

	public abstract String toString();

}
