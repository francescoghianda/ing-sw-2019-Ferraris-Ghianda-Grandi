package it.polimi.se2019.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WaitScene extends GridPane implements Initializable
{
    @FXML
    private Label label;

    @FXML
    private Label timerLabel;

    @FXML
    private ImageView imageView;

    private static final String STR1 = "In attesa di giocatori...";
    private static final String STR2 = "La partita inizia tra";

    WaitScene()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WaitScene.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        load(loader);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        getStylesheets().add("css/StartMenuStyle.css");
        label.setText(STR1);
        label.setStyle("-fx-text-fill: white;"+"-fx-font-size: 45;");
        timerLabel.setStyle("-fx-text-fill: white;"+"-fx-font-size: 100;");
        Image image= new Image("/img/loading.gif");
        imageView.setImage(image);
    }

    boolean isTimerVisible()
    {
        return timerLabel.isVisible();
    }

    void showTimer(boolean visible)
    {
        if(visible)label.setText(STR2);
        else label.setText(STR1);
        timerLabel.setVisible(visible);
        imageView.setVisible(visible);
    }

    void updateTimer(int remainSeconds)
    {
        timerLabel.setText(String.valueOf(remainSeconds));
    }

    private void load(FXMLLoader loader)
    {
        try
        {
            loader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
