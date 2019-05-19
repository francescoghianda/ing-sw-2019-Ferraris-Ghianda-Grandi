package it.polimi.se2019.ui;

import it.polimi.se2019.network.message.Bundle;

import java.util.ArrayList;


/**
 * initializes the user interface
 */
public interface UI extends GameEventHandler
{
    void update();
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
    PowerUpCard chooseSpawnPoint(PowerUpCard option1, PowerUpCard option2);


}
