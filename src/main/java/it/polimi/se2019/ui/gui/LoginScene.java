package it.polimi.se2019.ui.gui;

import it.polimi.se2019.ui.gui.value.Value;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginScene extends BorderPane implements Initializable
{

    @FXML
    private Pane formBg;

    @FXML
    private TextField usernameTextFiled;

    @FXML
    private GridPane gridPane;

    private Label messageLabel;

    public static final Value<String> username = new Value<>();

    LoginScene()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginScene.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        messageLabel = new Label("Username non valido!");
        messageLabel.setStyle("-fx-text-fill: red");

        load(loader);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        getStylesheets().add("css/StartMenuStyle.css");
        formBg.setStyle("-fx-background-color: rgba(255,255,255,0.6);"+"-fx-background-radius: 20 20 20 20");
    }

    public void reset()
    {
        usernameTextFiled.setText("");
    }

    void invalidUsername()
    {
        formBg.setMaxHeight(70);

        if(!gridPane.getChildren().contains(messageLabel))
        {
            gridPane.add(messageLabel, 1, 1);
        }
    }

    @FXML
    public void confirm(ActionEvent event)
    {
        if(usernameTextFiled.getText().isEmpty())
        {
            invalidUsername();
            return;
        }

        username.set(usernameTextFiled.getText());
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

}