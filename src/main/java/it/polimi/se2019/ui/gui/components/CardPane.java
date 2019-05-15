package it.polimi.se2019.ui.gui.components;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CardPane extends StackPane implements Initializable
{

    public CardPane()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/cardPane.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        load(fxmlLoader);
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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }
}
