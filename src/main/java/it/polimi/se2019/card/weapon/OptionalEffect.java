package it.polimi.se2019.card.weapon;

public class OptionalEffect
{
    private boolean enabled;
    private int redCost;
    private int blueCost;
    private int yellowCost;

    private String script;

    public OptionalEffect()
    {

    }

    void setScript(String script)
    {
        this.script = script;
    }

    void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    void setCost(int redCost, int blueCost, int yellowCost)
    {
        this.redCost = redCost;
        this.blueCost = blueCost;
        this.yellowCost = yellowCost;
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
