package it.polimi.se2019.card;

import java.io.Serializable;

/**
 * Containes all the data of a Card
 */
public final class CardData implements Serializable
{
    public static final long serialVersionUID = 5L;

    private final String id;
    private final String idIgnoreClone;
    private final String name;
    private final String description;
    private final boolean enabled;

    public CardData(String id, String idIgnoreClone, String name, String description, boolean enabled)
    {
        this.id = id;
        this.idIgnoreClone = idIgnoreClone;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
    }

    public String getId()
    {
        return id;
    }

    public String getIdIgnoreClone()
    {
        return idIgnoreClone;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public final boolean equals(Object obj)
    {
        if(!(obj instanceof CardData))return false;
        return ((CardData)obj).getId().equals(id);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
