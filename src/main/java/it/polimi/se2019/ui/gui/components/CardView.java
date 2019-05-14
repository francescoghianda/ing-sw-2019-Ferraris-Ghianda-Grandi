package it.polimi.se2019.ui.gui.components;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;


public class CardView extends BasicComponent
{

    @FXML
    private ImageView image;

    @FXML
    private Pane pane;

    private ScaleTransition transition;

    public CardView()
    {
        super("/fxml/card.fxml");
        load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        transition = new ScaleTransition();
        pane.setMaxSize(200, 220);
        image.setImage(new Image(getClass().getResourceAsStream("/img/weapons/AD_weapons_IT_022.png")));
        transition.setDuration(Duration.millis(100));
        transition.setInterpolator(Interpolator.EASE_BOTH);
        transition.setNode(pane);

        transition.setAutoReverse(true);
        pane.setOnMouseEntered(this);
        pane.setOnMouseExited(this);
    }

    @Override
    public void handle(MouseEvent event)
    {
        if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED))
        {
            transition.setFromX(getScaleX());
            transition.setFromY(getScaleY());
            transition.setToX(1.2);
            transition.setToY(1.2);
            transition.playFromStart();
        }
        else if(event.getEventType().equals(MouseEvent.MOUSE_EXITED))
        {
            transition.setFromX(getScaleX());
            transition.setFromY(getScaleY());
            transition.setToX(1);
            transition.setToY(1);
            transition.playFromStart();
        }
    }
}
