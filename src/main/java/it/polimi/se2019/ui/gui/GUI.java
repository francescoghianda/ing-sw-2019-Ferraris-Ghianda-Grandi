package it.polimi.se2019.ui.gui;

import it.polimi.se2019.map.Block;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.ui.gui.dialogs.CloseDialog;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;
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
        /////TEST
        //sceneManager.setScene(SceneManager.MATCH_SCENE);
        //window.centerOnScreen();
        ///////

    }

    @Override
    public void update()
    {

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
    public Player selectPlayer()
    {
        return null;
    }

    @Override
    public Block selectBlock()
    {
        return null;
    }

    @Override
    public void gameIsStarting()
    {
        SceneManager.runOnFxThread(()->
        {
            if(!SceneManager.WAIT_SCENE.isTimerVisible())SceneManager.WAIT_SCENE.showTimer(true);
        });
    }

    private void maximizeStage()
    {
        window.setResizable(true);
        window.setMaximized(true);
        //window.setResizable(false);
    }

    @Override
    public void gameStarted()
    {

        sceneManager.setScene(SceneManager.MATCH_SCENE);

        SceneManager.runOnFxThread(() ->
        {
            window.centerOnScreen();
            maximizeStage();
        });
    }

    @Override
    public void showTimerCountdown(int remainSeconds)
    {
        SceneManager.runOnFxThread(()-> SceneManager.WAIT_SCENE.updateTimer(remainSeconds));
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
        SceneManager.START_MENU_SCENE.connectionRefused();
    }

    @Override
    public String choose(Bundle<String, ArrayList<String>> options)
    {
        return null;
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
        /*ButtonType yesButton = new ButtonType("Si", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        Dialog<ButtonType> closeDialog = new Dialog<>();
        closeDialog.setContentText("Sei sicuro di voler uscire?");
        closeDialog.getDialogPane().getStylesheets().add("css/CloseDialogStyle.css");
        String bgUrl = "img/close_dialog_bg/close_dialog_bg_"+ (new Random().nextInt(5) + 1)+".png";
        closeDialog.getDialogPane().setBackground(new Background(new BackgroundImage(new Image(bgUrl), null, null, BackgroundPosition.CENTER, new BackgroundSize(300, 300, false, false, true, true))));
        closeDialog.getDialogPane().setMinSize(300, 300);
        closeDialog.getDialogPane().getButtonTypes().addAll(yesButton, noButton);
        Optional<ButtonType> result = closeDialog.showAndWait();
        if(!(result.isPresent() && result.get().equals(yesButton)))event.consume();*/

        CloseDialog closeDialog = new CloseDialog(window);
        Optional<ButtonType> result = closeDialog.showAndWait();
        if(!(result.isPresent() && result.get().equals(ButtonType.YES)))event.consume();


    }
}
