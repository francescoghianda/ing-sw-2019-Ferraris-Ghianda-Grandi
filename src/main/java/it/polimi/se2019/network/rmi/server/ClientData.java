package it.polimi.se2019.network.rmi.server;

import it.polimi.se2019.player.Player;

class ClientData
{

    private Player player;
    private String nickname;
    private boolean logged;

    ClientData()
    {

    }

    Player getPlayer()
    {
        return player;
    }

    void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    String getNickname()
    {
        return this.nickname;
    }

    void setLogged(boolean logged)
    {
        this.logged = logged;
    }

    boolean isLogged()
    {
        return this.logged;
    }
}
