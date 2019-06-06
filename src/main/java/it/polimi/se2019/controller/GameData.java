package it.polimi.se2019.controller;

import it.polimi.se2019.map.BlockData;
import it.polimi.se2019.map.MapData;
import it.polimi.se2019.player.PlayerData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameData implements Serializable
{
    private final MapData map;
    private final PlayerData player;
    private final int remainingSkulls;
    private final int deaths;

    private final int powerUpDeckSize;
    private final int weaponDeckSize;

    private List<PlayerData> usernameList;

    public GameData(MapData map, PlayerData player, int remainingSkulls, int deaths, int powerUpDeckSize, int weaponDeckSize)
    {
        this.map = map;
        this.player = player;
        this.remainingSkulls = remainingSkulls;
        this.deaths = deaths;
        this.powerUpDeckSize = powerUpDeckSize;
        this.weaponDeckSize = weaponDeckSize;
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

    public int getPowerUpDeckSize()
    {
        return powerUpDeckSize;
    }

    public int getWeaponDeckSize()
    {
        return weaponDeckSize;
    }

    public List<PlayerData> getPlayers()
    {
        if(usernameList == null)
        {
            usernameList = new ArrayList<>();
            map.getBlocksAsList().forEach(blockData -> usernameList.addAll(blockData.getPlayers()));
        }
        return usernameList;
    }
}
