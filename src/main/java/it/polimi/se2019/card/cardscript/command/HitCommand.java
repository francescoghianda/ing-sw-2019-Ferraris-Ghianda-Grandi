package it.polimi.se2019.card.cardscript.command;

import it.polimi.se2019.card.cardscript.Executor;
import it.polimi.se2019.card.cardscript.command.parameter.ParameterTypes;
import it.polimi.se2019.card.cost.Cost;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.TimeOutException;
import it.polimi.se2019.map.Block;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.network.message.ConnectionErrorException;
import it.polimi.se2019.player.GameBoard;
import it.polimi.se2019.player.NotEnoughAmmoException;
import it.polimi.se2019.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * It receive a block or a player. If it receives a block, it gives a damage to all the players into that block.
 * If it receives a player,it gives him a quantity of damage equal to the value expressed in the "DIGIT" parameter
 */
public class HitCommand extends Command
{
    private static CommandPattern pattern = new CommandPattern(ParameterTypes.PLAYER_OR_BLOCK, ParameterTypes.DIGIT);

    private List<Player> players;
    private int damage;

    public HitCommand(Executor executor, String[] parameters)
    {
        super(executor, parameters, pattern);

        players = new ArrayList<>();

        if(getParam(0) != null)
        {
            if(isPlayer(parameters[0])) players.add((Player)getParam(0));
            else players.addAll(((Block)getParam(0)).getPlayers());
        }

        damage = Integer.parseInt(parameters[1]);
    }

    @Override
    public boolean exec()
    {
        if(players.isEmpty())return false;

        /*
        while (!executor.getContextPlayer().getOnFirePowerUps().isEmpty())
        {
            String answer = executor.getContextPlayer().getView().choose("Vuoi usare un power up?", "Si", "No");
            if(!answer.equals("Si"))break;
            try
            {
                PowerUpCard chosenPowerUp = PowerUpCard.findById(executor.getContextPlayer().getView().choosePowerUp().getId());

                if(chosenPowerUp.getUse() != PowerUpCard.Use.ON_FIRE)
                {
                    executor.getContextPlayer().getView().showNotification("Non puoi usare questo power up");
                }
                else
                {
                    chosenPowerUp.apply(executor.getContextPlayer(), executor.getContextPlayer().getGameController());
                }

            }
            catch (CanceledActionException e)
            {
                //Do nothing
            }
            catch (ImpossibleActionException e)
            {
                executor.getContextPlayer().getView().showNotification("Azione impossibile");
            }
        }*/

        useViewFinder();

        players.remove(executor.getContextPlayer());
        players.forEach(player ->
        {
            player.getGameBoard().addDamage(executor.getContextPlayer(), damage);
            List<PowerUpCard> tagBackGrenades = player.getTagBackGrenades();
            useTagBackGrenade(tagBackGrenades, player);
        });
        return true;
    }

    private void useTagBackGrenade(List<PowerUpCard> tagBackGrenade, Player player)
    {
        if(tagBackGrenade.isEmpty() || !player.getVisiblePlayers().contains(executor.getContextPlayer()) || !player.getClientConnection().isConnected())return;

        try
        {
            if(tagBackGrenade.size() == 1)
            {
                String answer = player.getView().choose("Vuoi usare la granata venom?", "Si", "No");
                if(answer.equals("Si"))
                {
                    executor.getContextPlayer().getGameBoard().addMarker(player, 1);
                    player.removePowerUp(tagBackGrenade.get(0));
                }
            }
            else
            {
                String answer = player.getView().choose("Vuoi usare una granata venom?", "Si", "No");
                if(answer.equals("Si"))
                {
                    try
                    {
                        PowerUpCard chosen;
                        do
                        {
                            chosen = PowerUpCard.findById(player.getView().choosePowerUp().getId());
                        }
                        while (!chosen.isTagBackGrenade());

                        executor.getContextPlayer().getGameBoard().addMarker(player, 1);
                        player.removePowerUp(chosen);

                    }
                    catch (CanceledActionException e)
                    {
                        //Do nothing
                    }
                }
            }
        }
        catch (TimeOutException e)
        {
            //Do nothing
        }
        catch (ConnectionErrorException e)
        {
            executor.getContextPlayer().getGameController().notifyOtherClients(player, virtualView -> virtualView.showNotification(player.getUsername()+" si è disconnesso!"));
        }
    }

    private void useViewFinder()
    {
        if(!canUseViewFinder())return;

        List<PowerUpCard> viewFinders = executor.getContextPlayer().getViewFinders();
        if(viewFinders.isEmpty())return;
        PowerUpCard viewFinder = chooseViewFinder(viewFinders);
        if(viewFinder == null)return;
        Optional<Player> chosenPlayer = choosePlayerForViewFinder();
        if (!chosenPlayer.isPresent())return;

        try
        {
            if(!payForViewFinder())return;
        }
        catch (CanceledActionException e)
        {
            return;
        }

        executor.getContextPlayer().removePowerUp(viewFinder);
        chosenPlayer.get().getGameBoard().addDamage(executor.getContextPlayer(),1);

    }

    private PowerUpCard chooseViewFinder(List<PowerUpCard> viewFinders)
    {
        if(viewFinders.size() == 1)
        {
            String answer = executor.getContextPlayer().getView().choose("Vuoi usare il mirino?", "Si", "No");
            if(answer.equals("Si"))
            {
                return viewFinders.get(0);
            }
            else return null;
        }
        else
        {
            String answer = executor.getContextPlayer().getView().choose("Vuoi usare un mirino?", "Si", "No");
            if(answer.equals("Si"))
            {
                try
                {
                    PowerUpCard chosen;
                    do
                    {
                        chosen = PowerUpCard.findById(executor.getContextPlayer().getView().choosePowerUp().getId());
                    }
                    while (!chosen.isViewFinder());

                    return chosen;

                }
                catch (CanceledActionException e)
                {
                    return null;
                }
            }
            else return null;
        }
    }

    private Optional<Player> choosePlayerForViewFinder()
    {
        ArrayList<String> usernames = players.stream().map(Player::getUsername).collect(Collectors.toCollection(ArrayList::new));
        String chosenUsername;
        try
        {
            chosenUsername = executor.getContextPlayer().getView().chooseOrCancel(Bundle.of("Su che giocatore lo vuoi usare?", usernames));
        }
        catch (CanceledActionException e)
        {
            return Optional.empty();
        }

        return executor.getContextPlayer().getGameController().findPlayerByUsername(chosenUsername);
    }

    private boolean payForViewFinder() throws CanceledActionException
    {
        Player contextPlayer = executor.getContextPlayer();
        ArrayList<String> options = new ArrayList<>();

        if(contextPlayer.getGameBoard().getRedAmmo() >= 1)options.add("Rosso");
        if(contextPlayer.getGameBoard().getBlueAmmo() >= 1)options.add("Blu");
        if(contextPlayer.getGameBoard().getYellowAmmo() >= 1)options.add("Yellow");

        String answer = executor.getContextPlayer().getView().chooseOrCancel(Bundle.of("Con che colore vuoi pagare?", options));

        Cost cost;
        if(answer.equals("Rosso")) cost = new Cost(1, 0, 0);
        else if(answer.equals("Blu")) cost = new Cost(0, 1, 0);
        else cost = new Cost(0, 0, 1);

        try
        {
            contextPlayer.getGameBoard().pay(cost);
        }
        catch (NotEnoughAmmoException e)
        {
            return false;
        }

        return true;
    }


    private boolean canUseViewFinder()
    {
        GameBoard gameBoard = executor.getContextPlayer().getGameBoard();
        return gameBoard.getRedAmmo() >= 1 || gameBoard.getBlueAmmo() >= 1 || gameBoard.getYellowAmmo() >= 1;
    }

    @Override
    public Commands getType()
    {
        return Commands.HIT;
    }
}
