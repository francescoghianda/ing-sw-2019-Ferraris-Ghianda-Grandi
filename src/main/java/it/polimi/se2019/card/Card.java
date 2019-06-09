package it.polimi.se2019.card;

import java.io.Serializable;

public abstract class Card implements Serializable
{
	private String name;
	private String description;
	private String id;

	private boolean enabled;

	protected Card()
	{
		name = "";
		description = "";
		id = "";
		enabled = true;
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
		return id;
	}

	public final void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public final boolean isEnabled()
	{
		return enabled;
	}

	public final String getIdIgnoreClone()
	{
		if(id.startsWith("PUC"))
		{
			return this.id.split("_")[0];
		}
		return this.id;
	}

	@Override
	public final boolean equals(Object obj)
	{
		if(!(obj instanceof Card))return false;
		return ((Card)obj).getId().equals(id);
	}

	public abstract String toString();

}
