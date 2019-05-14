package it.polimi.se2019.ui.gui;

import it.polimi.se2019.ui.gui.components.BoardView;
import it.polimi.se2019.ui.gui.components.CardView;
import it.polimi.se2019.ui.gui.components.MapNumber;
import it.polimi.se2019.ui.gui.components.MapView;
import it.polimi.se2019.utils.logging.Logger;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MatchScene extends GridPane implements Initializable, EventHandler<KeyEvent>
{

    @FXML
    private BoardView board;

    @FXML
    private MapView mapView;

    private int map;

    public MatchScene()
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
    }



    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setStyle("-fx-background-image: url('/img/texture_background.png')");

        CardView card = new CardView();

        mapView.setMap(MapNumber.MAP_1);
        

        SceneManager.getInstance().getStage().addEventHandler(KeyEvent.KEY_PRESSED, this);


    }

    @Override
    public void handle(KeyEvent event)
    {
        if(event.getEventType().equals(KeyEvent.KEY_PRESSED) && event.getCode().isArrowKey())
        {
            if(event.getCode() == KeyCode.DOWN)
            {
                map--;
                if(map < 0)map = 4;
            }
            else if(event.getCode() == KeyCode.UP)
            {
                map++;
                if(map > 4)map = 0;
            }
            mapView.setMap(map);
            mapView.paint();
        }
    }
}
