package it.polimi.se2019.controller.action;

import it.polimi.se2019.card.CardData;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.player.Action;
import it.polimi.se2019.player.NotEnoughAmmoException;
import it.polimi.se2019.player.Player;

public class GrabAction extends ControllerAction
{
    private Player player;

    public GrabAction(GameController gameController, Player player)
    {
        super(gameController);
        this.player = player;
    }

    /**
     * Execute the action associated with the grab action of the game
     * @throws CanceledActionException
     */
    @Override
    public void execute() throws CanceledActionException
    {
        Block playerBlock = player.getBlock();
        if(playerBlock.isSpawnPoint() && playerBlock.isWeaponCardPresent())
        {
            WeaponCard weaponCard = selectWeaponFromBlock();

            try
            {
                player.getGameBoard().pay(weaponCard.getBuyCost());
                if(player.weaponsSize() >= 3)
                {
                    try
                    {
                        WeaponCard substituteWeapon = selectWeaponFromPlayer();
                        player.removeWeapon(substituteWeapon);
                        weaponCard.reload();
                        player.addWeaponCard(weaponCard);
                        playerBlock.replaceWeaponCard(weaponCard, substituteWeapon);
                        substituteWeapon.reload();
                    }
                    catch (CanceledActionException e)
                    {
                        player.getGameBoard().reverseLastPay();
                        throw new CanceledActionException(e.getCanceledCause());
                    }
                }
                else
                {
                    playerBlock.removeWeaponCard(weaponCard);
                    weaponCard.reload();
                    player.addWeaponCard(weaponCard);
                }
                player.addExecutedAction(Action.GRAB);
            }
            catch (NotEnoughAmmoException e)
            {
                notEnoughAmmo();
            }

        }
        else if(playerBlock.getAmmoCard() != null)
        {
            playerBlock.getAmmoCard().apply(player, gameController.getPowerUpCardDeck());
            gameController.getAmmoCardDeck().addCard(playerBlock.getAmmoCard());
            playerBlock.removeAmmoCard();
            player.addExecutedAction(Action.GRAB);
        }
    }


    private WeaponCard selectWeaponFromPlayer() throws CanceledActionException
    {
        CardData chosen = player.getView().chooseWeaponToSwap();
        return WeaponCard.findCardById(chosen.getId());
    }

    private WeaponCard selectWeaponFromBlock() throws CanceledActionException
    {
        CardData chosen = player.getView().chooseWeaponFromBlock();
        return WeaponCard.findCardById(chosen.getId());
    }

    private void notEnoughAmmo() throws CanceledActionException
    {
        if(player.powerUpsSize() > 0)
        {
            boolean sellPowerUp = player.getView().notEnoughAmmo(true);
            if(!sellPowerUp)return;
            CardData chosen = player.getView().choosePowerUp();
            PowerUpCard powerUpCard = PowerUpCard.findById(chosen.getId());

            switch (powerUpCard.getColor())
            {
                case BLUE:
                    player.getGameBoard().addBlueAmmo(1);
                    break;
                case RED:
                    player.getGameBoard().addRedAmmo(1);
                    break;
                case YELLOW:
                    player.getGameBoard().addYellowAmmo(1);
                    break;
            }
            player.removePowerUp(powerUpCard);
            gameController.getPowerUpCardDeck().addCard(powerUpCard);
        }
        else
        {
            player.getView().notEnoughAmmo(false);
        }
    }
}
