package it.polimi.se2019.controller.action;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.CardData;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.player.Action;
import it.polimi.se2019.player.NotEnoughAmmoException;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReloadAction extends ControllerAction
{
    public static final int RELOAD_ONE = 0;
    public static final int RELOAD_ALL = 1;

    private Player player;
    private int mode;

    public ReloadAction(GameController gameController, Player player, int mode)
    {
        super(gameController);
        this.player = player;
        this.mode = mode;
    }

    @Override
    public void execute()
    {
        if(!player.hasUnloadedWeapons())return;
        List<WeaponCard> unloadedWeapons = getUnloadedWeapons(player);
        if(unloadedWeapons.isEmpty())return;

        if(mode == RELOAD_ONE)reloadOne(unloadedWeapons);
        else reloadAll(unloadedWeapons);
    }

    private void reloadAll(List<WeaponCard> unloadedWeapons)
    {
        ArrayList<CardData> unloadedWeaponsData = unloadedWeapons.stream().map(Card::getCardData).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<CardData> toReload = player.getView().chooseWeaponsToReload(unloadedWeaponsData);
        if(!unloadedWeaponsData.containsAll(toReload))return;
        List<WeaponCard> toReloadWeapons = toReload.stream().map(card -> WeaponCard.findCardById(card.getId())).collect(Collectors.toList());

        /*int totalRedCost = toReloadWeapons.stream().map(WeaponCard::getReloadCost).map(Cost::getRedAmmo).reduce(0, Integer::sum);
        int totalBlueCost = toReloadWeapons.stream().map(WeaponCard::getReloadCost).map(Cost::getBlueAmmo).reduce(0, Integer::sum);
        int totalYellowCost = toReloadWeapons.stream().map(WeaponCard::getReloadCost).map(Cost::getYellowAmmo).reduce(0, Integer::sum);*/

        toReloadWeapons.forEach(weaponCard ->
        {
            try
            {
                player.getGameBoard().pay(weaponCard.getReloadCost());
                weaponCard.reload();
            }
            catch (NotEnoughAmmoException e)
            {
                Logger.warning("Weapon "+weaponCard+" cannot be reloaded by player "+player);
            }
        });
    }

    private void reloadOne(List<WeaponCard> unloadedWeapons)
    {
        WeaponCard chosen = WeaponCard.findCardById(player.getView().chooseWeaponToReload(unloadedWeapons.stream().map(Card::getCardData).collect(Collectors.toCollection(ArrayList::new))).getId());
        try
        {
            player.getGameBoard().pay(chosen.getReloadCost());
            chosen.reload();
            player.addExecutedAction(Action.RELOAD);
        }
        catch (NotEnoughAmmoException e)
        {
            Logger.warning("Weapon "+chosen+" cannot be reloaded by player "+player);
        }
    }

    private List<WeaponCard> getUnloadedWeapons(Player player)
    {
        List<WeaponCard> unloadedWeapons = player.getUnloadedWeapons();
        return unloadedWeapons.stream().filter(weapon -> player.getGameBoard().canPay(weapon.getReloadCost())).collect(Collectors.toList());
    }
}
