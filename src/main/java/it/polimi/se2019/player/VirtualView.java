package it.polimi.se2019.player;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.message.AsyncMessage;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.network.message.RequestFactory;
import it.polimi.se2019.ui.GameEvent;
import it.polimi.se2019.ui.UI;

import java.util.ArrayList;

public class VirtualView implements UI
{
    private final ClientConnection client;

    public VirtualView(ClientConnection client)
    {
        this.client = client;
    }

    @Override
    public void update(GameData data)
    {
        client.sendMessageToClient(new AsyncMessage("update", ui -> ui.update(data)));
    }

    @Override
    public void startUI()
    {

    }

    @Override
    public String getUsername()
    {
        return (String) client.getResponseTo(RequestFactory.newActionRequest("get_username", UI::getUsername)).getContent();
    }

    @Override
    public String selectPlayer()
    {
        return null;
    }

    @Override
    public String selectBlock()
    {
        return null;
    }

    @Override
    public void logged()
    {
        client.sendMessageToClient(new AsyncMessage("logged", UI::logged));
    }

    @Override
    public void gameIsStarting()
    {
        client.sendMessageToClient(new AsyncMessage("game_is_starting", UI::gameIsStarting));
    }

    @Override
    public void gameStarted()
    {
        client.sendMessageToClient(new AsyncMessage("game_started", UI::gameStarted));
    }

    @Override
    public void showTimerCountdown(int remainSeconds)
    {
        client.sendMessageToClient(new AsyncMessage("timer_countdown", ui -> ui.showTimerCountdown(remainSeconds)));
    }

    @Override
    public void youAreFirstPlayer()
    {
        client.sendMessageToClient(new AsyncMessage("you_ara_first_player", UI::youAreFirstPlayer));
    }

    @Override
    public void firstPlayerIs(String firstPlayerUsername)
    {
        client.sendMessageToClient(new AsyncMessage("first_player_is", ui -> ui.firstPlayerIs(firstPlayerUsername)));
    }

    @Override
    public void connectionRefused()
    {

    }

    @Override
    public boolean notEnoughAmmo(boolean askToSellPowerUp)
    {
        if(askToSellPowerUp)
        {
            return (Boolean) client.getResponseTo(RequestFactory.newActionRequest("not_enough_ammo", ui -> ui.notEnoughAmmo(true))).getContent();
        }

        client.sendMessageToClient(new AsyncMessage("not_enough_ammo", ui -> ui.notEnoughAmmo(false)));
        return false;
    }

    @Override
    public String choose(Bundle<String, ArrayList<String>> options) throws CanceledActionException
    {
        return (String) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose", ui -> ui.choose(options))).getContent();
    }

    @Override
    public String chooseSpawnPoint(Card option1, Card option2)
    {
        return (String) client.getResponseTo(RequestFactory.newActionRequest("choose_spawn_point", ui -> ui.chooseSpawnPoint(option1, option2))).getContent();
    }

    @Override
    public Action chooseActionFrom(Action[] possibleActions)
    {
        return (Action) client.getResponseTo(RequestFactory.newActionRequest("choose_action", ui -> ui.chooseActionFrom(possibleActions))).getContent();
    }

    @Override
    public Coordinates chooseBlock(int maxDistance) throws CanceledActionException
    {
        return (Coordinates) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_block", ui -> ui.chooseBlock(maxDistance))).getContent();
    }

    @Override
    public Card chooseWeaponFromPlayer() throws CanceledActionException
    {
        return (Card) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_weapon_from_player", UI::chooseWeaponFromPlayer)).getContent();
    }

    @Override
    public Card chooseWeaponFromBlock() throws CanceledActionException
    {
        return (Card) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_weapon_from_block", UI::chooseWeaponFromBlock)).getContent();
    }

    @Override
    public Card choosePowerUp() throws CanceledActionException
    {
        return (Card) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_power_up", UI::choosePowerUp)).getContent();
    }

    @Override
    public void handle(GameEvent event)
    {

    }
}
