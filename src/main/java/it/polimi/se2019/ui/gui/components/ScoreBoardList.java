package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.player.PlayerData;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ScoreBoardList extends Pane
{
    private List<ScoreBoardCard> cards;
    private int spacing;

    public ScoreBoardList()
    {
        cards = new ArrayList<>();
        spacing = 20;

        widthProperty().addListener(this::onResize);
        heightProperty().addListener(this::onResize);
    }

    public void setScoreBoard(List<PlayerData> scoreBoard)
    {
        double cardHeight = getHeight()/scoreBoard.size();

        for(int i = 0; i < scoreBoard.size(); i++)
        {
            ScoreBoardCard card = new ScoreBoardCard(scoreBoard.get(i));

            card.setPrefWidth(getWidth());
            card.setPrefHeight(cardHeight);
            card.setLayoutX(0);
            card.setLayoutY((cardHeight+spacing)*i);

            getChildren().add(card);

            cards.add(card);
        }
    }

    private void onResize(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
    {
        double cardHeight = getHeight()/cards.size();

        int i = 0;
        for(ScoreBoardCard card : cards)
        {
            card.setPrefWidth(getWidth());
            card.setPrefHeight(cardHeight);
            card.setMaxHeight(cardHeight);
            card.setLayoutX(0);
            card.setLayoutY((cardHeight+spacing)*i);
            i++;
        }
    }
}
