package it.polimi.se2019.ui.gui;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.ui.gui.components.*;
import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MatchScene extends GridPane implements Initializable, CardView.OnCardViewClickListener, MapView.OnBlockClickListener, ActionPane.OnActionClickListener, ChoosePane.OnOptionChosenListener
{

    private static  MatchScene instance;

    @FXML
    private BoardView board;

    @FXML
    private MapView mapView;

    @FXML
    private ActionPane actionPane;

    @FXML
    private CardPane weaponsCardPane;

    @FXML
    private CardPane powerUpsCardPane;

    @FXML
    private GridPane cardGridPane;

    @FXML
    private ChoosePane choosePane;

    @FXML
    private Label cardDescriptionLabel;


    private boolean firstUpdate;
    final ObservableValue<String> selectedPowerUpId;

    private MatchScene()
    {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MatchScene.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            Logger.exception(e);
        }

        selectedPowerUpId = new ObservableValue<>();

        firstUpdate = true;
    }

    public static MatchScene getInstance()
    {
        if(instance == null)instance = new MatchScene();
        return instance;
    }

    public Label getCardDescriptionLabel()
    {
        return cardDescriptionLabel;
    }

    public void update(GameData data)
    {
        if(firstUpdate)
        {
            actionPane.setColor(data.getPlayer().getColor());
            board.setColor(data.getPlayer().getColor());
            choosePane.setColor(data.getPlayer().getColor());
            firstUpdate = false;
        }
        mapView.update(data.getMap());
        mapView.setRemainingSkulls(data.getRemainingSkulls(), data.getDeaths());
        weaponsCardPane.updateCards(data.getPlayer().getWeapons(), this);
        powerUpsCardPane.updateCards(data.getPlayer().getPowerUps(), this);
        board.update(data.getPlayer());
    }

    public void addCard(Card card)
    {
        if(card.getId().startsWith("PUC"))powerUpsCardPane.addCard(card, this);
        else weaponsCardPane.addCard(card, this);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setStyle("-fx-background-image: url('/img/texture3.png')");

        cardGridPane.setMaxHeight(GUI.getScreenHeight()/1.4);
        mapView.setOnBlockClickListener(this);
        actionPane.setOnActionClickListener(this);
        choosePane.setOnOptionChosenListener(this);
        cardDescriptionLabel.setFont(new Font(15));
        cardDescriptionLabel.setStyle("-fx-text-fill: white");
        cardDescriptionLabel.setPadding(new Insets(0, 0, 0, 20));

        //TEST

        choosePane.setText("Chi vuoi colpire?");
        choosePane.addOption("Fra");
        choosePane.addOption("Simo");
        choosePane.addOption("Silvia");

        choosePane.setColor(GameColor.BLUE);

    }

    @Override
    public void onCardClick(CardView cardView)
    {
        if(cardView.getCardType() == CardView.WEAPON_CARD)
        {

        }
        else if(cardView.getCardType() == CardView.POWER_UP_CARD)
        {
            selectedPowerUpId.setValue(cardView.getCardId());
        }
    }

    @Override
    public void onBlockClick(int blockX, int blockY)
    {
        System.out.println(blockX+" - "+blockY);
    }

    @Override
    public void onActionClick(int action)
    {
        System.out.println(action);
    }

    @Override
    public void onOptionChosen(String chosenOption)
    {
        System.out.println(chosenOption);
    }
}
