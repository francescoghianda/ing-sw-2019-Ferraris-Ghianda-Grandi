package it.polimi.se2019.ui.gui;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class MatchScene extends Scene
{

    public MatchScene()
    {
        super(new BorderPane(), GUI.getScreenWidth(), GUI.getScreenHeight());

        getRoot().setStyle("-fx-background-color: magenta");

    }
}
