package it.polimi.se2019.ui.gui;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class LoginScene extends Scene implements EventHandler<MouseEvent>, InputHandler<String>
{
    public static final String INPUT_USERNAME = "username";

    private String username;

    private Label messageLabel;
    private Label usernameLabel;
    private TextField usernameTextFiled;
    private Button loginButton;
    private BorderPane formBg;
    private GridPane gridPane;


    public LoginScene()
    {
        super(new BorderPane(), 600, 403);
        getStylesheets().add("css/StartMenuStyle.css");

        BorderPane layout = (BorderPane)getRoot();
        formBg = new BorderPane();
        formBg.setStyle("-fx-background-color: rgba(255,255,255,0.6);"+"-fx-background-radius: 20 20 20 20");
        formBg.setMaxWidth(400);
        formBg.setMaxHeight(50);

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);

        messageLabel = new Label("Username non valido!");
        messageLabel.setStyle("-fx-text-fill: red");
        usernameLabel = new Label("Username:");
        usernameTextFiled = new TextField();
        loginButton = new Button("Login");

        gridPane.addRow(0, usernameLabel, usernameTextFiled);
        formBg.setCenter(gridPane);

        layout.setCenter(formBg);

        HBox bottomBox = new HBox(loginButton);
        bottomBox.setMinHeight(100);
        bottomBox.setAlignment(Pos.CENTER);

        layout.setBottom(bottomBox);

        loginButton.setOnMouseClicked(this);

    }

    public LoginScene reset()
    {
        username = null;
        usernameTextFiled.setText("");
        return this;
    }

    public String getUsername()
    {
        if(username == null || username.isEmpty())return null;
        return username;
    }

    public void invalidUsername()
    {
        username = null;
        formBg.setMaxHeight(70);
        if(!gridPane.getChildren().contains(messageLabel))gridPane.add(messageLabel, 1, 1);
    }

    @Override
    public void handle(MouseEvent event)
    {
        if(usernameTextFiled.getText().isEmpty())
        {
            invalidUsername();
            return;
        }
        this.username = usernameTextFiled.getText();
        Input.notifyAllInputs();
    }

    @Override
    public String getInput(String inputName)
    {
        if(inputName.equals(INPUT_USERNAME))return getUsername();
        return null;
    }
}
