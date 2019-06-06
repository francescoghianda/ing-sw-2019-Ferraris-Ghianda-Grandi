package it.polimi.se2019.network;

import it.polimi.se2019.player.Player;

public class User
{
    private String username;
    private Player player;

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
}
