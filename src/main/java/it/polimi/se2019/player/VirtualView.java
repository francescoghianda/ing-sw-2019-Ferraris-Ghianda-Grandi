package it.polimi.se2019.player;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.controller.Match;
import it.polimi.se2019.controller.settings.MatchSettings;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.network.ClientConnection;
import it.polimi.se2019.network.ClientsManager;
import it.polimi.se2019.network.message.*;
import it.polimi.se2019.ui.GameEvent;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.utils.logging.Logger;

import java.util.ArrayList;
import java.util.Arrays;

public class VirtualView implements UI
{
    private final ClientConnection client;
    private TimeoutTime timeoutTime;

    public VirtualView(ClientConnection client)
    {
        this.client = client;
        timeoutTime = TimeoutTime.seconds(MatchSettings.getInstance().getResponseTimerSeconds());
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
    public String login()
    {
        String username = (String) client.getResponseTo(RequestFactory.newActionRequest("username", UI::login), TimeoutTime.INDETERMINATE).getContent();

        while(ClientsManager.getInstance().getConnectedClientsUsername().contains(username))
        {
            username = (String) client.getResponseTo(RequestFactory.newActionRequest("invalid_username", UI::login), TimeoutTime.INDETERMINATE).getContent();
        }

        client.getUser().setUsername(username);
        boolean reconnected = false;
        if(ClientsManager.getInstance().getDisconnectedClientsUsername().contains(username))
        {
            ClientsManager.getInstance().registerClient(client);

            if(client.getMatch().getState() != Match.State.ENDED)
            {
                client.getGameController().playerReconnected(client);
                reconnected = true;
            }

            Logger.warning("Client "+username+" has reconnected!");
        }
        else
        {
            logged();
            ClientsManager.getInstance().registerClient(client);
        }
        client.setLogged(true, reconnected);

        return username;
    }

    @Override
    public void timeout()
    {
        client.sendMessageToClient(new AsyncMessage("timeout", UI::timeout));
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
            return (Boolean) client.getResponseTo(RequestFactory.newActionRequest("not_enough_ammo", ui -> ui.notEnoughAmmo(true)), timeoutTime).getContent();
        }

        client.sendMessageToClient(new AsyncMessage("not_enough_ammo", ui -> ui.notEnoughAmmo(false)));
        return false;
    }

    @Override
    public void roundStart()
    {
        client.sendMessageToClient(new AsyncMessage("round_start", UI::roundStart));
    }

    @Override
    public void roundEnd()
    {
        client.sendMessageToClient(new AsyncMessage("round_end", UI::roundEnd));
    }

    @Override
    public String chooseOrCancel(Bundle<String, ArrayList<String>> options) throws CanceledActionException
    {
        return (String) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_or_cancel", ui -> ui.chooseOrCancel(options)), timeoutTime).getContent();
    }

    public String chooseOrCancel(String question, String... options) throws CanceledActionException
    {
        return chooseOrCancel(new Bundle<>(question, new ArrayList<>(Arrays.asList(options))));
    }

    public String choose(Bundle<String, ArrayList<String>> options)
    {
        return (String) client.getResponseTo(RequestFactory.newActionRequest("choose", ui -> ui.choose(options)), timeoutTime).getContent();
    }

    public String choose(String question, String... options)
    {
        return choose(new Bundle<>(question, new ArrayList<>(Arrays.asList(options))));
    }

    @Override
    public String chooseSpawnPoint(Card option1, Card option2)
    {
        return (String) client.getResponseTo(RequestFactory.newActionRequest("choose_spawn_point", ui -> ui.chooseSpawnPoint(option1, option2)), timeoutTime).getContent();
    }

    @Override
    public Action chooseActionFrom(Action[] possibleActions)
    {
        return (Action) client.getResponseTo(RequestFactory.newActionRequest("choose_action", ui -> ui.chooseActionFrom(possibleActions)), timeoutTime).getContent();
    }

    @Override
    public Coordinates chooseBlock(int maxDistance) throws CanceledActionException
    {
        return (Coordinates) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_block", ui -> ui.chooseBlock(maxDistance)), timeoutTime).getContent();
    }

    @Override
    public Coordinates chooseBlockFrom(ArrayList<Coordinates> coordinates) throws CanceledActionException
    {
        return (Coordinates) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_block_from", ui -> ui.chooseBlockFrom(coordinates)), timeoutTime).getContent();
    }

    @Override
    public Card chooseWeaponFromPlayer() throws CanceledActionException
    {
        return (Card) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_weapon_from_player", UI::chooseWeaponFromPlayer), timeoutTime).getContent();
    }

    @Override
    public Card chooseWeaponFromBlock() throws CanceledActionException
    {
        return (Card) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_weapon_from_block", UI::chooseWeaponFromBlock), timeoutTime).getContent();
    }

    @Override
    public Card choosePowerUp() throws CanceledActionException
    {
        return (Card) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_power_up", UI::choosePowerUp), timeoutTime).getContent();
    }

    @Override
    public void handle(GameEvent event)
    {

    }
}
