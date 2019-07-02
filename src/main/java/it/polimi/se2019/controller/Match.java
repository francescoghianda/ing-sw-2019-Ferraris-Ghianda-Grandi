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

public class Match
{
    private static AtomicInteger idsGenerator = new AtomicInteger(0);

    public enum State
    {
        WAITING_FOR_PLAYERS, RUNNING, ENDED
    }

    private final GameController gameController;
    private State state;

    private ObservableList<Player> players;
    private List<GameColor> availablePlayerColors;
    private Random random;

    private int matchId;

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

    public ObservableList<Player> getPlayers()
    {
        return players;
    }

    public void startGame()
    {
        state = State.RUNNING;
        gameController.startGameTimer();
    }

    public void gameEnded()
    {
        state = State.ENDED;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public GameController getGameController()
    {
        return gameController;
    }

    public State getState()
    {
        return this.state;
    }

    public int getMatchId()
    {
        return matchId;
    }

    public MatchData getData()
    {
        return new MatchData(matchId, state, players.size());
    }
}