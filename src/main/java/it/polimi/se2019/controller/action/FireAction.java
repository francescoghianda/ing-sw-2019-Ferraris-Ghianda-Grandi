package it.polimi.se2019.controller.action;

import it.polimi.se2019.card.weapon.OptionalEffect;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Action;
import it.polimi.se2019.player.ImpossibleActionException;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class FireAction extends ControllerAction
{
    private Player player;

    public FireAction(GameController gameController, Player player)
    {
        super(gameController);
        this.player = player;
    }

    @Override
    public void execute() throws CanceledActionException, ImpossibleActionException
    {
        WeaponCard chosenWeapon = WeaponCard.findCardById(player.getView().chooseWeaponFromPlayer().getId());

        if(chosenWeapon == null || !chosenWeapon.isLoad())throw new ImpossibleActionException(ImpossibleActionException.Cause.WEAPON_NOT_LOADED);

        try
        {
            if(chosenWeapon.hasEnabledOptionalEffect())useOptionalEffect(chosenWeapon);
            if(chosenWeapon.hasAlternateFireMode())chooseFireMode(chosenWeapon);

            chosenWeapon.fire(player, gameController);

            if(chosenWeapon.hasEnabledOptionalEffect())useOptionalEffect(chosenWeapon);
        }
        finally
        {
            if(chosenWeapon.isUsed())
            {
                chosenWeapon.setLoad(false);
                player.addExecutedAction(Action.FIRE);
            }
            chosenWeapon.reset();
        }
    }

    private void chooseFireMode(WeaponCard weapon)
    {
        String chosenFireMode = player.getView().choose("Scegli la modalità di fuoco", "Modalità Base", "Modalità Alternativa");
        if(chosenFireMode.equals("Modalità Base")) weapon.setFireMode(WeaponCard.Mode.BASIC);
        else weapon.setFireMode(WeaponCard.Mode.ALTERNATE_FIRE);
    }

    private void useOptionalEffect(WeaponCard weapon)
    {
        if(weapon.getEnabledOptionalEffects().isEmpty())return;

        String message = "Vuoi usare un effetto opzionale?";
        while(!weapon.getEnabledOptionalEffects().isEmpty())
        {
            if(player.getView().choose(message, "Si", "No").equals("Si"))
            {
                ArrayList<String> optionalEffectNames = weapon.getEnabledOptionalEffects().stream().map(OptionalEffect::getName).collect(Collectors.toCollection(ArrayList::new));
                String chosenEffect = "";
                try
                {
                    chosenEffect = player.getView().chooseOrCancel(Bundle.of("Che effetto vuoi usare?", optionalEffectNames));
                    weapon.useOptionalEffect(player, weapon.getOptionalEffect(chosenEffect), gameController);
                    weapon.getOptionalEffect(chosenEffect).setEnabled(false);
                }
                catch (CanceledActionException e)
                {
                    Logger.warning("Effect "+chosenEffect+" was not executed cause"+e.getCanceledCause()+" - "+player);
                }
                catch (ImpossibleActionException e)
                {
                    Logger.warning("Effect "+chosenEffect+" was not executed cause"+e.cause()+" - "+player);
                }
                finally
                {
                    message = "Vuoi usare un altro effetto opzionale?";
                }
            }
            else
            {
                break;
            }
        }

    }
}
