package it.polimi.se2019.controller;

import it.polimi.se2019.controller.settings.MatchSettings;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.list.ListChangeAdapter;
import it.polimi.se2019.utils.list.ListChangeEvent;
import it.polimi.se2019.utils.list.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Defines the current match , which has a state, a gamecontroller, a list of gamecolor,  a list of players,
 * a random value and an identificator
 */
public class Match
{
    private static AtomicLong idsGenerator = new AtomicLong(0);

    public enum State
    {
        WAITING_FOR_PLAYERS, RUNNING, ENDED
    }

    private final GameController gameController;
    private State state;

    private ObservableList<Player> players;
    private List<GameColor> availablePlayerColors;
    private Random random;

    private long matchId;

    /**
     * constructs a new match
     */
    public Match()
    {
        players = new ObservableList<>();
        availablePlayerColors = new ArrayList<>(Arrays.asList(GameColor.values()));
        availablePlayerColors.remove(GameColor.RED);
        random = new Random();
        matchId = idsGenerator.getAndIncrement();

        players.addChangeListener(new ListChangeAdapter<Player>() {
            @Override
            public void onChanged(Player element, ListChangeEvent eventType)
            {
                if(eventType == ListChangeEvent.ELEMENT_ADDED && players.size() >= MatchSettings.getInstance().getMaxPlayers())
                {
                    startGame();
                }
            }
        });

        gameController = new GameController(this);
        state = State.WAITING_FOR_PLAYERS;
    }

    /**
     * creates a player for that match if the color is available
     * @param server The ClientConnection associated with the user that will control the new player
     * @return the player created
     * @throws TooManyPlayerException
     */
    public Player createPlayer(ClientConnection server)
    {
        if(availablePlayerColors.isEmpty())throw new TooManyPlayerException();
        GameColor color = availablePlayerColors.get(random.nextInt(availablePlayerColors.size()));
        Player player = new Player(color, gameController, server);

        availablePlayerColors.remove(color);

        player.getGameBoard().setRedAmmo(3);
        player.getGameBoard().setBlueAmmo(3);
        player.getGameBoard().setYellowAmmo(3);

        players.add(player);

        gameController.sendUpdate(player);

        return player;
    }

    /**
     *
     * @return an ObservableList containing all the player that play in in this match (also the disconnected player)
     */
    public ObservableList<Player> getPlayers()
    {
        return players;
    }

    /**
     *it starts the game
     */
    public void startGame()
    {
        if(state != State.RUNNING && players.size() >= MatchSettings.getInstance().getMinPlayers())
        {
            state = State.RUNNING;
            gameController.startGameTimer();
        }
    }

    /**
     *
     * @return the number of currently connected player
     */
    public int connectedPlayerSize()
    {
        return (int) players.stream().filter(player -> player.getClientConnection().isConnected()).count();
    }

    /**
     * Change the state of the match
     * @param state the new state to be setted
     */
    public void setState(State state)
    {
        this.state = state;
    }

    /**
     *
     * @return the GameController associated with this match
     */
    public GameController getGameController()
    {
        return gameController;
    }

    /**
     *
     * @return the current state of the match
     */
    public State getState()
    {
        return this.state;
    }

    /**
     *
     * @return the id of the match
     */
    public long getMatchId()
    {
        return matchId;
    }

    public MatchData getData()
    {
        return new MatchData(matchId, state, players.size());
    }
}