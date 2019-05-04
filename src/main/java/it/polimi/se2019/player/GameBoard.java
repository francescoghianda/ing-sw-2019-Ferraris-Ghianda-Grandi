package it.polimi.se2019.player;

import java.util.HashMap;

/**
 * Defines the gameboard of the game
 */
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

    /**
     * gameboard
     */
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

    /**
     * adds the received damage to the gameboard of this player
     * @param player player who received the damage
     * @param damage received damage
     */
    public void addDamage(Player player, int damage)
    {
        if(receivedDamage.containsKey(player)) receivedDamage.replace(player, receivedDamage.get(player)+damage);
        else receivedDamage.put(player, damage);
    }

    /**
     * adds a marker after a received damage
     * @param player player who received the damage and who has to receive the marker
     * @param mark mark that has to be added to a player
     */
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

    public void addRedAmmo(int redAmmo)
    {
        this.redAmmo += redAmmo;
    }

    public void addBlueAmmo(int blueAmmo)
    {
        this.blueAmmo += blueAmmo;
    }

    public void addYellowAmmo(int yellowAmmo)
    {
        this.yellowAmmo += yellowAmmo;
    }


}
