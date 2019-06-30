package it.polimi.se2019.controller.action;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.player.Player;

import java.util.LinkedHashMap;

public class CountPointsAction extends ControllerAction
{

    private Player deadPlayer;

    public CountPointsAction(GameController gameController, Player deadPlayer)
    {
        super(gameController);
        this.deadPlayer = deadPlayer;
    }

    @Override
    public void execute()
    {
        LinkedHashMap<Player, Integer> receivedDamage = deadPlayer.getGameBoard().getReceivedDamage();


    }
}
