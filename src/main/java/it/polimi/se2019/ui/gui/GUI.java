package it.polimi.se2019.ui.gui;

import it.polimi.se2019.player.Player;
import it.polimi.se2019.ui.UI;
import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GUI extends Application implements UI
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

}
