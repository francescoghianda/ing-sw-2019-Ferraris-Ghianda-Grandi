package it.polimi.se2019.ui;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.controller.TimeOutException;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Action;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * initializes the user interface
 */
public interface UI extends GameEventHandler
{
    void update(GameData data);
    void startUI();

    String login();
    String selectPlayer();
    String selectBlock();
    void requestFocus();
    void logged();
    void gameIsStarting();
    void gameStarted();
    void showTimerCountdown(int remainSeconds);
    void youAreFirstPlayer();
    void firstPlayerIs(String firstPlayerUsername);
    void connectionRefused();
    void roundStart();
    void roundEnd();
    void timeout();

    boolean notEnoughAmmo(boolean askToSellPowerUp);
    String chooseOrCancel(Bundle<String, ArrayList<String>> options) throws CanceledActionException;
    String choose(Bundle<String, ArrayList<String>> options);
    String chooseSpawnPoint(Card option1, Card option2);
    Bundle<Action, Serializable> chooseActionFrom(Action[] possibleActions);
    Coordinates chooseBlock(int maxDistance) throws CanceledActionException;
    Coordinates chooseBlockFrom(ArrayList<Coordinates> coordinates)throws CanceledActionException;
    Card chooseWeaponFromPlayer() throws CanceledActionException;
    Card chooseWeaponFromBlock() throws CanceledActionException;
    Card choosePowerUp() throws CanceledActionException;
    ArrayList<Card> chooseWeaponsToReload(ArrayList<Card> weapons);
    Card chooseWeaponToReload(ArrayList<Card> weapons);
}
