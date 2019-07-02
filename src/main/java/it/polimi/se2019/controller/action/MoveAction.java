package it.polimi.se2019.controller.action;

import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.player.Action;
import it.polimi.se2019.player.Player;

import java.util.List;

public class MoveAction extends ControllerAction
{
    private Player player;
    private Block block;

    public MoveAction(GameController gameController, Player player, Block block)
    {
        super(gameController);
        this.player = player;
        this.block = block;
    }

    @Override
    public void execute()
    {
        if(block.equals(player.getBlock())) return;

        int distance = block.getDistanceFrom(player.getBlock());

        List<Block> path = player.getBlock().getRandomPathTo(block).getBlocks();

        path.forEach(pathBlock ->
        {
            try
            {
                player.setBlock(pathBlock);
                gameController.sendBroadcastUpdate();
                Thread.sleep(400);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        });

        player.setBlock(block);
        for(int i = 0; i < distance; i++)player.addExecutedAction(Action.MOVE);
    }
}
