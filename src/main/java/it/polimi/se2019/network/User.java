package it.polimi.se2019.network;

import it.polimi.se2019.controller.Match;
import it.polimi.se2019.player.Player;

/**
 * creates an user defining its name, the associated player and the match
 */
public class User
{
    private String username;
    private Player player;
    private Match match;

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setMatch(Match match)
    {
        this.match = match;
    }

    public Match getMatch()
    {
        return match;
    }
}
