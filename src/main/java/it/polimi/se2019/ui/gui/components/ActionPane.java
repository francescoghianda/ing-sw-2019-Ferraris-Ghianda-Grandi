package it.polimi.se2019.ui.gui.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ActionPane extends TilePane implements Initializable
{
    @FXML
    private Button moveButton;

    @FXML
    private Button fireButton;

    @FXML
    private Button grabButton;

    @FXML
    private Button reloadButton;

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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        moveButton.setDisable(true);
        fireButton.setDisable(true);
        grabButton.setDisable(true);
        reloadButton.setDisable(true);
    }
}
