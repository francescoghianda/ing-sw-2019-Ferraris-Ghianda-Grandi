package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.ui.gui.GUI;

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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    private StackPane slot1;

    @FXML
    private StackPane slot2;

    @FXML
    private StackPane slot3;

    @FXML
    private VBox cardSlot1;

    @FXML
    private VBox cardSlot2;

    @FXML
    private VBox cardSlot3;

    private Label descriptionLabel;
    private String title;
    private DoubleProperty fontSize;
    private String type;
    private int cardNumber;

    public CardPane(@NamedArg("title") String title, @NamedArg("type") String type)
    {
        this.type = type;
        this.title = title;
        fontSize = new SimpleDoubleProperty();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/cardPane.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
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

    public void addCard(Card card, CardView.OnCardViewClickListener listener)
    {
        cardNumber++;
        if(cardNumber > 3)throw new CardPaneOutOfBoundsException();

        CardView cardView = new CardView(card, descriptionLabel, CardView.SCALE_TRANSITION);
        cardView.setMaxWidth(getMaxWidth()/4.5);

        cardView.setOnCardViewClickListener(listener);

        getFreeCardSlot().getChildren().add(cardView);
    }

    public void updateCards(List<Card> cards, CardView.OnCardViewClickListener listener)
    {
        removeIfNotInList(cardSlot1, cards);
        removeIfNotInList(cardSlot2, cards);
        removeIfNotInList(cardSlot3, cards);

        cards.forEach(card ->
        {
            if(!isCardPresent(card))addCard(card, listener);
        });
    }

    private void removeIfNotInList(VBox cardSlot, List<Card> cards)
    {
        if(cardSlot.getChildren().isEmpty())return;
        Card currentCard = ((CardView)cardSlot.getChildren().get(0)).getCard();
        for(Card card : cards)
        {
            if(card.equals(currentCard))return;
        }
        cardNumber--;
        cardSlot.getChildren().clear();
    }

    private boolean isCardPresent(Card card)
    {
        return isCardInSlot(card, cardSlot1) || isCardInSlot(card, cardSlot2) || isCardInSlot(card, cardSlot3);
    }

    private boolean isCardInSlot(Card card, VBox cardSlot)
    {
        return !cardSlot.getChildren().isEmpty() && ((CardView)cardSlot.getChildren().get(0)).getCard().equals(card);
    }

    private VBox getFreeCardSlot()
    {
        if(cardSlot1.getChildren().isEmpty())return cardSlot1;
        if(cardSlot2.getChildren().isEmpty())return cardSlot2;
        return cardSlot3;
    }

    public void setDescriptionLabel(Label descriptionLabel)
    {
        this.descriptionLabel = descriptionLabel;
        if(!cardSlot1.getChildren().isEmpty())((CardView)cardSlot1.getChildren()).setDescriptionLabel(descriptionLabel);
        if(!cardSlot2.getChildren().isEmpty())((CardView)cardSlot2.getChildren()).setDescriptionLabel(descriptionLabel);
        if(!cardSlot3.getChildren().isEmpty())((CardView)cardSlot3.getChildren()).setDescriptionLabel(descriptionLabel);
    }

    private void initSlots(Image image)
    {
        emptySlot1.setImage(image);
        emptySlot2.setImage(image);
        emptySlot3.setImage(image);

        double maxWidth = getMaxWidth()/3.8;
        emptySlot1.setFitWidth(maxWidth);
        emptySlot2.setFitWidth(maxWidth);
        emptySlot3.setFitWidth(maxWidth);

        cardSlot1.setPadding(new Insets(maxWidth/16, 0, 0, 0));
        cardSlot2.setPadding(new Insets(maxWidth/16, 0, 0, 0));
        cardSlot3.setPadding(new Insets(maxWidth/16, 0, 0, 0));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setMaxWidth(GUI.getScreenWidth()/4);
        setMinWidth(GUI.getScreenWidth()/4);

        setMinHeight(0);

        Image emptySlotImage;

        if ("weapon".equals(type)) emptySlotImage = new Image(getClass().getResourceAsStream("/img/weapon_slot.png"));
        else emptySlotImage = new Image(getClass().getResourceAsStream("/img/powerup_slot.png"));

        initSlots(emptySlotImage);

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
