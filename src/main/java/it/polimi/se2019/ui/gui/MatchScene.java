package it.polimi.se2019.ui.gui;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.map.BlockData;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.player.Action;
import it.polimi.se2019.ui.gui.components.*;
import it.polimi.se2019.ui.gui.value.CancelableValue;
import it.polimi.se2019.ui.gui.value.Value;
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
import java.util.Arrays;
import java.util.List;
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


    private GameData gameData;
    private boolean chooseBlock;
    private boolean chooseWeaponFromPlayer;
    private boolean chooseWeaponFromBlock;
    private boolean choosePowerUp;
    private int maxDistance;
    private boolean firstUpdate;
    final Value<String> selectedSpawnPoint;
    final Value<Action> selectedAction;
    final CancelableValue<Coordinates> selectedBlock;
    final Value<String> selectedOption;
    final CancelableValue<Card> selectedWeapon;
    final CancelableValue<Card> selectedPowerUp;

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

        selectedSpawnPoint = new Value<>();
        selectedAction = new Value<>();
        selectedBlock = new CancelableValue<>();
        selectedOption = new Value<>();
        selectedWeapon = new CancelableValue<>();
        selectedPowerUp = new CancelableValue<>();

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

    public GameData getGameData()
    {
        return gameData;
    }

    public void update(GameData data)
    {
        this.gameData = data;
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
        actionPane.disableAll();
        choosePane.setOnOptionChosenListener(this);
        cardDescriptionLabel.setFont(new Font(15));
        cardDescriptionLabel.setStyle("-fx-text-fill: white");
        cardDescriptionLabel.setPadding(new Insets(0, 0, 0, 20));

    }

    public void setOptions(String question, String... options)
    {
        choosePane.clear();
        choosePane.setText(question);
        Arrays.asList(options).forEach(choosePane::addOption);
    }

    public void setOptions(String question, List<String> options)
    {
        String[] optionsArray = new String[options.size()];
        setOptions(question, options.toArray(optionsArray));
    }

    public void enableActions(Action[] actions)
    {
        actionPane.disableAll();

        for(Action action : actions)
        {
            if(action == Action.MOVE)actionPane.enable(ActionPane.MOVE_ACTION);
            else if(action == Action.FIRE)actionPane.enable(ActionPane.FIRE_ACTION);
            else if(action == Action.GRAB)actionPane.enable(ActionPane.GRAB_ACTION);
            else if(action == Action.RELOAD)actionPane.enable(ActionPane.RELOAD_ACTION);
        }
        actionPane.enable(ActionPane.END_ROUND);
    }

    @Override
    public void onCardClick(CardView cardView)
    {
        if(cardView.getCardType() == CardView.WEAPON_CARD)
        {
            if(chooseWeaponFromPlayer)
            {
                chooseWeaponFromPlayer = false;
                choosePane.clear();
                selectedWeapon.set(cardView.getCard());
            }
            else if(chooseWeaponFromBlock)
            {
                chooseWeaponFromBlock = false;
                choosePane.clear();
                selectedWeapon.set(cardView.getCard());
            }
        }
        else if(cardView.getCardType() == CardView.POWER_UP_CARD)
        {
            selectedSpawnPoint.set(cardView.getCardId());
            selectedAction.set(Action.USE_POWER_UP);

            if(choosePowerUp)
            {
                choosePowerUp = false;
                choosePane.clear();
                selectedPowerUp.set(cardView.getCard());
            }
        }
    }

    public void chooseBlock(int maxDistance)
    {
        chooseBlock = true;
        choosePane.setText("Seleziona un blocco (Distanza massima: "+maxDistance+")");
        this.maxDistance = maxDistance;
    }

    public void choosePowerUp()
    {
        choosePane.setText("Scegli un potenziamento");
        choosePowerUp = true;
    }

    public void chooseWeaponFromPlayer()
    {
        choosePane.setText("Scegli un'arma");
        chooseWeaponFromPlayer = true;
    }

    public void chooseWeaponFromBlock()
    {
        choosePane.setText("Scegli l'arma che vuoi raccogliere");
        chooseWeaponFromBlock = true;
    }

    @Override
    public void onBlockClick(int blockX, int blockY)
    {
        BlockData clicked = gameData.getMap().getBlock(blockX, blockY);
        int playerX = gameData.getPlayer().getX();
        int playerY = gameData.getPlayer().getY();
        BlockData playerBlock = gameData.getMap().getBlock(playerX, playerY);
        int distance = gameData.getMap().getDistance(playerBlock, clicked);

        if(chooseBlock && distance <= maxDistance)
        {
            chooseBlock = false;
            choosePane.clear();
            selectedBlock.set(new Coordinates(clicked.getX(), clicked.getY()));
        }
    }

    @Override
    public void onActionClick(int action)
    {
        if(action == ActionPane.MOVE_ACTION)selectedAction.set(Action.MOVE);
        else if(action == ActionPane.FIRE_ACTION)selectedAction.set(Action.FIRE);
        else if(action == ActionPane.RELOAD_ACTION)selectedAction.set(Action.RELOAD);
        else if(action == ActionPane.GRAB_ACTION)selectedAction.set(Action.GRAB);
        else if(action == ActionPane.END_ROUND)selectedAction.set(Action.END_ROUND);
    }

    @Override
    public void onOptionChosen(String chosenOption)
    {
        selectedOption.set(chosenOption);
        choosePane.clear();
    }
}
