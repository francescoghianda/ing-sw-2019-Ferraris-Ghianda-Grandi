package it.polimi.se2019.controller.action;

import it.polimi.se2019.card.deck.Deck;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.controller.TimeOutException;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.network.message.ConnectionErrorException;
import it.polimi.se2019.player.Player;

public class RespawnAction extends ControllerAction
{

    private Player player;

    public RespawnAction(GameController gameController, Player player)
    {
        super(gameController);
        this.player = player;
    }

    @Override
    public void execute()
    {
        Deck<PowerUpCard> powerUpCardDeck = gameController.getPowerUpCardDeck();

        PowerUpCard powerUp = player.powerUpsSize() <= 0 ? powerUpCardDeck.getFirstCard() : null;

        try
        {
            String chosenId = player.getView().chooseSpawnPoint(powerUp == null ? null : powerUp.getCardData(), null);
            PowerUpCard chosenPowerUp = PowerUpCard.findById(chosenId);
            Block spawnPoint = gameController.getMap().findRoomByColor(chosenPowerUp.getColor()).getSpawnPoint();

            if(powerUp == null)player.removePowerUp(chosenPowerUp);
            powerUpCardDeck.addCard(chosenPowerUp);

            player.getGameBoard().resetDamageAndMarks();
            player.setBlock(spawnPoint);
            gameController.sendBroadcastUpdate();
        }
        catch (ConnectionErrorException | TimeOutException e)
        {
            if(powerUp != null)
            {
                Block spawnPoint = gameController.getMap().findRoomByColor(powerUp.getColor()).getSpawnPoint();
                powerUpCardDeck.addCard(powerUp);
                player.setBlock(spawnPoint);
            }
            else
            {
                PowerUpCard powerUpCard = player.getRandomPowerUp();
                player.removePowerUp(powerUpCard);
                powerUpCardDeck.addCard(powerUpCard);
                Block spawnPoint = gameController.getMap().findRoomByColor(powerUpCard.getColor()).getSpawnPoint();
                player.setBlock(spawnPoint);
            }
            player.getGameBoard().resetDamageAndMarks();
            gameController.sendBroadcastUpdate();
        }
    }
}
