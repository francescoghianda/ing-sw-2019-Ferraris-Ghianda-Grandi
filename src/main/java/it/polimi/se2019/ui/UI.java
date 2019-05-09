package it.polimi.se2019.ui;

import it.polimi.se2019.map.Block;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Player;

import java.util.ArrayList;


/**
 * initializes the user interface
 */
public interface UI
{
    void update();
    void startUI();

    String getUsername();
    Player selectPlayer();
    Block selectBlock();
    void logged();
    void gameIsStarting();
    void gameStarted();
    void showTimerCountdown(int remainSeconds);
    void youAreFirstPlayer();
    void firstPlayerIs(String firstPlayerUsername);
    void connectionRefused();
    String choose(Bundle<String, ArrayList<String>> options);
}
