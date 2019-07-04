package it.polimi.se2019.controller;

import it.polimi.se2019.map.MapData;
import it.polimi.se2019.player.PlayerData;
import it.polimi.se2019.utils.constants.GameMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Includes all the data of the current game
 */
public class GameData implements Serializable
{
    private static final long serialVersionUID = -2016341428516036292L;

    private final MapData map;
    private final PlayerData player;
    private final int remainingSkulls;
    private final ArrayList<Death> deaths;

    private final int powerUpDeckSize;
    private final int weaponDeckSize;

    private final GameMode gameMode;
    private final MatchData matchData;

    private List<PlayerData> usernameList;

    /**
     * constructs a new Gamedata
     * @param map
     * @param player
     * @param remainingSkulls
     * @param deaths
     * @param powerUpDeckSize
     * @param weaponDeckSize
     * @param gameMode
     * @param matchData
     */
    public GameData(MapData map, PlayerData player, int remainingSkulls, ArrayList<Death> deaths, int powerUpDeckSize, int weaponDeckSize, GameMode gameMode, MatchData matchData)
    {
        this.map = map;
        this.player = player;
        this.remainingSkulls = remainingSkulls;
        this.deaths = deaths;
        this.powerUpDeckSize = powerUpDeckSize;
        this.weaponDeckSize = weaponDeckSize;
        this.gameMode = gameMode;
        this.matchData = matchData;
    }

    public MatchData getMatchData()
    {
        return matchData;
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

    public int getDeathsNumber()
    {
        return this.deaths.size();
    }

    public List<Death> getDeaths()
    {
        return new ArrayList<>(deaths);
    }

    public int getPowerUpDeckSize()
    {
        return powerUpDeckSize;
    }

    public int getWeaponDeckSize()
    {
        return weaponDeckSize;
    }

    public GameMode getGameMode()
    {
        return gameMode;
    }

    /**
     *
     * @return the username list of players' s data of the specific game
     */
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
