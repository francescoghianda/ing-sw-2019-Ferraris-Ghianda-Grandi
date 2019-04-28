package it.polimi.se2019.player;

import java.util.HashMap;

public class GameBoard
{
    private Player player;
    private HashMap<Player, Integer> receivedDamage;
    private HashMap<Player, Integer> markers;
    private int redAmmo;
    private int blueAmmo;
    private int yellowAmmo;
    private int skulls;
    private int aviableActions;

    public GameBoard()
    {
        receivedDamage = new HashMap<>();
        markers = new HashMap<>();
    }

    public int getTotalReceivedDamage()
    {
        int total = 0;
        for(Integer damage : receivedDamage.values())total += damage;
        return total;
    }

    public int getReceivedDamage(Player player)
    {
        return receivedDamage.getOrDefault(player, 0);
    }

    public int getMarker(Player player)
    {
        return markers.getOrDefault(player, 0);
    }

    public int getRedAmmo()
    {
        return redAmmo;
    }

    public int getBlueAmmo()
    {
        return blueAmmo;
    }

    public int getYellowAmmo()
    {
        return yellowAmmo;
    }

    public int getSkulls()
    {
        return skulls;
    }

    public void addDamage(Player player, int damage)
    {
        if(receivedDamage.containsKey(player)) receivedDamage.replace(player, receivedDamage.get(player)+damage);
        else receivedDamage.put(player, damage);
    }

    public void addMarker(Player player, int mark)
    {
        if(markers.containsKey(player)) markers.replace(player, markers.get(player)+mark);
        else markers.put(player, mark);
    }

    public void setRedAmmo(int redAmmo)
    {
        this.redAmmo = redAmmo;
    }

    public void setYellowAmmo(int yellowAmmo)
    {
        this.yellowAmmo = yellowAmmo;
    }

    public void setBlueAmmo(int blueAmmo)
    {
        this.blueAmmo = blueAmmo;
    }


}
