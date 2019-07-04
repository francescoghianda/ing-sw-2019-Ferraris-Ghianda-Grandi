package it.polimi.se2019.player;

import it.polimi.se2019.card.CardData;
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

import java.io.Serializable;
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
    public void notifyImpossibleAction()
    {
        client.sendMessageToClient(new AsyncMessage("impossible_action", UI::notifyImpossibleAction));
    }

    @Override
    public void showNotification(String text)
    {
        client.sendMessageToClient(new AsyncMessage("notification", ui -> ui.showNotification(text)));
    }

    @Override
    public void closeConnection()
    {
        client.sendMessageToClient(new AsyncMessage("close_connection", UI::closeConnection));
    }

    @Override
    public boolean showScoreBoardAndChooseIfPlayAgain(ArrayList<PlayerData> scoreBoard)
    {
        return (boolean) client.getResponseTo(RequestFactory.newActionRequest("end_match", ui -> ui.showScoreBoardAndChooseIfPlayAgain(scoreBoard)), TimeoutTime.INDETERMINATE).getContent();
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
        return chooseOrCancel(Bundle.of(question, new ArrayList<>(Arrays.asList(options))));
    }

    public String choose(Bundle<String, ArrayList<String>> options)
    {
        return (String) client.getResponseTo(RequestFactory.newActionRequest("choose", ui -> ui.choose(options)), timeoutTime).getContent();
    }

    public String choose(String question, String... options)
    {
        return choose(Bundle.of(question, new ArrayList<>(Arrays.asList(options))));
    }

    @Override
    public String chooseSpawnPoint(CardData option1, CardData option2)
    {
        return (String) client.getResponseTo(RequestFactory.newActionRequest("choose_spawn_point", ui -> ui.chooseSpawnPoint(option1, option2)), timeoutTime).getContent();
    }

    @Override
    public Bundle<Action, Serializable> chooseActionFrom(Action[] possibleActions)
    {
        return Bundle.cast(client.getResponseTo(RequestFactory.newActionRequest("choose_action", ui -> ui.chooseActionFrom(possibleActions)), timeoutTime).getContent(), Action.class, Serializable.class);
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
    public CardData chooseWeaponFromPlayer() throws CanceledActionException
    {
        return (CardData) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_weapon_from_player", UI::chooseWeaponFromPlayer), timeoutTime).getContent();
    }

    @Override
    public CardData chooseWeaponFromBlock() throws CanceledActionException
    {
        return (CardData) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_weapon_from_block", UI::chooseWeaponFromBlock), timeoutTime).getContent();
    }

    @Override
    public CardData choosePowerUp() throws CanceledActionException
    {
        return (CardData) client.getResponseTo(RequestFactory.newCancellableActionRequest("choose_power_up", UI::choosePowerUp), timeoutTime).getContent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArrayList<CardData> chooseWeaponsToReload(ArrayList<CardData> weapons)
    {
        return (ArrayList<CardData>) client.getResponseTo(RequestFactory.newActionRequest("choose_weapons_to_reload", ui -> ui.chooseWeaponsToReload(weapons)), timeoutTime).getContent();
    }

    @Override
    public CardData chooseWeaponToReload(ArrayList<CardData> weapons)
    {
        return (CardData) client.getResponseTo(RequestFactory.newActionRequest("choose_weapon_to_reload", ui -> ui.chooseWeaponToReload(weapons)), timeoutTime).getContent();
    }

    @Override
    public void requestFocus()
    {
        client.sendMessageToClient(new AsyncMessage("request_focus", UI::requestFocus));
    }

    @Override
    public void handle(GameEvent event)
    {

    }
}
