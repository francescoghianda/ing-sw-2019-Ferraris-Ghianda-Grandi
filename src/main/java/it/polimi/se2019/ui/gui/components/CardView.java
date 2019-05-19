package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.ui.gui.SceneManager;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class CardView extends AnchorPane implements Initializable, EventHandler<MouseEvent>
{
    public static final int WEAPON_CARD = 0;
    public static final int POWER_UP_CARD = 1;

    @FXML
    private ImageView imageView;

    private CardPane cardPane;
    private Card card;
    private ScaleTransition transition;
    private int cardType;
    private Image image;
    private OnCardViewClickListener clickListener;

    public CardView(Card card, CardPane cardPane)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/card.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        this.card = card;
        this.cardPane = cardPane;

        String idTypePart = card.getId().substring(0, 3);
        String cardId = card.getIdIgnoreClone().substring(3);

        switch (idTypePart)
        {
            case "WPC":
                cardType = WEAPON_CARD;
                image = new Image(getClass().getResourceAsStream("/img/weapons/AD_weapons_IT_02"+cardId+".png"));
                break;
            case "PUC":
                cardType = POWER_UP_CARD;
                System.out.println(cardId);
                image = new Image(getClass().getResourceAsStream("/img/powerups/AD_powerups_IT_02"+cardId+".png"));
                break;
            default:
                throw new InvalidCardIdException(card.getId());
        }

        load(fxmlLoader);
    }

    public String getCardId()
    {
        return card.getId();
    }

    public int getCardType()
    {
        return this.cardType;
    }

    public void setOnCardViewClickListener(OnCardViewClickListener listener)
    {
        this.clickListener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        transition = new ScaleTransition();

        setMinHeight(0);
        setMinWidth(0);

        imageView.fitWidthProperty().bind(widthProperty());

        maxHeightProperty().bind(imageView.fitHeightProperty());

        imageView.setPreserveRatio(true);
        imageView.setImage(image);
        transition.setDuration(Duration.millis(100));
        transition.setInterpolator(Interpolator.EASE_BOTH);
        transition.setNode(this);

        transition.setAutoReverse(true);
        setOnMouseEntered(this);
        setOnMouseExited(this);
        setOnMouseClicked(this);
    }

    public Card getCard()
    {
        return this.card;
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

            cardPane.setDescription(card.getDescription());
        }
        else if(event.getEventType().equals(MouseEvent.MOUSE_EXITED))
        {
            transition.setFromX(getScaleX());
            transition.setFromY(getScaleY());
            transition.setToX(1);
            transition.setToY(1);
            transition.playFromStart();

            cardPane.setDescription("");
        }
        else if(event.getEventType().equals(MouseEvent.MOUSE_CLICKED))
        {
            if(clickListener != null)clickListener.onCardClick(this);
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
