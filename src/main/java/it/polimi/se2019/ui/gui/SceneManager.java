package it.polimi.se2019.ui.gui;

import it.polimi.se2019.ui.NetworkInterface;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.ui.gui.value.ValueObserver;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager
{

    public static final int LOGIN_SCENE = 1;
    public static final int START_MENU_SCENE = 2;
    public static final int WAIT_SCENE = 3;
    public static final int MATCH_SCENE = 4;


    public static LoginScene loginScene;
    public static StartMenuScene startMenuScene;
    public static WaitScene waitScene;
    public static Scene matchScene;

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
        initScene();
        return instance;
    }

    public Stage getStage()
    {
        return this.stage;
    }

    private static void initScene()
    {
        loginScene = new LoginScene();
        startMenuScene = new StartMenuScene();
        waitScene = new WaitScene();
        matchScene = new Scene(MatchScene.getInstance());
    }

    public MatchScene getMatchScene()
    {
        return (MatchScene)matchScene.getRoot();
    }

    public static SceneManager getInstance()
    {
        return instance;
    }

    void setScene(int sceneNumber)
    {
        setScene(getScene(sceneNumber));
    }

    private void setScene(Scene scene)
    {
        if(!Platform.isFxApplicationThread())Platform.runLater(() -> select(scene));
        else select(scene);
    }

    public static Scene getScene(int scene)
    {
        Scene selected = null;

        switch (scene)
        {
            case LOGIN_SCENE:
                selected = loginScene;
                break;
            case START_MENU_SCENE:
                selected = startMenuScene;
                break;
            case WAIT_SCENE:
                selected = waitScene;
                break;
            case MATCH_SCENE:
                selected = matchScene;
                break;
        }

        return selected;
    }

    private void select(Scene scene)
    {
        stage.setScene(scene);
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
        if(getScene().equals(loginScene)) runOnFxThread(loginScene::invalidUsername);
        else setScene(loginScene.reset());

        return new ValueObserver<String>().get(LoginScene.username);
    }

    public static void runOnFxThread(Runnable runnable)
    {
        if(!Platform.isFxApplicationThread())
        {
            Platform.runLater(runnable);
        }
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
