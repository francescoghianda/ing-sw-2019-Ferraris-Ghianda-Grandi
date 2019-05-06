package it.polimi.se2019.ui.gui;

import javafx.scene.Scene;

public enum GameScene
{
    START_MENU(new StartMenuScene());

    private final Scene scene;

    GameScene(Scene scene)
    {
        this.scene = scene;
    }

    public Scene getScene()
    {
        return this.scene;
    }
}
