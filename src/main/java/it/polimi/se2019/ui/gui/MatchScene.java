package it.polimi.se2019.ui.gui;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.CardData;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.map.BlockData;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.player.Action;
import it.polimi.se2019.ui.gui.components.*;
import it.polimi.se2019.ui.gui.value.CancelableValue;
import it.polimi.se2019.ui.gui.value.Value;
import it.polimi.se2019.utils.logging.Logger;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import java.util.ArrayList;
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
    private HBox bottomHBox;

    @FXML
    private PlayersInfoTable playersInfoTable;

    @FXML
    private ColumnConstraints col0;

    @FXML
    private ColumnConstraints col1;

    @FXML
    private RowConstraints row0;

    @FXML
    private RowConstraints row1;


    private final ObjectPropertyBase<GameData> gameDataProperty;

    private boolean chooseBlock;
    private boolean chooseWeaponFromPlayer;
    private boolean chooseWeaponFromBlock;
    private boolean choosePowerUp;
    private boolean firstUpdate;
    final Value<String> selectedSpawnPoint;
    final Value<Action> selectedAction;
    final CancelableValue<Coordinates> selectedBlock;
    final Value<String> selectedOption;
    final CancelableValue<CardData> selectedWeapon;
    final CancelableValue<CardData> selectedPowerUp;

    private Serializable optionalActionObject;
    //private List<Resizable> resizableComponents;

    private MatchScene()
    {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MatchScene.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        gameDataProperty = new SimpleObjectProperty<>();

        //resizableComponents = new ArrayList<>();

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

    public Serializable getOptionalActionObject()
    {
        return optionalActionObject;
    }

    public void deleteOptionalActionObject()
    {
        optionalActionObject = null;
    }

    public ObjectProperty<GameData> getGameDataProperty()
    {
        return gameDataProperty;
    }

    public void update(GameData data)
    {
        Platform.runLater(() -> gameDataProperty.set(data));
    }

    public void addCard(CardData card)
    {
        if(card.getId().startsWith("PUC"))powerUpsCardPane.addCard(card, this);
        else weaponsCardPane.addCard(card, this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setStyle("-fx-background-image: url('/img/texture3.png')");

        //setGridLinesVisible(true);

        mapView.setOnBlockClickListener(this);
        actionPane.setOnActionClickListener(this);
        actionPane.disableAll();
        choosePane.setOnOptionChosenListener(this);

        //bottomHBox.setBackground(new Background(new BackgroundFill(new Color(1, 0, 0, 0.5), null, null)));


        ChangeListener<Number> bottomBoxSizeChangeListener = (observable, oldValue, newValue) ->
        {
            if(!GUI.getWindow().isMaximized())actionPane.setMinWidth(getWidth()*(col0.getPercentWidth()/100) - board.getWidth());
        };

        bottomHBox.widthProperty().addListener(bottomBoxSizeChangeListener);
        bottomHBox.heightProperty().addListener(bottomBoxSizeChangeListener);

        widthProperty().addListener(bottomBoxSizeChangeListener);

        SceneManager.getInstance().onSceneSelectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.equals(SceneManager.getScene(SceneManager.MATCH_SCENE)))
            {
                Stage window = GUI.getWindow();
                window.setResizable(true);
                window.setMinHeight(GUI.getScreenHeight()/1.5);
                window.setHeight(GUI.getScreenHeight()/1.1);
                window.setWidth(window.getMinWidth());
                window.centerOnScreen();
                actionPane.setMinWidth(getWidth()*(col0.getPercentWidth()/100) - board.getWidth());
            }
        });

        GUI.getWindow().fullScreenProperty().addListener((observable, oldValue, newValue) ->
        {
            adjustSize();
        });

        GUI.getWindow().maximizedProperty().addListener((observable, oldValue, newValue) ->
        {
            adjustSize();
        });


        gameDataProperty.addListener((observable, oldData, gameData) ->
        {

            if(firstUpdate)
            {
                firstUpdate = false;

                choosePane.setColor(gameData.getPlayer().getColor());
                actionPane.setColor(gameData.getPlayer().getColor());
                board.setColor(gameData.getPlayer().getColor());
            }

            board.update(gameData.getPlayer());
            mapView.update(gameData.getMap());
            mapView.setRemainingSkulls(gameData.getRemainingSkulls(), gameData.getDeathsNumber());
            weaponsCardPane.updateCards(gameData.getPlayer().getWeapons(), this);
            powerUpsCardPane.updateCards(gameData.getPlayer().getPowerUps(), this);
            playersInfoTable.update();
        });

    }

    private void adjustSize()
    {
        Thread thread = new Thread(() ->
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            Platform.runLater(() ->
                    actionPane.setMinWidth(getWidth()*(col0.getPercentWidth()/100) - board.getWidth()));

        });
        thread.start();
    }

    public void setOptions(String question, String... options)
    {
        choosePane.clear();
        choosePane.setText(question);
        Arrays.asList(options).forEach(choosePane::addOption);
    }

    public void addCancelableOption(CancelableValue value)
    {
        choosePane.addCancelButton(value);
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
        actionPane.enable(ActionPane.END_ACTION);
        actionPane.enable(ActionPane.END_ROUND);
    }

    public void setSize(double width, double height)
    {
        //System.out.println(width+" - "+height);

        //resizableComponents.forEach(resizable -> resizable.resize(width, height));
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
            optionalActionObject = cardView.getCard();
            selectedAction.set(Action.USE_POWER_UP);

            if(choosePowerUp)
            {
                choosePowerUp = false;
                choosePane.clear();
                selectedPowerUp.set(cardView.getCard());
            }
        }
    }

    public void onActionCanceled()
    {
        mapView.setColored(false);
    }

    public void chooseBlock(int maxDistance)
    {
        chooseBlock = true;
        choosePane.setText("Seleziona un blocco (Distanza massima: "+maxDistance+")");
        mapView.enableBlocks(maxDistance);
        mapView.setColored(true);
    }

    public void chooseBlock(ArrayList<Coordinates> coordinates)
    {
        chooseBlock = true;
        choosePane.setText("Seleziona un blocco");
        mapView.enableBlocks(coordinates);
        mapView.setColored(true);
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
        BlockData clicked = gameDataProperty.getValue().getMap().getBlock(blockX, blockY);

        if(chooseBlock)
        {
            chooseBlock = false;
            choosePane.clear();
            mapView.setColored(false);
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
        else if(action == ActionPane.END_ACTION)selectedAction.set(Action.END_ACTION);

        actionPane.disableAll();
    }

    @Override
    public void onOptionChosen(String chosenOption)
    {
        selectedOption.set(chosenOption);
        choosePane.clear();
    }
}
