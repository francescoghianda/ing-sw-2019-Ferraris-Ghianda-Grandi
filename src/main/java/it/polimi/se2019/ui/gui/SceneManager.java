package it.polimi.se2019.ui.gui;

import it.polimi.se2019.network.NetworkClient;
import it.polimi.se2019.ui.NetworkInterface;
import it.polimi.se2019.ui.UI;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager
{
    private static SceneManager instance;
    private final Stage stage;

    private NetworkInterface client;

    private SceneManager(Stage stage, UI ui)
    {
        this.stage = stage;
        this.client = new NetworkInterface(ui);
    }

    public static SceneManager createSceneManager(Stage stage, UI ui)
    {
        instance = new SceneManager(stage, ui);
        return instance;
    }

    public static SceneManager getInstance()
    {
        return instance;
    }

    public void setScene(GameScene scene)
    {
        stage.setScene(scene.getScene());
    }

    public void connect(String ip, int port, int mode)
    {
        client.connect(ip, port, mode);
    }


}
