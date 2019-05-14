package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.ui.gui.GUI;
import it.polimi.se2019.utils.constants.GameColor;
import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

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
        this.background = backgrounds.get(color);
        backgroundImage.setImage(background);

        backgroundImage.setPreserveRatio(true);
        backgroundImage.setFitHeight(GUI.getScreenHeight()/5);
    }
}
