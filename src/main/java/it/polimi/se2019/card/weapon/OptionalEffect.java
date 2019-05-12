package it.polimi.se2019.card.weapon;

import it.polimi.se2019.card.cost.Cost;

public class OptionalEffect
{
    private final String name;
    private boolean enabled;

    private Cost cost;

    private String script;

    public OptionalEffect(String name)
    {
        this.name = name;
    }

    public void setScript(String script)
    {
        this.script = script;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setCost(int redCost, int blueCost, int yellowCost)
    {
        cost = new Cost(redCost, blueCost, yellowCost);
    }

    public void setCost(Cost cost)
    {
        this.cost = cost;
    }

    public String getName()
    {
        return this.name;
    }

    public String getScript()
    {
        return this.script;
    }

    boolean isEnabled()
    {
        return this.enabled;
    }
}
