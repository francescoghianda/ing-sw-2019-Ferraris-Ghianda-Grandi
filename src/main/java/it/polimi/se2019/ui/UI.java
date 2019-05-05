package it.polimi.se2019.ui;

import it.polimi.se2019.player.Player;

/**
 * initializes the user interface
 */
public interface UI
{
    void update();
    void init();

    String getUsername();
    void logged();

    Player selectPlayer();

    void gameIsStarting();
    void showTimerCountdown(int remainSeconds);
    void youAreFirstPlayer();
    void firstPlayerIs(String firstPlayerUsername);
}
