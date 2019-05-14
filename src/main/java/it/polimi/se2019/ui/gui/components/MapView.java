package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.ui.gui.GUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class MapView extends StackPane implements Initializable
{

    @FXML
    private ImageView leftImage;

    @FXML
    private ImageView rightImage;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Canvas canvas;

    @FXML
    private HBox hBox;


    private MapNumber map;
    private boolean initialized;

    public MapView()
    {
        loadFxml();
    }

    public void setMap(MapNumber map)
    {
        this.map = map;
        if(initialized)
        {
            this.leftImage.setImage(map.getLeftImage());
            this.rightImage.setImage(map.getRightImage());
        }
    }

    public void setMap(int map)
    {
        switch (map)
        {
            case 2:
                setMap(MapNumber.MAP_2);
                break;
            case 3:
                setMap(MapNumber.MAP_3);
                break;
            case 4:
                setMap(MapNumber.MAP_4);
                break;
            default:
                setMap(MapNumber.MAP_1);
        }
    }

    public void paint()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.CYAN);

        double scale = canvas.getHeight()/leftImage.getImage().getHeight();

        //AMMO CARD DRAW TEST
        Collection<Rectangle2D> coords = map.getLeftAmmoPosition().values();

        for(Rectangle2D rect : coords)
        {
            gc.fillRect(rect.getMinX()*scale, rect.getMinY()*scale, rect.getWidth()*scale, rect.getHeight()*scale);
        }

        coords = map.getRightAmmoPosition().values();

        double leftWidth = leftImage.getImage().getWidth();

        for(Rectangle2D rect : coords)
        {
            gc.fillRect((rect.getMinX()+leftWidth)*scale, rect.getMinY()*scale, rect.getWidth()*scale, rect.getHeight()*scale);
        }

        //MAP BLOCK DRAW TEST
        int xm = 410;
        int ym = 425;
        int size = 420;

        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                gc.setFill(Color.color(Math.random(), Math.random(), Math.random(), 0.5));
                gc.fillRect((xm+size*i*1.05)*scale, (ym+size*j*1.03)*scale, size*scale, size*scale);
            }
        }

        //SKULL DRAW TEST
        double skullWidth = 100;
        double skullHeight = 140;

        double sx = 180;
        double sy = 95;

        for(int i = 0; i < 8; i++)
        {
            gc.setFill(Color.color(Math.random(), Math.random(), Math.random(), 0.5));
            gc.fillRect((sx+skullWidth*i*1.07)*scale, sy*scale, skullWidth*scale, skullHeight*scale);
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        initialized = true;

        leftImage.setPreserveRatio(true);
        rightImage.setPreserveRatio(true);

        double height = GUI.getScreenHeight()/1.4;

        leftImage.setFitHeight(height);
        rightImage.setFitHeight(height);


        hBox.widthProperty().addListener((observable, oldValue, newValue) ->
                {
                    canvas.setWidth(leftImage.getBoundsInParent().getWidth()+rightImage.getBoundsInParent().getWidth());
                    paint();
                });

        hBox.heightProperty().addListener((observable, oldValue, newValue) ->
                {
                    canvas.setHeight(newValue.doubleValue());
                    paint();
                });

        if(map != null)
        {
            this.leftImage.setImage(map.getLeftImage());
            this.rightImage.setImage(map.getRightImage());
        }
    }

    private void loadFxml()
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mapView.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
