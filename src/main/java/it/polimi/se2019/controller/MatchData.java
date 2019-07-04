package it.polimi.se2019.controller;

import java.io.Serializable;

/**
 * containes all the data of the current match
 */
public class MatchData implements Serializable
{
    private static final long serialVersionUID = 1055710234212493850L;

    private final int matchId;
    private final Match.State matchState;
    private final int playersNumber;

    /**
     * constructs a new match data
     * @param matchId
     * @param matchState
     * @param playersNumber
     */
    public MatchData(int matchId, Match.State matchState, int playersNumber)
    {
        this.matchId = matchId;
        this.matchState = matchState;
        this.playersNumber = playersNumber;
    }

    public int getMatchId()
    {
        return matchId;
    }

    public Match.State getMatchState()
    {
        return matchState;
    }

    public int getPlayersNumber()
    {
        return playersNumber;
    }
}
