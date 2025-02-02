package it.polimi.se2019.player;

import it.polimi.se2019.card.cost.Cost;
import it.polimi.se2019.ui.cli.Option;
import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.constants.GameMode;

import java.io.Serializable;
import java.util.*;

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
    private int points;

    private Cost lastPay;

    /**
     * gameboard that istances received damages and markers
     */
    public GameBoard()
    {
        receivedDamage = new LinkedHashMap<>();
        markers = new LinkedHashMap<>();
    }

    public int getMaxPointsValue(boolean finalFrenzyMode)
    {
        if(finalFrenzyMode)return skulls == 0 ? 2 : 1;

        int max = 8 - (2*skulls);
        return max > 1 ? max : 1;
    }

    public int getTotalReceivedDamage()
    {
        int total = 0;
        for(Integer damage : receivedDamage.values())total += damage;
        return total;
    }

    public void resetDamageAndMarks()
    {
        receivedDamage.clear();
        markers.clear();
    }

    public void addSkull()
    {
        skulls++;
    }

    public void addPoints(int points)
    {
        this.points += points;
    }

    public int getPoints()
    {
        return points;
    }

    public LinkedHashMap<Player, Integer> getReceivedDamage()
    {
        return new LinkedHashMap<>(receivedDamage);
    }

    public Optional<Player> getFirstBloodPlayer()
    {
        return receivedDamage.keySet().stream().findFirst();
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
     * @param player the player who hits this player
     * @param damage the received damage
     */
    public void addDamage(Player player, int damage)
    {
        if(damage < 0)throw new NegativeValueException();

        damage += markers.getOrDefault(player, 0);
        markers.replace(player, 0);

        if(getTotalReceivedDamage()+damage > 12)
        {
            damage -= getTotalReceivedDamage()+damage-12;
        }

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

    /**
     * it defines the payment for a ammocard, if it is possible
     * @param cost is the cost of the ammocard
     * @throws NotEnoughAmmoException
     */
    public void pay(Cost cost) throws NotEnoughAmmoException
    {
        if(!canPay(cost))throw new NotEnoughAmmoException();
        this.lastPay = cost;
        redAmmo -= cost.getRedAmmo();
        blueAmmo -= cost.getBlueAmmo();
        yellowAmmo -= cost.getYellowAmmo();
    }

    /**
     * if a player pays for a ammocard and then the action is canceled, the payment is reversed
     */
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

        return new GameBoardData(redAmmo, blueAmmo, yellowAmmo, skulls, damages, markers, points);
    }

}
