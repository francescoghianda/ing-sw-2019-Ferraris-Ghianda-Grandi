package it.polimi.se2019.ui.gui;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Action;
import it.polimi.se2019.ui.GameEvent;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.ui.gui.dialogs.CloseDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.omg.CORBA.MARSHAL;

import java.util.ArrayList;
import java.util.Optional;

public class GUI extends Application implements UI, EventHandler<WindowEvent>
{
    private static final String TITLE = "Adrenalina";

    private static Stage window;

    private SceneManager sceneManager;

    public GUI()
    {

    }

    @Override
    public void init()
    {

    }

    @Override
    public void start(Stage primaryStage)
    {
        window = new Stage();

        sceneManager = SceneManager.createSceneManager(window, this);

        sceneManager.setScene(SceneManager.START_MENU_SCENE);

        window.setTitle(TITLE);
        window.setResizable(false);
        window.setOnCloseRequest(this);
        window.show();
        window.centerOnScreen();


        //gameStarted();

    }

    @Override
    public void update(GameData data)
    {
        SceneManager.runOnFxThread(() -> sceneManager.getMatchScene().update(data));
    }

    @Override
    public void startUI()
    {
        Thread thread = new Thread(Application::launch);
        thread.start();
    }

    @Override
    public String getUsername()
    {
        return sceneManager.getUsername();
    }

    @Override
    public void logged()
    {
        sceneManager.setScene(SceneManager.WAIT_SCENE);
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
    public void gameIsStarting()
    {
        SceneManager.runOnFxThread(()->
        {
            if(!SceneManager.waitScene.isTimerVisible())SceneManager.waitScene.showTimer(true);
        });
    }

    @Override
    public void gameStarted()
    {
        sceneManager.setScene(SceneManager.MATCH_SCENE);
        SceneManager.runOnFxThread(window::centerOnScreen);
    }

    @Override
    public void showTimerCountdown(int remainSeconds)
    {
        SceneManager.runOnFxThread(()-> SceneManager.waitScene.updateTimer(remainSeconds));
    }

    @Override
    public void youAreFirstPlayer()
    {

    }

    @Override
    public void firstPlayerIs(String firstPlayerUsername)
    {

    }

    @Override
    public void connectionRefused()
    {
        SceneManager.startMenuScene.connectionRefused();
    }

    @Override
    public boolean notEnoughAmmo(boolean askToSellPowerUp)
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(()->
        {

            if(askToSellPowerUp)
            {
                matchScene.setOptions("Non hai abbastanza munizioni!\nVuoi vendere un potenziamento?", "Si", "No");
            }
            else
            {
                matchScene.setOptions("Non hai abbastanza munizioni!");
            }
        });

        if(askToSellPowerUp)
        {
            String option = new ValueObserver<String>().getValue(matchScene.selectedOption);
            return option.equals("Si");
        }

        return false;
    }

    @Override
    public String choose(Bundle<String, ArrayList<String>> options)
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(() -> matchScene.setOptions(options.getFirst(), options.getSecond()));
        return new ValueObserver<String>().getValue(matchScene.selectedOption);
    }

    @Override
    public String chooseSpawnPoint(Card option1, Card option2)
    {
        SceneManager.runOnFxThread(() ->
        {
            MatchScene.getInstance().addCard(option1);
            MatchScene.getInstance().addCard(option2);
            MatchScene.getInstance().setOptions("Seleziona il potenziamento che vuoi scartare");
        });
        return new ValueObserver<String>().getValue(sceneManager.getMatchScene().selectedPowerUpId);
    }

    @Override
    public Action chooseActionFrom(Action[] possibleActions)
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(() ->
        {
            matchScene.setOptions("È il tuo turno");
            matchScene.enableActions(possibleActions);
        });
        return new ValueObserver<Action>().getValue(matchScene.selectedAction);
    }

    @Override
    public Coordinates chooseBlock(int maxDistance)
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(() -> matchScene.chooseBlock(maxDistance));
        return new ValueObserver<Coordinates>().getValue(matchScene.selectedBlock);
    }

    @Override
    public Card chooseWeaponFromPlayer()
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(matchScene::chooseWeaponFromPlayer);
        return new ValueObserver<Card>().getValue(matchScene.selectedWeapon);
    }

    @Override
    public Card chooseWeaponFromBlock()
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(matchScene::chooseWeaponFromBlock);
        return new ValueObserver<Card>().getValue(matchScene.selectedWeapon);
    }

    @Override
    public Card choosePowerUp()
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(matchScene::choosePowerUp);
        return new ValueObserver<Card>().getValue(matchScene.selectedPowerUp);
    }


    public static double getStageWidth()
    {
        return window.getWidth();
    }

    public static double getStageHeight()
    {
        return window.getHeight();
    }

    public static double getScreenWidth()
    {
        return Screen.getPrimary().getVisualBounds().getWidth();
    }

    public static double getScreenHeight()
    {
        return Screen.getPrimary().getVisualBounds().getHeight();
    }

    @Override
    public void handle(WindowEvent event)
    {
        CloseDialog closeDialog = new CloseDialog(window);
        Optional<ButtonType> result = closeDialog.showAndWait();
        if(!(result.isPresent() && result.get().equals(ButtonType.YES)))
        {
            event.consume();
        }
        else
        {
            Platform.exit();
            System.exit(0);
        }
    }

    @Override
    public void handle(GameEvent event)
    {

    }
}
