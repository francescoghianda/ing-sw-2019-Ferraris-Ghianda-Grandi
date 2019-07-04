package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.CardData;
import it.polimi.se2019.controller.Death;
import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.map.BlockData;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.map.MapData;
import it.polimi.se2019.ui.gui.GUI;
import it.polimi.se2019.ui.gui.MatchScene;
import it.polimi.se2019.utils.gui.BloodDropImageFactory;
import it.polimi.se2019.utils.logging.Logger;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MapView extends StackPane implements Initializable, EventHandler<MouseEvent>
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

    @FXML
    private Pane pane;

    private HashMap<String, Image> ammoCardImages;

    private OnBlockClickListener clickListener;

    private MapData map;
    private MapNumber mapNumber;
    private boolean initialized;

    private Image skullImage;
    private Image powerUpDeckImage;
    private Image weaponDeckImage;

    private ArrayList<MapBlock> mapBlocks;

    private volatile double scale;
    private double blockStartX;
    private double blockStartY;
    private double blockSize;
    private double blockStartXScaled;
    private double blockStartYScaled;
    private double leftImageWidth;
    private double rightImageWidth;

    private boolean scaleComputed;
    private final List<Runnable> afterScale;

    public MapView()
    {
        blockStartX = 410;
        blockStartY = 425;
        blockSize = 420;
        scale = 1.0;
        ammoCardImages = new HashMap<>();
        skullImage = new Image(getClass().getResourceAsStream("/img/skull.png"));
        powerUpDeckImage = new Image(getClass().getResourceAsStream("/img/powerups/AD_powerups_IT_02.png"));
        weaponDeckImage = new Image(getClass().getResourceAsStream("/img/weapons/AD_weapons_IT_02.png"));
        afterScale = new ArrayList<>();
        loadAmmoCardImages();
        loadFxml();
    }

    public void enableBlocks(ArrayList<Coordinates> coordinates)
    {
        mapBlocks.forEach(block -> block.setEnabled(coordinates.contains(block.getCoordinates())));
    }

    public void enableBlocks(int maxDistanceFromPlayer)
    {
        MatchScene matchScene = MatchScene.getInstance();
        int playerX = matchScene.getGameDataProperty().getValue().getPlayer().getX();
        int playerY = matchScene.getGameDataProperty().getValue().getPlayer().getY();
        BlockData playerBlock = matchScene.getGameDataProperty().getValue().getMap().getBlock(playerX, playerY);
        if(playerBlock == null)return;

        mapBlocks.forEach(block ->
        {
            BlockData blockData = matchScene.getGameDataProperty().getValue().getMap().getBlock(block.getMapX(), block.getMapY());
            int distance = matchScene.getGameDataProperty().getValue().getMap().getDistance(playerBlock, blockData);
            block.setEnabled(distance <= maxDistanceFromPlayer);
        });
    }

    public void setColored(boolean colored)
    {
        mapBlocks.forEach(block -> block.setColored(colored));
    }

    private void setMapNumber(MapNumber mapNumber)
    {
        this.mapNumber = mapNumber;
        if(initialized)
        {
            this.leftImage.setImage(mapNumber.getLeftImage());
            this.rightImage.setImage(mapNumber.getRightImage());
        }

        this.mapBlocks = new ArrayList<>();
    }

    private void setMapNumber(int map)
    {
        switch (map)
        {
            case 2:
                setMapNumber(MapNumber.MAP_2);
                break;
            case 3:
                setMapNumber(MapNumber.MAP_3);
                break;
            case 4:
                setMapNumber(MapNumber.MAP_4);
                break;
            default:
                setMapNumber(MapNumber.MAP_1);
        }
    }

    private void doAfterScaleComputed(Runnable task)
    {
        if(scaleComputed)task.run();
        else afterScale.add(task);
    }

    private void repaint()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        paintDecks();

        mapBlocks.forEach(this::repaintBlock);

    }

    double getScale()
    {
        return scale;
    }

    public void update(MapData map)
    {
        if(this.map == null)
        {
            setMapNumber(map.getMapNumber());
            this.map = map;
            doAfterScaleComputed(this::initMapBlocks);
        }
        this.map = map;
        doAfterScaleComputed(this::updateAmmoImages);
        doAfterScaleComputed(this::updateWeapons);
        doAfterScaleComputed(this::updatePlayers);
        doAfterScaleComputed(this::paintDecks);
    }

    private void paintDecks()
    {
        GameData gameData = MatchScene.getInstance().getGameDataProperty().getValue();

        if(gameData == null)return;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        double pwuX = 1086*scale + getLeftImageWidth();
        double pwuY = 115*scale;
        double pwuWidth = 177*scale;
        double pwuHeight = 256*scale;

        double wpX = 1020*scale + getLeftImageWidth();
        double wpY = 523*scale;
        double wpWidth = 242*scale;
        double wpHeight = 402*scale;

        paintDeck(gc, powerUpDeckImage, pwuX, pwuY, pwuWidth, pwuHeight, gameData.getPowerUpDeckSize());
        paintDeck(gc, weaponDeckImage, wpX, wpY, wpWidth, wpHeight, gameData.getWeaponDeckSize());
    }

    private void paintDeck(GraphicsContext gc, Image image, double x, double y, double width, double height, int size)
    {
        gc.clearRect(x-2, y-100*scale, width+2, height+100*scale);
        gc.setFill(Color.BLACK);
        for(int i = 0; i < size; i++)
        {
            gc.drawImage(image, x, y-2*i, width, height);
            gc.strokeRoundRect(x, y-2*i, width, height, 10, 10);
        }
    }

    private void updateWeapons()
    {
        pane.getChildren().clear();

        map.getBlocksAsList().forEach(block ->
        {
            if(block.isSpawnPoint())
            {
                List<CardData> weapons = block.getWeaponCards();
                for(int i = 0; i < weapons.size(); i++)
                {
                    CardView cardView = new CardView(weapons.get(i), CardView.FADE_TRANSITION);
                    cardView.setOnCardViewClickListener(getBlock(block.getX(), block.getY()));

                    cardView.setMaxWidth(237*scale);

                    if(block.getX() == 0 && block.getY() == 1)
                    {
                        cardView.setLayoutX(0);
                        cardView.setLayoutY((702*scale)+40*scale*i+cardView.getMaxWidth()*i+cardView.getMaxWidth());
                        cardView.getTransforms().add(new Rotate(-90, 0, 0));
                    }
                    else if(block.getY() == 0 && block.getX() == 2)
                    {
                        cardView.setLayoutX(getLeftImageWidth()+40*scale*i+150*scale+cardView.getMaxWidth()*i);
                        cardView.setLayoutY(0);
                    }
                    else
                    {
                        cardView.setLayoutX(getLeftImageWidth()+rightImageWidth);
                        cardView.setLayoutY(1100*scale+40*scale*i+cardView.getMaxWidth()*i);
                        cardView.getTransforms().add(new Rotate(90, 0, 0));
                    }

                    pane.getChildren().add(cardView);
                }
            }
        });
    }

    private void updatePlayers()
    {
        BlockData[][] blocks = map.getBlocks();

        for(int i = 0; i < blocks.length; i++)
        {
            for(int j = 0; j < blocks[i].length; j++)
            {
                if(blocks[i][j] != null)
                {
                    MapBlock mapBlock = getBlock(j, i);
                    if(mapBlock != null)mapBlock.updatePlayers(blocks[i][j].getPlayers());
                }
            }
        }
    }

    private MapBlock getBlock(int x, int y)
    {
        for(MapBlock block : mapBlocks)
        {
            if(block.getMapX() == x && block.getMapY() == y)return block;
        }
        return null;
    }

    private void updateAmmoImages()
    {
        List<BlockData> blocks = map.getBlocksAsList();

        blocks.forEach(blockData ->
        {
            MapBlock mapBlock = findBlockByCoordinates(blockData.getX(), blockData.getY());
            String ammoCardId = blockData.getAmmoCardId();
            if(mapBlock != null)mapBlock.setAmmoCard(ammoCardImages.get(ammoCardId));
        });
    }

    private void initMapBlocks()
    {
        if(map == null)return;
        mapBlocks.clear();
        double xm = blockStartX;
        double ym = blockStartY;
        double size = blockSize;

        HashMap<Point2D, Rectangle2D> ammoMap = mapNumber.getLeftAmmoPosition();
        ammoMap.putAll(mapNumber.getRightAmmoPosition());

        BlockData[][] blocks = map.getBlocks();

        for(int i = 0; i < blocks.length; i++)
        {
            for(int j = 0; j < blocks[i].length; j++)
            {
                if(blocks[i][j] == null)continue;
                Rectangle2D ammoRect = getAmmoRect(ammoMap, j, i);
                mapBlocks.add(new MapBlock(this, (xm + size * j * 1.05) * scale, (ym + size * i * 1.03) * scale, size * scale, size * scale, blockStartXScaled, blockStartYScaled, ammoRect));
            }
        }
    }

    private Rectangle2D getAmmoRect(HashMap<Point2D, Rectangle2D> rects, int x, int y)
    {
        Set<Point2D> keys = rects.keySet();
        for(Point2D key : keys)
        {
            if(key.distance(x, y) == 0)return rects.get(key);
        }
        return new Rectangle2D(0, 0, 0, 0);
    }

    double getLeftImageWidth()
    {
        return leftImageWidth;
    }

    private MapBlock findBlockByCoordinates(int x, int y)
    {
        for(MapBlock block : mapBlocks)
        {
            if(block.getMapX() == x && block.getMapY() == y)return block;
        }
        return null;
    }

    public void setOnBlockClickListener(OnBlockClickListener listener)
    {
        this.clickListener = listener;
    }


    void repaintBlock(MapBlock block)
    {
        block.paint(canvas.getGraphicsContext2D());
    }


    private void paintSkulls(int skulls, int deathsNumber)
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double skullWidth = 100;
        double skullHeight = 140;

        double sx = 180;
        double sy = 95;

        List<Death> deaths = MatchScene.getInstance().getGameDataProperty().get().getDeaths();

        for(int i = 0; i < deaths.size(); i++)
        {
            Image bloodDropImage = BloodDropImageFactory.getInstance().getBloodDropImage(deaths.get(i).getKillerColor());
            clearAndDraw(bloodDropImage, gc, (sx+skullWidth*i*1.07)*scale, sy*scale, skullWidth*scale, skullHeight*scale);
            if(deaths.get(i).isOverKill())
            {
                gc.drawImage(bloodDropImage, (sx+skullWidth*i*1.07)*scale, sy*scale-20*scale, skullWidth*scale, skullHeight*scale);
            }
        }

        for(int i = deaths.size(); i < skulls+deaths.size(); i++)
        {
            clearAndDraw(skullImage, gc, (sx+skullWidth*i*1.07)*scale, sy*scale, skullWidth*scale, skullHeight*scale);
        }
    }

    private void clearAndDraw(Image image, GraphicsContext gc, double x, double y, double width, double height)
    {
        gc.clearRect(x, y, width, height);
        gc.drawImage(image, x, y, width, height);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        initialized = true;

        setMinHeight(0);
        hBox.setMinHeight(0);

        leftImage.setPreserveRatio(true);
        rightImage.setPreserveRatio(true);

        setMapNumber(MapNumber.MAP_1);

        double height = GUI.getScreenHeight()/1.4;
        //double height = GUI.getMinStageHeight()/1.4;

        leftImage.setFitHeight(height);
        rightImage.setFitHeight(height);

        canvas.setOnMouseMoved(this);
        canvas.setOnMouseClicked(this);

        setPickOnBounds(false);
        pane.setPickOnBounds(false);

        //setBackground(new Background(new BackgroundFill(Color.AQUA, null, null)));

        //hBox.setBackground(new Background(new BackgroundFill(Color.BLANCHEDALMOND, null, null)));

        hBox.heightProperty().addListener((observable, oldValue, newValue) ->
        {
            //System.out.println("Map hBox height = "+newValue.doubleValue());

            leftImage.setFitHeight(newValue.doubleValue());
            rightImage.setFitHeight(newValue.doubleValue());

        });

        hBox.widthProperty().addListener((observable, oldValue, newValue) ->
                canvas.setWidth(leftImage.getBoundsInParent().getWidth()+rightImage.getBoundsInParent().getWidth()));

        hBox.heightProperty().addListener((observable, oldValue, newValue) ->
                {
                    canvas.setWidth(leftImage.getBoundsInParent().getWidth()+rightImage.getBoundsInParent().getWidth());
                    canvas.setHeight(newValue.doubleValue());
                    scale = canvas.getHeight()/leftImage.getImage().getHeight();
                    scaleComputed = true;
                    blockStartXScaled = blockStartX*scale;
                    blockStartYScaled = blockStartY*scale;
                    leftImageWidth = leftImage.getImage().getWidth()*scale;
                    rightImageWidth = rightImage.getImage().getWidth()*scale;
                    Logger.debug("SCALE COMPUTED");
                    afterScale.forEach(Runnable::run);
                    paintSkulls(4, 4);
                    repaint();
                });


        pane.maxWidthProperty().bind(canvas.widthProperty());
        //pane.prefWidthProperty().bind(canvas.widthProperty());
        //pane.prefHeightProperty().bind(canvas.heightProperty());

        if(mapNumber != null)
        {
            this.leftImage.setImage(mapNumber.getLeftImage());
            this.rightImage.setImage(mapNumber.getRightImage());
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

    public void setRemainingSkulls(int remainingSkulls, int deaths)
    {
        doAfterScaleComputed(() -> Platform.runLater(() -> paintSkulls(remainingSkulls, deaths)));
    }

    @Override
    public void handle(MouseEvent event)
    {
        if(event.getEventType().equals(MouseEvent.MOUSE_CLICKED))
        {
            for(MapBlock mapBlock : mapBlocks)
            {
                if(mapBlock.isActive() && mapBlock.isEnabled())
                {
                    clickListener.onBlockClick(mapBlock.getMapX(), mapBlock.getMapY());
                    break;
                }
            }
        }
        else mapBlocks.forEach(block->block.handle(event, map));

    }

    private void loadAmmoCardImages()
    {
        String idPrefix = "AMC";

        try
        {
            for(int i = 2; i < 38; i++)
            {
                ammoCardImages.put(idPrefix+i, new Image(getClass().getResourceAsStream("/img/ammocards/AD_ammo_04"+i+".png")));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public interface OnBlockClickListener
    {
        void onBlockClick(int blockX, int blockY);
    }
}
