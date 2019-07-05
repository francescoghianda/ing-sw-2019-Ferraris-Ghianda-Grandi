package it.polimi.se2019.ui.gui.components;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Containes the basic components of the gui
 *
 */
public abstract class BasicComponent extends Pane implements Initializable, EventHandler<MouseEvent>
{
    private final FXMLLoader fxmlLoader;

    public BasicComponent(String fxmlPath)
    {
        fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
    }

    protected final void load()
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
    public abstract void initialize(URL location, ResourceBundle resources);

    @Override
    public void handle(MouseEvent e)
    {

    }
}
