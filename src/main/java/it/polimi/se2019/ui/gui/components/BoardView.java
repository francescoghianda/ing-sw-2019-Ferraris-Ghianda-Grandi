package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.player.GameBoardData;
import it.polimi.se2019.player.Player;
import it.polimi.se2019.player.PlayerData;
import it.polimi.se2019.ui.gui.GUI;
import it.polimi.se2019.ui.gui.MatchScene;
import it.polimi.se2019.utils.constants.GameColor;
import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.ResourceBundle;

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

    public BoardView(@NamedArg("color")GameColor color)
    {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/board.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        loadBackGrounds();
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

        System.out.println("h"+canvas.getHeight());
    }

    private void paint()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        paintAmmo(gc);
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
