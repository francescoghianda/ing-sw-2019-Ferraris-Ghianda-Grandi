package it.polimi.se2019.controller;

import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.list.ObservableList;

import java.util.List;

/**
 * Manages the round indicated by its players, an index, and index the player and a number for the round
 *
 */
public class RoundManager
{
    private ObservableList<Player> players;
    private int playerIndex;
    private int roundIndex;
    private int roundNumber;

    public RoundManager(ObservableList<Player> players)
    {
        this.players = players;
        roundNumber = 1;
    }

    /*public Player next()
    {
        Player nextPlayer = getNextPlayer();
        while (!nextPlayer.getClientConnection().isConnected() || !nextPlayer.getClientConnection().isLogged())nextPlayer = getNextPlayer();
        return nextPlayer;
    }*/

    /**
     *
     * @return
     */

    public Player next()
    {
        if(playerIndex >= players.size())
        {
            if(roundIndex >= players.size())roundNumber++;
            playerIndex = 0;
            roundIndex = 0;
        }

        Player currentPlayer = players.get(playerIndex);
        playerIndex++;
        roundIndex++;
        return currentPlayer;
    }

    public int getRoundNumber()
    {
        return this.roundNumber;
    }

    /**
     * checks if the round is the first one
     * @return
     */
    public boolean isFirstRound()
    {
        return roundNumber == 1;
    }

    /**
     * checks if the player is the first one of that round
     * @param player
     * @return
     */

    public boolean isFirstPlayer(Player player)
    {
        return players.get(0).equals(player);
    }

    public boolean isLastPlayer(Player player)
    {
        return players.get(players.size()-1).equals(player);
    }
}
