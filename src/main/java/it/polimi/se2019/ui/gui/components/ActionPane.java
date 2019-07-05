package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.ui.gui.GUI;
import it.polimi.se2019.utils.constants.GameColor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * defines the action pane specifing the parameters associated with actions like MOVE, GRAB, FIRE, RELOAD, END_ROUND, END_ACTION
 */
public class ActionPane extends VBox implements Initializable
{
    public static final int MOVE_ACTION = 0;
    public static final int FIRE_ACTION = 1;
    public static final int GRAB_ACTION = 2;
    public static final int RELOAD_ACTION = 3;
    public static final int END_ROUND = 4;
    public static final int END_ACTION = 5;

    @FXML
    private TilePane tilePane;

    @FXML
    private ColoredButton moveButton;

    @FXML
    private ColoredButton fireButton;

    @FXML
    private ColoredButton grabButton;

    @FXML
    private ColoredButton reloadButton;

    @FXML
    private ColoredButton endRoundButton;

    @FXML
    private ColoredButton endActionButton;

    private OnActionClickListener clickListener;

    public ActionPane()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/actionPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        load(fxmlLoader);
    }

    private void load(FXMLLoader fxmlLoader)
    {
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setOnActionClickListener(OnActionClickListener listener)
    {
        this.clickListener = listener;
    }

    public void setColor(GameColor color)
    {
        Color backgroundColor = Color.web(color.getColor(), 0.1);
        setBackground(new Background(new BackgroundFill(backgroundColor, null, null)));

        moveButton.setColor(color);
        fireButton.setColor(color);
        grabButton.setColor(color);
        reloadButton.setColor(color);
        endRoundButton.setColor(color);
        endActionButton.setColor(color);
    }

    /**
     * let to disable all the buttons when required
     */
    public void disableAll()
    {
        moveButton.setDisable(true);
        fireButton.setDisable(true);
        grabButton.setDisable(true);
        reloadButton.setDisable(true);
        endRoundButton.setDisable(true);
        endActionButton.setDisable(true);
    }

    public void enable(int action)
    {
        setDisable(action, false);
    }

    public void disable(int action)
    {
        setDisable(action, true);
    }

    private void setDisable(int action, boolean disable)
    {
        if (action == MOVE_ACTION)
        {
            moveButton.setDisable(disable);
        }
        else if (action == GRAB_ACTION)
        {
            grabButton.setDisable(disable);
        }
        else if (action == FIRE_ACTION)
        {
            fireButton.setDisable(disable);
        }
        else if (action == RELOAD_ACTION)
        {
            reloadButton.setDisable(disable);
        }
        else if (action == END_ROUND)
        {
            endRoundButton.setDisable(disable);
        }
        else if (action == END_ACTION)
        {
            endActionButton.setDisable(disable);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //setPrefHeight(GUI.getScreenHeight()/5);
        //setMaxHeight(GUI.getScreenHeight()/5);
        setMaxWidth(Double.MAX_VALUE);
        //setMinWidth(GUI.getScreenWidth()/11.4);

        System.out.println(GUI.getScreenWidth()/11.4);

        tilePane.setPrefColumns(2);
        tilePane.setPrefRows(3);

        //setBackground(new Background(new BackgroundFill(Color.MAGENTA, null, null)));

        heightProperty().addListener((observable, oldValue, newValue) ->
                tilePane.setPrefTileHeight(newValue.doubleValue()/4));

        widthProperty().addListener((observable, oldValue, newValue) ->
                tilePane.setPrefTileWidth(newValue.doubleValue()/3));


        //prefTileWidthProperty().bind(widthProperty().divide(2).subtract(getHgap()*2));

        //setPrefHeight(GUI.getMinStageHeight()/5);
        //setMaxHeight(GUI.getMinStageHeight()/5);
        //setMaxWidth(GUI.getMinStageHeight()/8);

        ImageView moveImageView = new ImageView(new Image(getClass().getResourceAsStream("/img/action_button/move.png")));
        moveImageView.setPreserveRatio(true);
        moveImageView.setFitHeight(40);

        ImageView fireImageView = new ImageView(new Image(getClass().getResourceAsStream("/img/action_button/fire.png")));
        fireImageView.setPreserveRatio(true);
        fireImageView.setFitHeight(40);

        ImageView grabImageView = new ImageView(new Image(getClass().getResourceAsStream("/img/action_button/grab.png")));
        grabImageView.setPreserveRatio(true);
        grabImageView.setFitHeight(40);

        ImageView reloadImageView = new ImageView(new Image(getClass().getResourceAsStream("/img/action_button/reload.png")));
        reloadImageView.setPreserveRatio(true);
        reloadImageView.setFitHeight(40);

        moveButton.setGraphic(moveImageView);
        fireButton.setGraphic(fireImageView);
        grabButton.setGraphic(grabImageView);
        reloadButton.setGraphic(reloadImageView);

        endActionButton.setFont(new Font(18));
        endRoundButton.setFont(new Font(18));
        endRoundButton.setTextFill(Color.DARKRED);

        /*moveButton.heightProperty().addListener((observable, oldValue, newValue) ->
        {
            endRoundButton.setMaxHeight(newValue.intValue());
            endRoundButton.setMinHeight(newValue.intValue());
        });

        moveButton.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            endRoundButton.setMaxWidth(newValue.intValue()*2 + vbox.getSpacing());
            endRoundButton.setMinWidth(newValue.intValue()*2 + vbox.getSpacing());
        });*/


    }

    @FXML
    public void onAction(ActionEvent event)
    {
        int action = RELOAD_ACTION;
        if(event.getSource().equals(moveButton))action = MOVE_ACTION;
        else if(event.getSource().equals(fireButton))action = FIRE_ACTION;
        else if(event.getSource().equals(grabButton))action = GRAB_ACTION;
        else if(event.getSource().equals(endRoundButton))action = END_ROUND;
        else if(event.getSource().equals(endActionButton))action = END_ACTION;
        if(clickListener != null)clickListener.onActionClick(action);
    }

    public interface OnActionClickListener
    {
        void onActionClick(int action);
    }
}
