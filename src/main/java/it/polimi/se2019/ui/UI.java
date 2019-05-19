package it.polimi.se2019.ui;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.network.message.Bundle;

import java.util.ArrayList;


/**
 * initializes the user interface
 */
public interface UI extends GameEventHandler
{
    void update(GameData data);
    void startUI();

    String getUsername();
    String selectPlayer();
    String selectBlock();
    void logged();
    void gameIsStarting();
    void gameStarted();
    void showTimerCountdown(int remainSeconds);
    void youAreFirstPlayer();
    void firstPlayerIs(String firstPlayerUsername);
    void connectionRefused();

    String choose(Bundle<String, ArrayList<String>> options);
    String chooseSpawnPoint(Card option1, Card option2);
}
