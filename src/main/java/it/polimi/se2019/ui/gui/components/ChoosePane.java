package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.map.BlockData;
import it.polimi.se2019.player.PlayerData;
import it.polimi.se2019.ui.gui.GUI;
import it.polimi.se2019.ui.gui.MatchScene;
import it.polimi.se2019.utils.constants.GameColor;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ChoosePane extends AnchorPane implements Initializable
{

    @FXML
    private Label label;

    @FXML
    private TilePane tilePane;

    private GameColor color;
    private OnOptionChosenListener listener;
    private FadeTransition transition;

    public ChoosePane()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/choosePane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        color = GameColor.WHITE;
        transition = new FadeTransition();

        load(fxmlLoader);
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

    public void setOnOptionChosenListener(OnOptionChosenListener listener)
    {
        this.listener = listener;
    }

    public void setText(String text)
    {
        label.setText(text);

        List<PlayerData> players = MatchScene.getInstance().getGameDataProperty().get().getPlayers();

        for(PlayerData player : players)
        {
            if(player.getUsername().equals(text))
            {
                setColor(player.getColor());
                break;
            }
        }
    }

    public void addOption(String option)
    {
        ColoredButton optionButton = new ColoredButton(option, GameColor.WHITE);

        optionButton.setOnAction(this::onOptionClicked);
        optionButton.setMinHeight(GUI.getScreenHeight()/20);
        optionButton.setMinWidth(GUI.getScreenWidth()/24);

        //optionButton.setMinHeight(GUI.getMinStageHeight()/20);
        //optionButton.setMinWidth(GUI.getMinStageWidth()/24);

        tilePane.getChildren().add(optionButton);
    }

    public void setColor(GameColor color)
    {
        this.color = color;
        Color backgroundColor = Color.web(color.getColor(), 0.1);
        setBackground(new Background(new BackgroundFill(backgroundColor, null, null)));
        tilePane.getChildren().forEach(node -> ((ColoredButton)node).setColor(color));
    }

    public void clear()
    {
        label.setText("");
        tilePane.getChildren().clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setPrefHeight(GUI.getScreenHeight()/5);
        setMaxWidth(GUI.getScreenWidth()/4);
        setMinWidth(GUI.getScreenWidth()/4);

        //setPrefHeight(GUI.getMinStageHeight()/5);
        //setMaxWidth(GUI.getMinStageWidth()/4);
        //setMinWidth(GUI.getMinStageWidth()/4);

        label.setStyle("-fx-text-fill: white");

        setBackground(new Background(new BackgroundFill(new Color(1, 1, 1, 0.1), null, null)));

        label.maxHeightProperty().bind(heightProperty().multiply(0.25));
        tilePane.maxHeightProperty().bind(heightProperty().multiply(0.75));
    }

    public void onOptionClicked(ActionEvent event)
    {
        if(listener != null)listener.onOptionChosen(((Button)event.getSource()).getText());
    }

    public interface OnOptionChosenListener
    {
        void onOptionChosen(String chosenOption);
    }
}
