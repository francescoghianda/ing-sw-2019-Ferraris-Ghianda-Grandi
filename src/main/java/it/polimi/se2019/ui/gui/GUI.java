package it.polimi.se2019.ui.gui;

import it.polimi.se2019.card.CardData;
import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.network.OnServerDisconnectionListener;
import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Action;
import it.polimi.se2019.player.PlayerData;
import it.polimi.se2019.ui.GameEvent;
import it.polimi.se2019.ui.NetworkInterface;
import it.polimi.se2019.ui.UI;
import it.polimi.se2019.ui.gui.dialogs.CloseDialog;
import it.polimi.se2019.ui.gui.dialogs.ChooseWeaponsDialog;
import it.polimi.se2019.ui.gui.dialogs.MessageDialog;
import it.polimi.se2019.ui.gui.notification.NotificationPane;
import it.polimi.se2019.ui.gui.value.ValueObserver;
import it.polimi.se2019.utils.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class GUI extends Application implements UI, EventHandler<WindowEvent>, OnServerDisconnectionListener
{
    private static final String TITLE = "Adrenalina";

    private static Stage window;
    private SceneManager sceneManager;
    private MediaPlayer roundStartSound;

    private SequentialTransition scoreBoardTransition;

    public GUI()
    {

    }

    @Override
    public void init()
    {

    }

    private void setTooltipIndefiniteDuration()
    {
        try
        {
            Field behaviorField = Tooltip.class.getDeclaredField("BEHAVIOR");
            behaviorField.setAccessible(true);
            Object behavior = behaviorField.get(null);

            Field timerField = behavior.getClass().getDeclaredField("hideTimer");
            timerField.setAccessible(true);
            Timeline timer = (Timeline) timerField.get(behavior);

            timer.getKeyFrames().clear();
            timer.getKeyFrames().add(new KeyFrame(Duration.INDEFINITE));
        }
        catch (Exception e)
        {
            Logger.exception(e);
        }
    }

    @Override
    public void start(Stage primaryStage)
    {
        window = primaryStage;

        setTooltipIndefiniteDuration();

        try
        {
            roundStartSound = new MediaPlayer(new Media(getClass().getResource("/sounds/round_start.mp3").toURI().toString()));
            roundStartSound.setOnEndOfMedia(() -> roundStartSound.stop());
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        sceneManager = SceneManager.createSceneManager(window, this);

        //window.setMinHeight(getScreenHeight()/1.2);
        sceneManager.onSceneSelectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue == SceneManager.getScene(SceneManager.WAIT_SCENE))
            {
                WaitScene waitScene = ((WaitScene)newValue.getRoot());
                waitScene.showTimer(false);
                window.setWidth(waitScene.getPrefWidth());
                window.setHeight(waitScene.getPrefHeight());
            }
        });

        ChangeListener<Number> sizeChangedListener = (observable, oldValue, newValue) ->
        {
            if(sceneManager.getCurrentScene().equals(SceneManager.getScene(SceneManager.MATCH_SCENE)))window.setMinWidth(window.getHeight()*1.48);
        };

        scoreBoardTransition = new SequentialTransition();
        FadeTransition fadeOutMatchSceneTransition = new FadeTransition(Duration.millis(400));
        fadeOutMatchSceneTransition.setNode(MatchScene.getInstance());
        fadeOutMatchSceneTransition.setFromValue(1);
        fadeOutMatchSceneTransition.setToValue(0);

        fadeOutMatchSceneTransition.setOnFinished((value) ->
        {
            MatchScene.getInstance().setOpacity(1);
            sceneManager.getScoreBoardScene().setOpacity(0);
            sceneManager.setScene(SceneManager.SCORE_BOARD_SCENE);
        });

        FadeTransition fadeInScoreBoardScene = new FadeTransition(Duration.millis(400));
        fadeInScoreBoardScene.setNode(sceneManager.getScoreBoardScene());
        fadeInScoreBoardScene.setFromValue(0);
        fadeInScoreBoardScene.setToValue(1);
        scoreBoardTransition.getChildren().addAll(fadeOutMatchSceneTransition, fadeInScoreBoardScene);

        window.widthProperty().addListener(sizeChangedListener);
        window.heightProperty().addListener(sizeChangedListener);

        window.heightProperty().addListener((observable, oldValue, newValue) -> onSizeChange(window.getWidth(), newValue.doubleValue()));
        window.widthProperty().addListener((observable, oldValue, newValue) -> onSizeChange(newValue.doubleValue(), window.getHeight()));

        sceneManager.setScene(SceneManager.START_MENU_SCENE);

        window.setTitle(TITLE);
        window.setResizable(false);
        window.setOnCloseRequest(this);

        window.show();
        window.centerOnScreen();

    }

    public static Stage getWindow()
    {
        return window;
    }

    @Override
    public void timeout()
    {
        ValueObserver.timeout();
    }

    @Override
    public void notifyImpossibleAction()
    {
        SceneManager.runOnFxThread(() -> NotificationPane.showNotification("Azione impossibile"));
    }

    @Override
    public void showNotification(String text)
    {
        SceneManager.runOnFxThread(() -> NotificationPane.showNotification(text));
    }

    @Override
    public void closeConnection()
    {

    }

    @Override
    public boolean showScoreBoardAndChooseIfPlayAgain(ArrayList<PlayerData> scoreBoard)
    {
        SceneManager.runOnFxThread(() ->
        {
            SceneManager.getInstance().getScoreBoardScene().show(scoreBoard, MatchScene.getInstance().getGameDataProperty().get().getPlayer().getUsername().equals(scoreBoard.get(0).getUsername()));
            scoreBoardTransition.playFromStart();
        });

        return new ValueObserver<Boolean>().get(sceneManager.getScoreBoardScene().playAgainValue);
    }

    @Override
    public void update(GameData data)
    {
        sceneManager.getMatchScene().update(data);
    }

    @Override
    public void startUI()
    {
        Thread thread = new Thread(Application::launch);
        thread.start();
    }

    @Override
    public String login()
    {
        sceneManager.setScene(SceneManager.LOGIN_SCENE);
        return sceneManager.getUsername();
    }

    @Override
    public void logged()
    {
        SceneManager.runOnFxThread(() ->
        {
            sceneManager.setScene(SceneManager.WAIT_SCENE);
            window.setResizable(false);
        });
    }
    

    @Override
    public void gameIsStarting()
    {
        SceneManager.runOnFxThread(()->
        {
            if(!((WaitScene)SceneManager.getSceneRoot(SceneManager.WAIT_SCENE)).isTimerVisible())((WaitScene)SceneManager.getSceneRoot(SceneManager.WAIT_SCENE)).showTimer(true);
        });
    }

    @Override
    public void gameStarted()
    {
        SceneManager.runOnFxThread(() -> sceneManager.setScene(SceneManager.MATCH_SCENE));
    }

    @Override
    public void showTimerCountdown(int remainSeconds)
    {
        SceneManager.runOnFxThread(()-> ((WaitScene)SceneManager.getSceneRoot(SceneManager.WAIT_SCENE)).updateTimer(remainSeconds));
    }

    @Override
    public void youAreFirstPlayer()
    {

    }

    @Override
    public void firstPlayerIs(String firstPlayerUsername)
    {

    }

    @Override
    public void connectionRefused()
    {
        ((StartMenuScene)SceneManager.getSceneRoot(SceneManager.START_MENU_SCENE)).connectionRefused();
    }

    @Override
    public boolean notEnoughAmmo(boolean askToSellPowerUp)
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(()->
        {

            if(askToSellPowerUp)
            {
                matchScene.setOptions("Non hai abbastanza munizioni!\nVuoi vendere un potenziamento?", "Si", "No");
            }
            else
            {
                matchScene.setOptions("Non hai abbastanza munizioni!");
            }
        });

        if(askToSellPowerUp)
        {
            String option = new ValueObserver<String>().get(matchScene.selectedOption);
            return option.equals("Si");
        }

        return false;
    }

    @Override
    public void requestFocus()
    {
        Platform.runLater(()-> window.requestFocus());
    }

    @Override
    public String chooseOrCancel(Bundle<String, ArrayList<String>> options)
    {
        //TODO aggiungere possibilità di annullare l'azione

        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(() -> matchScene.setOptions(options.getFirst(), options.getSecond()));
        return new ValueObserver<String>().get(matchScene.selectedOption);
    }

    @Override
    public String choose(Bundle<String, ArrayList<String>> options)
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(() -> matchScene.setOptions(options.getFirst(), options.getSecond()));
        return new ValueObserver<String>().get(matchScene.selectedOption);
    }

    @Override
    public String chooseSpawnPoint(CardData option1, CardData option2)
    {
        SceneManager.runOnFxThread(() ->
        {
            if(option1 != null)MatchScene.getInstance().addCard(option1);
            if(option2 != null)MatchScene.getInstance().addCard(option2);
            MatchScene.getInstance().setOptions("Seleziona il potenziamento che vuoi scartare");
        });
        return new ValueObserver<String>().get(sceneManager.getMatchScene().selectedSpawnPoint);
    }

    @Override
    public Bundle<Action, Serializable> chooseActionFrom(Action[] possibleActions)
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(() ->
        {
            matchScene.setOptions("È il tuo turno");
            matchScene.enableActions(possibleActions);
        });
        Action selectedAction = new ValueObserver<Action>().get(matchScene.selectedAction);
        Serializable optionalActionObject = matchScene.getOptionalActionObject();
        matchScene.deleteOptionalActionObject();
        return Bundle.ofSecondNullable(selectedAction, optionalActionObject);
    }

    @Override
    public Coordinates chooseBlock(int maxDistance) throws CanceledActionException
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(() -> matchScene.chooseBlock(maxDistance));
        return new ValueObserver<Coordinates>().get(matchScene.selectedBlock);
    }

    public Coordinates chooseBlockFrom(ArrayList<Coordinates> coordinates)throws CanceledActionException
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(() -> matchScene.chooseBlock(coordinates));
        return new ValueObserver<Coordinates>().get(matchScene.selectedBlock);
    }

    @Override
    public void roundStart()
    {
        SceneManager.runOnFxThread(() -> roundStartSound.play());
    }

    @Override
    public void roundEnd()
    {

    }

    @Override
    public CardData chooseWeaponFromPlayer() throws CanceledActionException
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(matchScene::chooseWeaponFromPlayer);
        return new ValueObserver<CardData>().get(matchScene.selectedWeapon);
    }

    @Override
    public CardData chooseWeaponFromBlock() throws CanceledActionException
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(matchScene::chooseWeaponFromBlock);
        return new ValueObserver<CardData>().get(matchScene.selectedWeapon);
    }

    @Override
    public CardData choosePowerUp() throws CanceledActionException
    {
        MatchScene matchScene = MatchScene.getInstance();
        SceneManager.runOnFxThread(matchScene::choosePowerUp);
        return new ValueObserver<CardData>().get(matchScene.selectedPowerUp);
    }

    @Override
    public ArrayList<CardData> chooseWeaponsToReload(ArrayList<CardData> weapons)
    {
        return chooseWeaponsToReload(weapons, ChooseWeaponsDialog.MULTIPLE_SELECTION_MODE);
    }

    @Override
    public CardData chooseWeaponToReload(ArrayList<CardData> weapons)
    {
        ArrayList<CardData> chosen = chooseWeaponsToReload(weapons, ChooseWeaponsDialog.SINGLE_SELECTION_MODE);
        if(chosen != null)return chosen.get(0);
        return null;
    }

    @Override
    public CardData chooseWeaponToSwap() throws CanceledActionException
    {
        Task<Optional<ArrayList<CardData>>> task = new Task<Optional<ArrayList<CardData>>>()
        {
            @Override
            protected Optional<ArrayList<CardData>> call()
            {
                List<CardData> weapons = MatchScene.getInstance().getGameDataProperty().get().getPlayer().getWeapons();
                ChooseWeaponsDialog dialog = new ChooseWeaponsDialog(weapons, ChooseWeaponsDialog.SINGLE_SELECTION_MODE, "Seleziona l'arma che vuoi scambiare", "Scambia");
                return dialog.showAndWait();
            }
        };
        SceneManager.runOnFxThread(task);

        try
        {
            Optional<ArrayList<CardData>> optional = task.get();
            ArrayList<CardData> selected = optional.orElseThrow(() -> new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER));
            if(selected.isEmpty())throw new CanceledActionException(CanceledActionException.Cause.CANCELED_BY_USER);
            return selected.get(0);
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            throw new CanceledActionException(CanceledActionException.Cause.ERROR);
        }
    }

    private ArrayList<CardData> chooseWeaponsToReload(ArrayList<CardData> weapons, int selectionMode)
    {
        Task<Optional<ArrayList<CardData>>> task = new Task<Optional<ArrayList<CardData>>>()
        {
            @Override
            protected Optional<ArrayList<CardData>> call()
            {
                String title = selectionMode == ChooseWeaponsDialog.SINGLE_SELECTION_MODE ? "Seleziona un'arma da ricaricare" : "Seleziona le armi che vuoi ricaricare";
                ChooseWeaponsDialog dialog = new ChooseWeaponsDialog(weapons, selectionMode, title, "Ricarica");
                return dialog.showAndWait();
            }
        };
        SceneManager.runOnFxThread(task);

        try
        {
            Optional<ArrayList<CardData>> optional = task.get();
            return optional.orElseGet(ArrayList::new);
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static double getStageWidth()
    {
        return window.getWidth();
    }

    public static double getStageHeight()
    {
        return window.getHeight();
    }

    public static double getMinStageWidth()
    {
        return GUI.getScreenWidth()/1.2;
    }

    public static double getMinStageHeight()
    {
        return GUI.getScreenHeight()/1.06;
    }

    public static double getScreenWidth()
    {
        return Screen.getPrimary().getVisualBounds().getWidth();
    }

    public static double getScreenHeight()
    {
        return Screen.getPrimary().getVisualBounds().getHeight();
    }

    @Override
    public void handle(WindowEvent event)
    {
        CloseDialog closeDialog = new CloseDialog(window);
        Optional<ButtonType> result = closeDialog.showAndWait();
        if(!(result.isPresent() && result.get().equals(ButtonType.YES)))
        {
            event.consume();
        }
        else
        {
            Platform.exit();
            System.exit(0);
        }
    }

    @Override
    public void handle(GameEvent event)
    {

    }

    public void onSizeChange(double width, double height)
    {
        sceneManager.getMatchScene().setSize(width, height);
    }

    @Override
    public void onServerDisconnection()
    {
        SceneManager.runOnFxThread(() ->
        {
            GaussianBlur blur = new GaussianBlur(10);
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setSaturation(-1);
            blur.setInput(colorAdjust);
            window.getScene().getRoot().setEffect(blur);
            MessageDialog dialog = new MessageDialog("Hai perso la connessione col server!");
            dialog.showAndWait();
            System.exit(0);
        });
    }
}
