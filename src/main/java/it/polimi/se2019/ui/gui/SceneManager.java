package it.polimi.se2019.ui.gui;

import it.polimi.se2019.ui.NetworkInterface;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.ui.gui.value.ValueObserver;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager
{

    private SimpleObjectProperty<Scene> onSceneSelectedProperty;

    static final int LOGIN_SCENE = 1;
    static final int START_MENU_SCENE = 2;
    static final int WAIT_SCENE = 3;
    static final int MATCH_SCENE = 4;
    static final int SCORE_BOARD_SCENE = 5;

    private static Scene loginScene;
    private static Scene startMenuScene;
    private static Scene waitScene;
    private static Scene matchScene;
    private static Scene scoreBoardScene;

    private static SceneManager instance;
    private final Stage stage;

    private NetworkInterface client;

    private SceneManager(Stage stage, UI ui)
    {
        this.stage = stage;
        this.client = new NetworkInterface(ui);
        this.onSceneSelectedProperty = new SimpleObjectProperty<>();
    }

    static SceneManager createSceneManager(Stage stage, UI ui)
    {
        instance = new SceneManager(stage, ui);
        initScene();
        return instance;
    }

    private static void initScene()
    {
        loginScene = new Scene(new LoginScene());
        startMenuScene = new Scene(new StartMenuScene());
        waitScene = new Scene(new WaitScene());
        matchScene = new Scene(MatchScene.getInstance());
        scoreBoardScene = new Scene(new ScoreBoardScene());
    }


    ReadOnlyObjectProperty<Scene> onSceneSelectedProperty()
    {
        return onSceneSelectedProperty;
    }

    MatchScene getMatchScene()
    {
        return (MatchScene)matchScene.getRoot();
    }

    ScoreBoardScene getScoreBoardScene()
    {
        return (ScoreBoardScene)scoreBoardScene.getRoot();
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

        onSceneSelectedProperty.set(scene);
    }

    Scene getCurrentScene()
    {
        return onSceneSelectedProperty.getValue();
    }

    static Scene getScene(int scene)
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
            case SCORE_BOARD_SCENE:
                selected = scoreBoardScene;
                break;
        }

        return selected;
    }

    static Parent getSceneRoot(int scene)
    {
        return getScene(scene).getRoot();
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
        if(getScene().equals(loginScene)) runOnFxThread(((LoginScene)loginScene.getRoot())::invalidUsername);
        else
        {
            ((LoginScene)loginScene.getRoot()).reset();
            setScene(loginScene);
        }

        return new ValueObserver<String>().get(LoginScene.username);
    }

    static void runOnFxThread(Runnable runnable)
    {
        if(!Platform.isFxApplicationThread())
        {
            Platform.runLater(runnable);
        }
        else runnable.run();
    }
}
