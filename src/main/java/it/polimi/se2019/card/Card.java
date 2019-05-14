package it.polimi.se2019.card;

import java.io.Serializable;

public abstract class Card implements Serializable
{
	private String name;
	private String description;
	private String id;

	protected Card()
	{

	}

	public void setId(String id)
	{
		this.id = id;
	}

	public final void setName(String name)
	{
		this.name = name;
	}

	public final void setDescription(String description)
	{
		this.description = description;
	}

	public final String getName()
	{
		return name;
	}

	public final String getDescription()
	{
		return description;
	}

	public final String getId()
	{
		return this.id;
	}

	public abstract String toString();

}
