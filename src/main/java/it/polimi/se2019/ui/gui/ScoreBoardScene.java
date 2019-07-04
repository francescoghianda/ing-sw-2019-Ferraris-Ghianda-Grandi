package it.polimi.se2019.ui.gui;

import it.polimi.se2019.player.PlayerData;
import it.polimi.se2019.ui.gui.components.ColoredButton;
import it.polimi.se2019.ui.gui.components.ScoreBoardList;
import it.polimi.se2019.ui.gui.value.ObservableValue;
import it.polimi.se2019.ui.gui.value.Value;
import it.polimi.se2019.utils.constants.GameColor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ScoreBoardScene extends BorderPane implements Initializable
{
    @FXML
    private Label titleLabel;

    @FXML
    private ListView listView;

    @FXML
    private TilePane buttonsTilePane;

    @FXML
    private ColoredButton yesButton;

    @FXML
    private ColoredButton noButton;

    @FXML
    private ScoreBoardList scoreBoardList;

    public final Value<Boolean> playAgainValue;


    public ScoreBoardScene()
    {

        playAgainValue = new Value<>();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScoreBoardScene.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        load(loader);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Image backgroundImage = new Image(getClass().getResourceAsStream("/img/texture2.png"));
        setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        scoreBoardList.maxWidthProperty().bind(widthProperty().multiply(0.33));
        scoreBoardList.maxHeightProperty().bind(heightProperty().multiply(0.5));

        setAlignment(scoreBoardList, Pos.TOP_CENTER);
        //scoreBoardList.setMaxWidth(Double.MAX_VALUE);
        //scoreBoardList.setMaxHeight(Double.MAX_VALUE);

        yesButton.setColor(GameColor.GREEN);
        yesButton.setFont(new Font(25));
        noButton.setColor(GameColor.RED);
        noButton.setFont(new Font(25));

        yesButton.setOnAction(this::buttonsAction);
        noButton.setOnAction(this::buttonsAction);

        titleLabel.setFont(new Font(70));

        titleLabel.minHeightProperty().bind(heightProperty().multiply(0.1));
        buttonsTilePane.minHeightProperty().bind(heightProperty().multiply(0.1));

    }

    public void show(List<PlayerData> scoreBoard, boolean win)
    {
        scoreBoardList.setScoreBoard(scoreBoard);

        if(win)
        {
            titleLabel.setTextFill(Color.DARKGREEN);
            titleLabel.setText("Hai Vinto!");
        }
        else
        {
            titleLabel.setTextFill(Color.DARKRED);
            titleLabel.setText("Hai perso");
        }
    }

    private void buttonsAction(ActionEvent event)
    {
        if(event.getSource().equals(yesButton))
        {
            playAgainValue.set(true);
        }
        else if(event.getSource().equals(noButton))
        {
            playAgainValue.set(false);
        }
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
