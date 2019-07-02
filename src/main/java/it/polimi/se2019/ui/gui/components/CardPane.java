package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.CardData;
import it.polimi.se2019.ui.gui.GUI;

import it.polimi.se2019.ui.gui.MatchScene;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import sun.awt.geom.AreaOp;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CardPane extends StackPane implements Initializable
{

    @FXML
    private HBox hBox;

    @FXML
    private Label titleLabel;

    @FXML
    private VBox vBox;

    @FXML
    private ImageView emptySlot1;

    @FXML
    private ImageView emptySlot2;

    @FXML
    private ImageView emptySlot3;

    @FXML
    private CardSlot cardSlot1;

    @FXML
    private CardSlot cardSlot2;

    @FXML
    private CardSlot cardSlot3;

    private CardSlot[] slots;

    private String title;
    private DoubleProperty fontSize;
    private String type;
    private int cardNumber;

    private boolean cardSelectable;
    private int cardViewTransition;

    public CardPane(@NamedArg("title") String title, @NamedArg("type") String type)
    {
        this.type = type;
        this.title = title;
        fontSize = new SimpleDoubleProperty();
        cardViewTransition = CardView.SCALE_TRANSITION;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/cardPane.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        load(fxmlLoader);
    }

    public void setCardViewTransition(int cardViewTransition)
    {
        this.cardViewTransition = cardViewTransition;
        for(CardSlot slot : slots)
        {
            slot.setCardViewTransition(cardViewTransition);
        }
    }

    public List<CardData> getSelectedCards()
    {
        List<CardData> selectedCards = new ArrayList<>();

        for(CardSlot slot : slots)
        {
            if(slot.isCardSelected())selectedCards.add(slot.getCard());
        }

        return selectedCards;
    }

    public void deselectAllCards()
    {
        for(CardSlot slot : slots)
        {
            slot.setCardSelected(false);
        }
    }

    public void enableAllCards()
    {
        for(CardSlot slot : slots)
        {
            slot.setCardEnabled(true);
        }
    }

    public void setCardSelectable(boolean cardSelectable)
    {
        for(CardSlot slot : slots)
        {
            slot.setCardSelectable(cardSelectable);
        }
        this.cardSelectable = cardSelectable;
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

    public void addCard(CardData card, CardView.OnCardViewClickListener listener)
    {
        cardNumber++;
        if(cardNumber > 3)throw new CardPaneOutOfBoundsException();

        CardSlot freeSlot = getFreeCardSlot();
        if(freeSlot != null)
        {
            freeSlot.setCard(card, cardViewTransition, getWidth(), listener);
            freeSlot.setCardSelectable(cardSelectable);
        }

    }

    public void updateCards(List<CardData> cards, CardView.OnCardViewClickListener listener)
    {
        for(CardSlot slot : slots)
        {
            removeIfNotInList(slot, cards);
        }

        cards.forEach(card ->
        {
            //System.out.println(card.getName()+" - "+card.isEnabled());

            if(!isCardPresent(card))addCard(card, listener);
            else findCardSlotByCard(card).setCardEnabled(card.isEnabled());
        });
    }

    private CardSlot findCardSlotByCard(CardData card)
    {
        for(CardSlot slot : slots)
        {
            if(slot.containsCard(card))return slot;
        }
        return null;
    }

    private void removeIfNotInList(CardSlot cardSlot, List<CardData> cards)
    {
        if(cardSlot.isEmpty())return;
        if(cards.contains(cardSlot.getCard()))return;

        cardNumber--;
        cardSlot.removeCard();
    }

    private boolean isCardPresent(CardData card)
    {
        return cardSlot1.containsCard(card) || cardSlot2.containsCard(card) || cardSlot3.containsCard(card);
    }

    private CardSlot getFreeCardSlot()
    {
        for(CardSlot slot : slots)
        {
            if(slot.isEmpty())return slot;
        }
        return null;
    }

    private void initSlots(Image image)
    {
        emptySlot1.setImage(image);
        emptySlot2.setImage(image);
        emptySlot3.setImage(image);

        double maxWidth = getWidth()/3.8;
        emptySlot1.setFitWidth(maxWidth);
        emptySlot2.setFitWidth(maxWidth);
        emptySlot3.setFitWidth(maxWidth);

        for(CardSlot slot : slots)
        {
            slot.init(getWidth(), type);
        }
    }

    public void setTitleSize(double size)
    {
        fontSize.unbind();
        fontSize.setValue(size);
    }

    public void setTitle(String title)
    {
        titleLabel.setText(title);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setMaxWidth(GUI.getScreenWidth()/4);
        setMinHeight(0);

        Image emptySlotImage;

        if ("weapon".equals(type)) emptySlotImage = new Image(getClass().getResourceAsStream("/img/weapon_slot.png"));
        else emptySlotImage = new Image(getClass().getResourceAsStream("/img/powerup_slot.png"));

        slots = new CardSlot[]{cardSlot1, cardSlot2, cardSlot3};

        initSlots(emptySlotImage);

        widthProperty().addListener((observable, oldValue, newValue) ->
        {
            initSlots(emptySlotImage);

            for(CardSlot slot : slots)
            {
                if(!slot.isEmpty())slot.resize(getWidth());
            }
        });

        titleLabel.setText(title);
        titleLabel.minHeightProperty().bind(heightProperty().multiply(0.2));
        fontSize.bind(titleLabel.minHeightProperty().multiply(0.5));

        fontSize.addListener((observable, oldValue, newValue) ->
                titleLabel.setFont(new Font(newValue.doubleValue())));

        vBox.setAlignment(Pos.TOP_CENTER);
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.setSpacing((getMaxWidth()/4)*0.2);
    }
}
