package it.polimi.se2019.controller;

import it.polimi.se2019.map.MapData;
import it.polimi.se2019.player.PlayerData;

import java.io.Serializable;

public class GameData implements Serializable
{
    private final MapData map;
    private final PlayerData player;
    private final int remainingSkulls;
    private final int deaths;

    public GameData(MapData map, PlayerData player, int remainingSkulls, int deaths)
    {
        this.map = map;
        this.player = player;
        this.remainingSkulls = remainingSkulls;
        this.deaths = deaths;
    }

    public MapData getMap()
    {
        return map;
    }

    public PlayerData getPlayer()
    {
        return player;
    }

    public int getRemainingSkulls()
    {
        return remainingSkulls;
    }

    public int getDeaths()
    {
        return this.deaths;
    }
}
