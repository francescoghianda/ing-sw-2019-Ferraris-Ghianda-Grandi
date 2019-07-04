package it.polimi.se2019.player;

import it.polimi.se2019.utils.constants.GameColor;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * defines and keep the data of a gameboard when they have to be sent
 */
public class GameBoardData implements Serializable
{
    public static final long serialVersionUID = 10L;

    private final int redAmmo;
    private final int blueAmmo;
    private final int yellowAmmo;
    private final int skulls;
    private final int points;
    private final LinkedHashMap<GameColor, Integer> damages;
    private final LinkedHashMap<GameColor, Integer> markers;

    public GameBoardData(int redAmmo, int blueAmmo, int yellowAmmo, int skulls, LinkedHashMap<GameColor, Integer> damages, LinkedHashMap<GameColor, Integer> markers, int points)
    {
        this.redAmmo = redAmmo;
        this.blueAmmo = blueAmmo;
        this.yellowAmmo = yellowAmmo;
        this.skulls = skulls;
        this.points = points;
        this.damages = damages;
        this.markers = markers;
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

    public int getPoints()
    {
        return points;
    }

    public LinkedHashMap<GameColor, Integer> getDamages()
    {
        return damages;
    }

    public LinkedHashMap<GameColor, Integer> getMarkers()
    {
        return markers;
    }
}
