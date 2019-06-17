package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.utils.constants.GameColor;
import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.OverrunStyle;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
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
        setMinHeight(0);
        setMinWidth(0);

        fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(100));

        setStyle("-fx-background-color: "+color.getColor());
        getStylesheets().add("/css/ColoredButtonStyle.css");
        setEffect(new Bloom());

        setWrapText(true);
        setTextAlignment(TextAlignment.CENTER);
        setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);

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
