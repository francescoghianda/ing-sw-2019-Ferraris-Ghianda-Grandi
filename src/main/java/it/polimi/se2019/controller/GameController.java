package it.polimi.se2019.controller;

import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.map.Map;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.NetworkServer;
import it.polimi.se2019.network.message.Messages;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.constants.GameMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameController
{
    private Random random;
    private List<GameColor> availablePlayerColors;
    private List<Player> players;
    private Map map;
    private GameMode gameMode;
    private NetworkServer server;

    public GameController(NetworkServer server)
    {
        this.server = server;
        availablePlayerColors = new ArrayList<>(Arrays.asList(GameColor.values()));
        availablePlayerColors.remove(GameColor.RED);
        random = new Random();
        map = Map.createMap();
        players = new ArrayList<>();
    }

    public void startGame()
    {

    }

    private void selectStartingPlayer()
    {
        Player firstPlayer = players.get(random.nextInt(players.size()));
        firstPlayer.setAsStartingPlayer(true);
        firstPlayer.sendMessageToClient(Messages.YOU_ARE_FIRST_PLAYER);
        //firstPlayer.notifyOtherClients();
    }

    public GameMode getGameMode()
    {
        return this.gameMode;
    }

    public Player createPlayer(ClientConnection server)
    {
        GameColor color = availablePlayerColors.get(random.nextInt(availablePlayerColors.size()));
        availablePlayerColors.remove(color);
        Player player = new Player(color, this, server);
        players.add(player);
        return player;
    }

    public void movePlayer(Player player, Block block)
    {

    }

    public void hitPlayer(Player shooter, Player hitPlayer, WeaponCard weapon)
    {

    }

    public void grab(Player player)
    {

    }

    public void reload(Player player, WeaponCard weapon)
    {

    }

    public void reloadAll(Player player)
    {

    }
}
