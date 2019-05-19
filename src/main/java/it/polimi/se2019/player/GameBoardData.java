package it.polimi.se2019.player;

import it.polimi.se2019.utils.constants.GameColor;

import java.io.Serializable;
import java.util.HashMap;

public class GameBoardData implements Serializable
{
    private final int redAmmo;
    private final int blueAmmo;
    private final int yellowAmmo;
    private final int skulls;
    private final HashMap<GameColor, Integer> damages;
    private final HashMap<GameColor, Integer> markers;


    public GameBoardData(int redAmmo, int blueAmmo, int yellowAmmo, int skulls, HashMap<GameColor, Integer> damages, HashMap<GameColor, Integer> markers)
    {
        this.redAmmo = redAmmo;
        this.blueAmmo = blueAmmo;
        this.yellowAmmo = yellowAmmo;
        this.skulls = skulls;
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

    public HashMap<GameColor, Integer> getDamages()
    {
        return damages;
    }

    public HashMap<GameColor, Integer> getMarkers()
    {
        return markers;
    }
}
