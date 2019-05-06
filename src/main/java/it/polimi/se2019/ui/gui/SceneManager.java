package it.polimi.se2019.ui.gui;

import it.polimi.se2019.ui.NetworkInterface;
import it.polimi.se2019.ui.UI;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager
{
    public static final LoginScene LOGIN_SCENE = new LoginScene();
    public static final StartMenuScene START_MENU_SCENE = new StartMenuScene();
    public static final WaitScene WAIT_SCENE = new WaitScene();
    public static final MatchScene MATCH_SCENE = new MatchScene();

    private static SceneManager instance;
    private final Stage stage;

    private NetworkInterface client;

    private SceneManager(Stage stage, UI ui)
    {
        this.stage = stage;
        this.client = new NetworkInterface(ui);
    }

    static SceneManager createSceneManager(Stage stage, UI ui)
    {
        instance = new SceneManager(stage, ui);
        return instance;
    }

    public static SceneManager getInstance()
    {
        return instance;
    }

    void setScene(Scene scene)
    {
        if(!Platform.isFxApplicationThread())Platform.runLater(() -> stage.setScene(scene));
        else stage.setScene(scene);
    }

    Scene getScene()
    {
        return stage.getScene();
    }

    public void connect(String ip, int port, int mode)
    {
        Thread thread = new Thread(() -> client.connect(ip, port, mode));
        thread.start();
    }

    public String getUsername()
    {
        if(getScene().equals(LOGIN_SCENE)) runOnFxThread(LOGIN_SCENE::invalidUsername);
        else setScene(LOGIN_SCENE.reset());

        return new Input<>(LOGIN_SCENE).getInput(LoginScene.INPUT_USERNAME);
    }

    public static void runOnFxThread(Runnable runnable)
    {
        if(!Platform.isFxApplicationThread())Platform.runLater(runnable);
        else runnable.run();
    }

    public double getWidth()
    {
        return stage.getWidth();
    }

    public double getHeight()
    {
        return stage.getHeight();
    }


}
