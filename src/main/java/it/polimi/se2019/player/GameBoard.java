package it.polimi.se2019.player;

import it.polimi.se2019.card.cost.Cost;
import it.polimi.se2019.utils.constants.GameColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Defines the gameboard of the game
 */
public class GameBoard implements Serializable
{
    private LinkedHashMap<Player, Integer> receivedDamage;
    private LinkedHashMap<Player, Integer> markers;
    private int redAmmo;
    private int blueAmmo;
    private int yellowAmmo;
    private int skulls;
    private int aviableActions;

    private Cost lastPay;

    /**
     * gameboard
     */
    public GameBoard()
    {
        receivedDamage = new LinkedHashMap<>();
        markers = new LinkedHashMap<>();
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
        if(damage < 0)throw new NegativeValueException();
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
        if(mark < 0)throw new NegativeValueException();
        if(mark > 3)mark = 3;
        if(markers.containsKey(player))
        {
            if(markers.get(player)+mark <= 3) markers.replace(player, markers.get(player)+mark);
            else markers.replace(player, 3);
        }
        else markers.put(player, mark);
    }

    public void setRedAmmo(int redAmmo)
    {
        if(redAmmo < 0)throw new NegativeValueException();
        if(redAmmo > 3)throw new TooHighValueException();
        this.redAmmo = redAmmo;
    }

    public void setYellowAmmo(int yellowAmmo)
    {
        if(yellowAmmo < 0)throw new NegativeValueException();
        if(yellowAmmo > 3)throw new TooHighValueException();
        this.yellowAmmo = yellowAmmo;
    }

    public void setBlueAmmo(int blueAmmo)
    {
        if(blueAmmo < 0)throw new NegativeValueException();
        if(blueAmmo > 3)throw new TooHighValueException();
        this.blueAmmo = blueAmmo;
    }

    public void addRedAmmo(int redAmmo)
    {
        this.redAmmo += redAmmo;
        if(this.redAmmo > 3)this.redAmmo = 3;
    }

    public void addBlueAmmo(int blueAmmo)
    {
        this.blueAmmo += blueAmmo;
        if(this.blueAmmo > 3)this.blueAmmo = 3;
    }

    public void addYellowAmmo(int yellowAmmo)
    {
        this.yellowAmmo += yellowAmmo;
        if(this.yellowAmmo > 3)this.yellowAmmo = 3;
    }

    public boolean canPay(Cost cost)
    {
        return redAmmo >= cost.getRedAmmo() && blueAmmo >= cost.getBlueAmmo() && yellowAmmo >= cost.getYellowAmmo();
    }

    public void pay(Cost cost) throws NotEnoughAmmoException
    {
        if(!canPay(cost))throw new NotEnoughAmmoException();
        this.lastPay = cost;
        redAmmo -= cost.getRedAmmo();
        blueAmmo -= cost.getBlueAmmo();
        yellowAmmo -= cost.getYellowAmmo();
    }

    public void reverseLastPay()
    {
        if(lastPay == null)return;
        addRedAmmo(lastPay.getRedAmmo());
        addBlueAmmo(lastPay.getBlueAmmo());
        addYellowAmmo(lastPay.getYellowAmmo());
    }

    public GameBoardData getData()
    {
        LinkedHashMap<GameColor, Integer> damages = new LinkedHashMap<>();
        LinkedHashMap<GameColor, Integer> markers = new LinkedHashMap<>();

        receivedDamage.forEach((player, integer) -> damages.put(player.getColor(), integer));
        this.markers.forEach(((player, integer) -> markers.put(player.getColor(), integer)));

        return new GameBoardData(redAmmo, blueAmmo, yellowAmmo, skulls, damages, markers);
    }

}
