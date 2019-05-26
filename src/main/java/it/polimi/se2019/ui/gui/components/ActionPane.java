package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.ui.gui.GUI;
import it.polimi.se2019.utils.constants.GameColor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ActionPane extends TilePane implements Initializable
{
    public static final int MOVE_ACTION = 0;
    public static final int FIRE_ACTION = 1;
    public static final int GRAB_ACTION = 2;
    public static final int RELOAD_ACTION = 3;

    @FXML
    private ColoredButton moveButton;

    @FXML
    private ColoredButton fireButton;

    @FXML
    private ColoredButton grabButton;

    @FXML
    private ColoredButton reloadButton;

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
    }

    public void disableAll()
    {
        moveButton.setDisable(true);
        fireButton.setDisable(true);
        grabButton.setDisable(true);
        reloadButton.setDisable(true);
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
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setPrefHeight(GUI.getScreenHeight()/5);

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
    }

    @FXML
    public void onAction(ActionEvent event)
    {
        int action = RELOAD_ACTION;
        if(event.getSource().equals(moveButton))action = MOVE_ACTION;
        else if(event.getSource().equals(fireButton))action = FIRE_ACTION;
        else if(event.getSource().equals(grabButton))action = GRAB_ACTION;
        if(clickListener != null)clickListener.onActionClick(action);
    }

    public interface OnActionClickListener
    {
        void onActionClick(int action);
    }
}
