package it.polimi.se2019.controller.action;

import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.player.ImpossibleActionException;
import it.polimi.se2019.player.Player;

public class UsePowerUpAction extends ControllerAction
{
    private Player player;
    private PowerUpCard powerUp;


    public UsePowerUpAction(GameController gameController, Player player, PowerUpCard powerUp)
    {
        super(gameController);

        this.player = player;
        this.powerUp = powerUp;
    }

    @Override
    public void execute() throws CanceledActionException, ImpossibleActionException
    {
        if(powerUp.getUse() != PowerUpCard.Use.ALWAYS)throw new ImpossibleActionException(ImpossibleActionException.Cause.INVALID_POWER_UP);
        powerUp.apply(player, gameController);
        player.removePowerUp(powerUp);
        gameController.getPowerUpCardDeck().addCard(powerUp);
    }
}
