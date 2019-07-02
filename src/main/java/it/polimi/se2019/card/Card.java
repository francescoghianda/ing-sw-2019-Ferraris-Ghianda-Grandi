package it.polimi.se2019.card;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Card
{
	private String name;
	private String description;
	private String id;

	private AtomicBoolean enabled;

	protected Card()
	{
		name = "";
		description = "";
		id = "";
		enabled = new AtomicBoolean(true);
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
		this.enabled.set(enabled);
	}

	public final boolean isEnabled()
	{
		return enabled.get();
	}

	public final String getIdIgnoreClone()
	{
		if(id.startsWith("PUC"))
		{
			return this.id.split("_")[0];
		}
		return this.id;
	}

	public final CardData getCardData()
	{
		return new CardData(id, getIdIgnoreClone(), name, description, enabled.get());
	}

	@Override
	public final boolean equals(Object obj)
	{
		if(!(obj instanceof Card))return false;
		return ((Card)obj).getId().equals(id);
	}

	@Override
	public String toString()
	{
		return "CARD [id = "+getId()+", name = "+getName()+", enabled = "+enabled+"]";
	}

}
