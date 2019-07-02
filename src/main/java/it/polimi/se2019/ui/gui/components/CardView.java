package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.CardData;
import it.polimi.se2019.ui.gui.GUI;
import it.polimi.se2019.ui.gui.MatchScene;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class CardView extends AnchorPane implements Initializable, EventHandler<MouseEvent>
{
    public static final int WEAPON_CARD = 0;
    public static final int POWER_UP_CARD = 1;

    public static final int FADE_TRANSITION = 0;
    public static final int SCALE_TRANSITION = 1;

    @FXML
    private ImageView imageView;

    private final CardData card;
    private ScaleTransition scaleTransition;
    private FadeTransition fadeTransition;
    private final int cardType;
    private Image image;
    private OnCardViewClickListener clickListener;

    private final Tooltip tooltip;

    private int transition;

    private boolean enabled;

    private boolean selected;
    private boolean selectable;
    private DropShadow selectedEffect;
    private ColorAdjust grayScaleEffects;

    public CardView(CardData card, int transition)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/card.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        this.card = card;

        String idTypePart = card.getId().substring(0, 3);
        String cardId = card.getIdIgnoreClone().substring(3);

        this.transition = transition;
        this.enabled = card.isEnabled();

        switch (idTypePart)
        {
            case "WPC":
                cardType = WEAPON_CARD;
                image = new Image(getClass().getResourceAsStream("/img/weapons/AD_weapons_IT_02"+cardId+".png"));
                break;
            case "PUC":
                cardType = POWER_UP_CARD;
                image = new Image(getClass().getResourceAsStream("/img/powerups/AD_powerups_IT_02"+cardId+".png"));
                break;
            default:
                throw new InvalidCardIdException(card.getId());
        }

        //Tooltip
        BorderPane tooltipPane = new BorderPane();
        ImageView tooltipCardImageView = new ImageView(image);
        tooltipCardImageView.setPreserveRatio(true);
        tooltipCardImageView.setFitHeight(GUI.getScreenHeight()*0.2);
        tooltipPane.setLeft(tooltipCardImageView);

        BorderPane rightPane = new BorderPane();

        Label titleLabel = new Label(card.getName());
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setFont(new Font(GUI.getScreenHeight()*0.02));
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        Label descriptionLabel = new Label(card.getDescription());
        descriptionLabel.setFont(new Font(GUI.getScreenHeight()*0.01));
        descriptionLabel.setPadding(new Insets(5, 5, 0, 5));
        BorderPane.setAlignment(descriptionLabel, Pos.TOP_LEFT);

        rightPane.setTop(titleLabel);
        rightPane.setCenter(descriptionLabel);
        tooltipPane.setRight(rightPane);

        tooltip = new Tooltip();
        tooltip.setGraphic(tooltipPane);
        //

        selectedEffect = new DropShadow();
        selectedEffect.setColor(Color.GREEN);
        selectedEffect.setHeight(40);
        selectedEffect.setBlurType(BlurType.GAUSSIAN);

        grayScaleEffects = new ColorAdjust();
        grayScaleEffects.setSaturation(-1);

        load(fxmlLoader);
    }


    private void applyEffects()
    {
        if(enabled && !selected)imageView.setEffect(null);
        else if(enabled)imageView.setEffect(selectedEffect);
        else if(!selected)
        {
            grayScaleEffects.setInput(null);
            imageView.setEffect(grayScaleEffects);
        }
        if(!enabled && selected)
        {
            grayScaleEffects.setInput(selectedEffect);
            imageView.setEffect(grayScaleEffects);
        }
    }

    public void setSelectable(boolean selectable)
    {
        this.selectable = selectable;
    }

    public void setSelected(boolean selected)
    {
        if(selectable)this.selected = selected;
        applyEffects();
    }

    public boolean isSelected()
    {
        return selected;
    }

    public String getCardId()
    {
        return card.getId();
    }

    public int getCardType()
    {
        return this.cardType;
    }

    public void setTransition(int transition)
    {
        this.transition = transition;
    }

    public void setOnCardViewClickListener(OnCardViewClickListener listener)
    {
        this.clickListener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        scaleTransition = new ScaleTransition();
        fadeTransition = new FadeTransition();

        if(transition == FADE_TRANSITION)setOpacity(0.7);

        setMinHeight(0);
        setMinWidth(0);

        imageView.fitWidthProperty().bind(maxWidthProperty());
        maxHeightProperty().bind(imageView.fitHeightProperty());

        imageView.setPreserveRatio(true);
        imageView.setImage(image);

        applyEffects();

        scaleTransition.setDuration(Duration.millis(100));
        scaleTransition.setInterpolator(Interpolator.EASE_BOTH);
        scaleTransition.setNode(this);

        fadeTransition.setDuration(Duration.millis(100));
        fadeTransition.setNode(this);

        Tooltip.install(this, tooltip);

        scaleTransition.setAutoReverse(true);
        setOnMouseEntered(this);
        setOnMouseExited(this);
        setOnMouseClicked(this);
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        applyEffects();
    }

    public CardData getCard()
    {
        return this.card;
    }

    @Override
    public void handle(MouseEvent event)
    {
        if(transition == SCALE_TRANSITION)playScaleTransition(event);
        else playFadeTransition(event);

        if(event.getEventType().equals(MouseEvent.MOUSE_CLICKED))
        {
            setSelected(!isSelected());
            if(clickListener != null && enabled)clickListener.onCardClick(this);
        }
    }

    private void playScaleTransition(MouseEvent event)
    {
        if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED))
        {
            scaleTransition.setFromX(getScaleX());
            scaleTransition.setFromY(getScaleY());
            scaleTransition.setToX(1.2);
            scaleTransition.setToY(1.2);
            scaleTransition.playFromStart();
        }
        else if(event.getEventType().equals(MouseEvent.MOUSE_EXITED))
        {
            scaleTransition.setFromX(getScaleX());
            scaleTransition.setFromY(getScaleY());
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.playFromStart();
        }
    }

    private void playFadeTransition(MouseEvent event)
    {
        if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED))
        {
            fadeTransition.setFromValue(getOpacity());
            fadeTransition.setToValue(1);
            fadeTransition.playFromStart();
        }
        else if(event.getEventType().equals(MouseEvent.MOUSE_EXITED) && !selected)
        {
            fadeTransition.setFromValue(getOpacity());
            fadeTransition.setToValue(0.7);
            fadeTransition.playFromStart();
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

    public interface OnCardViewClickListener
    {
        void onCardClick(CardView cardView);
    }
}
