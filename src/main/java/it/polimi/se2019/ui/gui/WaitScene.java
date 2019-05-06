package it.polimi.se2019.ui.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class WaitScene extends Scene
{

    private static final String STR1 = "In attesa di giocatori...";
    private static final String STR2 = "La partita inizia tra";

    private Label label;
    private Label timerLabel;
    private ImageView imageView;

    public WaitScene()
    {
        super(new GridPane(), 600, 403);
        getStylesheets().add("css/StartMenuStyle.css");

        GridPane layout = (GridPane)getRoot();

        layout.setAlignment(Pos.CENTER);

        label = new Label(STR1);
        label.setStyle("-fx-text-fill: white;"+"-fx-font-size: 45;");
        label.getStyleClass().add("outline");

        timerLabel = new Label("10");
        timerLabel.setStyle("-fx-text-fill: white;"+"-fx-font-size: 100;");
        timerLabel.getStyleClass().add("outline");
        timerLabel.setVisible(false);

        imageView = new ImageView("/img/loading.gif");
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(150);
        imageView.setVisible(false);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(timerLabel, imageView);
        HBox hBox = new HBox(stackPane);
        hBox.setAlignment(Pos.CENTER);

        layout.addRow(0, label);
        layout.addRow(2, stackPane);
    }

    public boolean isTimerVisible()
    {
        return timerLabel.isVisible();
    }

    public void showTimer(boolean visible)
    {
        if(visible)label.setText(STR2);
        else label.setText(STR1);
        timerLabel.setVisible(visible);
        imageView.setVisible(visible);
    }

    public void updateTimer(int remainSeconds)
    {
        timerLabel.setText(String.valueOf(remainSeconds));
    }
}
