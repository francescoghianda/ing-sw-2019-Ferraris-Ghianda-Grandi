package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.CardData;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;


public class CardSlot extends VBox
{
    public static final String WEAPON_MODE = "weapon";
    public static final String POWER_UP_MODE = "powerup";

    private String mode;
    private CardView cardView;

    private int cardViewTransition;

    public CardSlot()
    {

    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public boolean isEmpty()
    {
        return cardView == null;
    }

    public CardData getCard()
    {
        return cardView == null ? null : cardView.getCard();
    }

    public void setCard(CardData card, int transition, double cardPaneWidth, CardView.OnCardViewClickListener listener)
    {
        getChildren().clear();
        cardView = new CardView(card, transition);

        if(mode.equals(WEAPON_MODE))cardView.setMaxWidth(cardPaneWidth/4.5);
        else cardView.setMaxWidth(cardPaneWidth/4.7);

        cardView.setOnCardViewClickListener(listener);
        getChildren().add(cardView);
    }

    public void removeCard()
    {
        getChildren().clear();
        cardView = null;
    }

    public void setCardViewTransition(int cardViewTransition)
    {
        if(cardView != null)cardView.setTransition(cardViewTransition);
    }

    public void setCardSelectable(boolean selectable)
    {
        if(cardView != null)cardView.setSelectable(selectable);
    }

    public void setCardSelected(boolean selected)
    {
        if(cardView != null)cardView.setSelected(selected);
    }

    public void setCardEnabled(boolean enabled)
    {
        if(cardView != null)cardView.setEnabled(enabled);
    }

    public boolean containsCard(CardData card)
    {
        if(isEmpty())return false;
        return cardView.getCard().equals(card);
    }

    public void init(double cardPaneWidth, String mode)
    {
        this.mode = mode;

        double maxWidth = cardPaneWidth/3.8;
        Insets insets;

        if(mode.equals(WEAPON_MODE))
        {
            insets = new Insets(maxWidth/16, 0, 0, 0);
        }
        else
        {
            insets = new Insets(maxWidth/15, maxWidth/17, 0, 0);
        }

        setPadding(insets);
    }

    public boolean isCardSelected()
    {
        if(isEmpty())return false;
        return cardView.isSelected();
    }

    public void resize(double cardPaneWidth)
    {
        if(mode.equals("weapon"))cardView.setMaxWidth(cardPaneWidth/4.5);
        else cardView.setMaxWidth(cardPaneWidth/4.7);
    }
}
