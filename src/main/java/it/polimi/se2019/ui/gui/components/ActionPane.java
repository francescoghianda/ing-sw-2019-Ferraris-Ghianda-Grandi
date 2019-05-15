package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.utils.constants.GameColor;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.TilePane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ActionPane extends TilePane implements Initializable, EventHandler<MouseEvent>
{
    @FXML
    private Button moveButton;

    @FXML
    private Button fireButton;

    @FXML
    private Button grabButton;

    @FXML
    private Button reloadButton;

    private FadeTransition transition;


    public ActionPane()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/actionPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        load(fxmlLoader);
    }

    private void load(FXMLLoader fxmlLoader)
    {
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setColor(GameColor color)
    {
        String colorStr;

        switch (color)
        {
            case BLUE:
                colorStr = "#006466";
                break;
            case YELLOW:
                colorStr = "gold";
                break;
            case PURPLE:
                colorStr = "purple";
                break;
            case GREEN:
                colorStr = "#38471f";
                break;
            default:
                colorStr = "#666666";
        }

        moveButton.setStyle("-fx-background-color: "+colorStr);
        fireButton.setStyle("-fx-background-color: "+colorStr);
        grabButton.setStyle("-fx-background-color: "+colorStr);
        reloadButton.setStyle("-fx-background-color: "+colorStr);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        String stylePath = "/css/ActionButtonStyle.css";
        moveButton.getStylesheets().add(stylePath);
        fireButton.getStylesheets().add(stylePath);
        grabButton.getStylesheets().add(stylePath);
        reloadButton.getStylesheets().add(stylePath);

        ImageView moveImageView = new ImageView(new Image(getClass().getResourceAsStream("/img/action_button/move.png")));
        moveImageView.setPreserveRatio(true);
        moveImageView.setFitHeight(40);

        ImageView fireImageView = new ImageView(new Image(getClass().getResourceAsStream("/img/action_button/fire.png")));
        fireImageView.setPreserveRatio(true);
        fireImageView.setFitHeight(40);

        ImageView grabImageView = new ImageView(new Image(getClass().getResourceAsStream("/img/action_button/grab.png")));
        grabImageView.setPreserveRatio(true);
        grabImageView.setFitHeight(40);

        ImageView reloadImageView = new ImageView(new Image(getClass().getResourceAsStream("/img/action_button/reload.png")));
        reloadImageView.setPreserveRatio(true);
        reloadImageView.setFitHeight(40);

        moveButton.setGraphic(moveImageView);
        fireButton.setGraphic(fireImageView);
        grabButton.setGraphic(grabImageView);
        reloadButton.setGraphic(reloadImageView);

        moveButton.setEffect(new Bloom());
        fireButton.setEffect(new Bloom());
        grabButton.setEffect(new Bloom());
        reloadButton.setEffect(new Bloom());


        transition = new FadeTransition();
        transition.setDuration(Duration.millis(100));


        moveButton.setOnMousePressed(this);
        moveButton.setOnMouseReleased(this);
        fireButton.setOnMousePressed(this);
        fireButton.setOnMouseReleased(this);
        grabButton.setOnMousePressed(this);
        grabButton.setOnMouseReleased(this);
        reloadButton.setOnMousePressed(this);
        reloadButton.setOnMouseReleased(this);

    }

    @Override
    public void handle(MouseEvent event)
    {
        if(event.getEventType() == MouseEvent.MOUSE_PRESSED)
        {
            transition.setNode((Node)event.getSource());
            transition.setToValue(0.5);
            transition.playFromStart();
        }
        else if(event.getEventType() == MouseEvent.MOUSE_RELEASED)
        {
            transition.setNode((Node)event.getSource());
            transition.setToValue(1);
            transition.playFromStart();
        }
    }
}
