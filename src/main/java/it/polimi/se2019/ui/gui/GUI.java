package it.polimi.se2019.ui.gui;

import it.polimi.se2019.player.Player;
import it.polimi.se2019.ui.UI;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GUI extends Application implements UI
{
    private static final String TITLE = "Adrenalina";

    private Stage window;

    public GUI()
    {

    }

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        window = new Stage();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        StartMenuScene menuScene = new StartMenuScene((int)screenBounds.getWidth(), (int)screenBounds.getHeight());

        window.setTitle(TITLE);
        window.setResizable(false);
        window.setScene(menuScene);
        window.show();
        window.centerOnScreen();
    }

    @Override
    public void update()
    {

    }

    @Override
    public void init()
    {

    }

    @Override
    public String getUsername()
    {
        return null;
    }

    @Override
    public void logged()
    {

    }

    @Override
    public Player selectPlayer()
    {
        return null;
    }

    @Override
    public void gameIsStarting()
    {

    }

    @Override
    public void showTimerCountdown(int remainSeconds)
    {

    }

    @Override
    public void youAreFirstPlayer()
    {

    }

    @Override
    public void firstPlayerIs(String firstPlayerUsername)
    {

    }

}
