package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.utils.constants.GameColor;
import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class ColoredButton extends Button implements EventHandler<MouseEvent>
{
    private GameColor color;
    private FadeTransition fadeTransition;

    public ColoredButton()
    {
        super();
        this.color = GameColor.WHITE;
        init();
    }

    public ColoredButton(String text, GameColor color)
    {
        super(text);
        this.color = color;
        init();
    }

    public void setColor(GameColor color)
    {
        this.color = color;
        setStyle("-fx-background-color: "+color.getColor());
    }

    private void init()
    {
        fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(100));

        setStyle("-fx-background-color: "+color.getColor());
        getStylesheets().add("/css/ColoredButtonStyle.css");
        setEffect(new Bloom());

        setOnMousePressed(this);
        setOnMouseReleased(this);
    }

    @Override
    public void handle(MouseEvent event)
    {
        if(event.getEventType() == MouseEvent.MOUSE_PRESSED)
        {
            fadeTransition.setNode(this);
            fadeTransition.setToValue(0.5);
            fadeTransition.playFromStart();
        }
        else if(event.getEventType() == MouseEvent.MOUSE_RELEASED)
        {
            fadeTransition.setNode(this);
            fadeTransition.setToValue(1);
            fadeTransition.playFromStart();
        }
    }
}
