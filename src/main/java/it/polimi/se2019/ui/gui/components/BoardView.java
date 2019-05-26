package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.player.GameBoardData;
import it.polimi.se2019.player.PlayerData;
import it.polimi.se2019.ui.gui.GUI;
import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.gui.BloodDropImageFactory;
import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.BinaryOperator;

public class BoardView extends StackPane implements Initializable
{
    @FXML
    private StackPane pane;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private Canvas canvas;

    private GameColor color;

    private PlayerData player;
    private GameBoardData gameBoard;

    private double scale;

    private static EnumMap<GameColor, Image> backgrounds;

    private Image background;


    private BloodDropImageFactory bloodFactory;

    public BoardView(@NamedArg("color")GameColor color)
    {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/board.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        loadBackGrounds();

        bloodFactory = BloodDropImageFactory.getInstance();

        this.color = color;
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

    public void setColor(GameColor color)
    {
        this.color = color;
        this.background = backgrounds.get(color);

        backgroundImage.setImage(background);

        backgroundImage.setPreserveRatio(true);
        backgroundImage.setFitHeight(GUI.getScreenHeight()/5);
        setPrefHeight(GUI.getScreenHeight()/5);
        setPrefWidth(backgroundImage.getBoundsInParent().getWidth());

        canvas.setHeight(backgroundImage.getFitHeight());
        canvas.setWidth(getPrefWidth());

        scale = canvas.getHeight()/background.getHeight();
    }

    private void paint()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        paintAmmo(gc);
        paintDamages(gc);
        paintMarkers(gc);
    }

    private void paintDamages(GraphicsContext gc)
    {
        LinkedHashMap<GameColor, Integer> damages = gameBoard.getDamages();

        double x = 110*scale;
        double y = 114*scale;
        double space1 = 27*scale;
        double space2 = 33*scale;

        //
        damages.put(GameColor.GREEN, 3);
        damages.put(GameColor.WHITE, 4);
        damages.put(GameColor.PURPLE, 4);
        //

        Set<GameColor> keys = damages.keySet();

        final double width = 35*scale;
        final double height = 50*scale;

        double totSpace = 0;
        int i = 0;
        for(GameColor key : keys)
        {
            int blood = damages.get(key);
            for(int j = 0; j < blood; j++, i++)
            {
                double space = space1;
                if(i == 2 || i == 5 || i == 10)space = space2;
                if(i == 0)space = 0;
                Image image = bloodFactory.getBloodDropImage(key);
                gc.drawImage(image, x+width*i+totSpace+space, y, width, height);
                totSpace += space;
            }
        }
    }

    private void paintMarkers(GraphicsContext gc)
    {
        LinkedHashMap<GameColor, Integer> markers = gameBoard.getMarkers();

        double xi = 542*scale;
        double yi = 0;
        double space = 8*scale;

        //
        markers.put(GameColor.BLUE, 2);
        markers.put(GameColor.YELLOW, 3);
        markers.put(GameColor.PURPLE, 2);
        //

        Set<GameColor> keys = markers.keySet();

        final double width = 35*scale;
        final double height = 50*scale;

        if(markers.values().stream().reduce(0, Integer::sum) <= 7)
        {
            int i = 0;
            for(GameColor key : keys)
            {
                int blood = markers.get(key);
                for(int j = 0; j < blood; j++, i++)
                {
                    Image image = bloodFactory.getBloodDropImage(key);
                    gc.drawImage(image, xi+width*i+space*i, yi, width, height);
                }
            }
        }
        else
        {
            gc.setFont(new Font(height/4.2));
            int i = 0;
            for(GameColor key : keys)
            {
                int blood = markers.get(key);
                Image image = bloodFactory.getBloodDropImage(key);
                double x = xi+width*i+space*i;
                gc.drawImage(image, x, yi, width, height);
                gc.setFill(Color.BLACK);

                Text text = new Text("x"+blood);
                text.setFont(gc.getFont());
                double textWidth = text.getBoundsInParent().getWidth();
                gc.fillText("x"+blood, x+width/2-textWidth/2, yi+height/1.5);
                i++;
            }
        }
    }


    private void paintAmmo(GraphicsContext gc)
    {
        double xi = 890*scale;
        double space = canvas.getWidth()/15;

        paintAmmoLine(gc, Color.web(GameColor.RED.getColor()), gameBoard.getRedAmmo(), xi);
        xi += space;
        paintAmmoLine(gc, Color.ROYALBLUE, gameBoard.getBlueAmmo(), xi);
        xi += space;
        paintAmmoLine(gc, Color.web(GameColor.YELLOW.getColor()), gameBoard.getYellowAmmo(), xi);
    }

    private void paintAmmoLine(GraphicsContext gc, Color color, int ammo, double xi)
    {
        double size = canvas.getHeight()/5;
        double yi = 45*scale;
        double space = size/7;

        for(int i = 0; i < ammo; i++)
        {
            double y = yi+size*i+space*i;
            gc.setFill(color);
            gc.fillRect(xi, y, size, size);
            gc.setFill(Color.BLACK);
            gc.strokeRect(xi, y, size, size);
        }
    }

    public void update(PlayerData player)
    {
        this.player = player;
        this.gameBoard = player.getGameBoard();

        paint();
    }

    public GameColor getColor()
    {
        return this.color;
    }

    private static void loadBackGrounds()
    {
        if(backgrounds == null || backgrounds.isEmpty())
        {
            backgrounds = new EnumMap<>(GameColor.class);
            String path = "/img/boards/";
            backgrounds.put(GameColor.BLUE, new Image(BoardView.class.getResourceAsStream(path+"blue/board.png")));
            backgrounds.put(GameColor.GREEN, new Image(BoardView.class.getResourceAsStream(path+"green/board.png")));
            backgrounds.put(GameColor.PURPLE, new Image(BoardView.class.getResourceAsStream(path+"purple/board.png")));
            backgrounds.put(GameColor.WHITE, new Image(BoardView.class.getResourceAsStream(path+"white/board.png")));
            backgrounds.put(GameColor.YELLOW, new Image(BoardView.class.getResourceAsStream(path+"yellow/board.png")));
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        /*this.background = backgrounds.get(color);

        backgroundImage.setImage(background);

        backgroundImage.setPreserveRatio(true);
        backgroundImage.setFitHeight(GUI.getScreenHeight()/5);
        setPrefHeight(GUI.getScreenHeight()/5);
        setPrefWidth(backgroundImage.getBoundsInParent().getWidth());*/
    }
}
