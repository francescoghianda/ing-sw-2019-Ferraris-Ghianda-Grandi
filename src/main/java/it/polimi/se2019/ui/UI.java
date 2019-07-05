package it.polimi.se2019.ui;

import it.polimi.se2019.card.CardData;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Action;
import it.polimi.se2019.player.PlayerData;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Interface that defines all methods that can be called by the VirtualView on a client
 */
public interface UI extends GameEventHandler
{
    /**
     * Send an update
     * @param data all data of the game
     */
    void update(GameData data);

    /**
     * Start the UI
     */
    void startUI();

    /**
     *
     * @return the username for login
     */
    String login();

    /**
     * Request focus on a window when a requested in received from the server
     */
    void requestFocus();

    /**
     * Invoked when the login is correct
     */
    void logged();

    /**
     * Invoked when the match is starting (the timer is started)
     */
    void gameIsStarting();

    /**
     * Invoked when the match is started (when timer end)
     */
    void gameStarted();

    /**
     * Show the remain seconds to start the match
     * @param remainSeconds the second that remain
     */
    void showTimerCountdown(int remainSeconds);

    /**
     * Invoked if the client is selected to be the first player to start the game
     */
    void youAreFirstPlayer();

    /**
     * Invoked when the first player is selected (this method is not invoked on the first player client)
     * @param firstPlayerUsername the username of the player selected
     */
    void firstPlayerIs(String firstPlayerUsername);

    /**
     * Invoked when the client is not able to establish a connection with the server
     */
    void connectionRefused();

    /**
     * Invoked when the round of the client is started
     */
    void roundStart();

    /**
     * Invoked when the round of the client is ended
     */
    void roundEnd();

    /**
     * Invoked when the timer of a current request is timed out
     * This method will interrupt all current input
     * The exeption TimeOutException will be thrown
     */
    void timeout();

    /**
     * Invoked when the action that the player is trying to execute is impossible
     */
    void notifyImpossibleAction();

    /**
     * Show a notification message
     * @param text The text of the message
     */
    void showNotification(String text);

    /**
     * Close the connection with the server
     */
    void closeConnection();

    /**
     * Show the final score board at the and of the match
     * @param scoreBoard a list of a player ordered by the points
     * @return true if the player wants to play again
     */
    boolean showScoreBoardAndChooseIfPlayAgain(ArrayList<PlayerData> scoreBoard);

    /**
     * Inform the player that he not have enough ammo to complete the action
     * @param askToSellPowerUp if true, the method will ask the player if he wants to sell a power-up
     * @return if askToSellPowerUp is false return false otherwise return the answer of the player
     */
    boolean notEnoughAmmo(boolean askToSellPowerUp);

    /**
     * Ask a player to select a option or cancel the action
     * @param options Bundle contains in the first object the question, and in the second the list of possible option
     * @return the selected option
     * @throws CanceledActionException if the player choose to cancel the action
     */
    String chooseOrCancel(Bundle<String, ArrayList<String>> options) throws CanceledActionException;

    /**
     * Ask a player to select a option
     * @param options Bundle contains in the first object the question, and in the second the list of possible option
     * @return the selected option
     */
    String choose(Bundle<String, ArrayList<String>> options);

    /**
     * Ask a player to choose a power-up to discard
     * @param option1 the first power-up option
     * @param option2 the second power-up option
     * @return the chosen power-up
     */
    String chooseSpawnPoint(CardData option1, CardData option2);

    /**
     * Ask a player which action he wants to execute
     * @param possibleActions the list of all possible action for that player
     * @return the chosen action
     */
    Bundle<Action, Serializable> chooseActionFrom(Action[] possibleActions);

    /**
     * Ask a player to choose a block of the map
     * @param maxDistance the max distance that the block must be compared to the player
     * @return the chosen block
     * @throws CanceledActionException if the player choose to cancel the action
     */
    Coordinates chooseBlock(int maxDistance) throws CanceledActionException;

    /**
     * Ask a player to choose a block of the map
     * @param coordinates the list of the possible block that can be selected
     * @return the chosen block
     * @throws CanceledActionException if the player choose to cancel the action
     */
    Coordinates chooseBlockFrom(ArrayList<Coordinates> coordinates)throws CanceledActionException;

    /**
     * Ask a player to choose a weapon between those he currently has
     * @return the selected weapon
     * @throws CanceledActionException if the player choose to cancel the action
     */
    CardData chooseWeaponFromPlayer() throws CanceledActionException;

    /**
     * Ask a player to choose a weapon from a spawn-point block
     * @return the selected weapon
     * @throws CanceledActionException if the player choose to cancel the action
     */
    CardData chooseWeaponFromBlock() throws CanceledActionException;

    /**
     * Ask a player to choose a power-up between those he currently has
     * @return the selected weapon
     * @throws CanceledActionException if the player choose to cancel the action
     */
    CardData choosePowerUp() throws CanceledActionException;

    /**
     * Ask a player to choose the weapons he wants to reload
     * @param weapons the list of all weapons that the player can reload
     * @return the list of selected weapon or null if the player cancel the action
     */
    ArrayList<CardData> chooseWeaponsToReload(ArrayList<CardData> weapons);

    /**
     * Ask a player to choose the weapon he wants to reload
     * @param weapons the list of all weapons that the player can reload
     * @return the selected weapon or null if the player cancel the action
     */
    CardData chooseWeaponToReload(ArrayList<CardData> weapons);

    /**
     * Ask a player to choose the weapon he wants to swap between those he currently has
     * @return the selected weapon or null if the player cancel the action
     */
    CardData chooseWeaponToSwap() throws CanceledActionException;
}
