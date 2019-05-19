package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.map.BlockData;
import it.polimi.se2019.map.MapData;
import it.polimi.se2019.ui.gui.GUI;
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

    private HashMap<String, Image> ammoCardImages;

    private OnBlockClickListener clickListener;

    private MapData map;
    private MapNumber mapNumber;
    private boolean initialized;

    private Image skullImage;

    private ArrayList<MapBlock> mapBlocks;

    private double scale;
    private double blockStartX;
    private double blockStartY;
    private double blockSize;
    private double blockStartXScaled;
    private double blockStartYScaled;
    private double leftImageWidth;

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
        afterScale = new ArrayList<>();
        loadAmmoCardImages();
        loadFxml();
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
        afterScale.add(task);
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
            if(scaleComputed)initMapBlocks();
            else doAfterScaleComputed(this::initMapBlocks);
        }
        this.map = map;
        updateAmmoImages();
        updatePlayers();
    }

    private void updatePlayers()
    {
        BlockData[][] blocks = map.getBlocks();

        for(int i = 0; i < blocks.length; i++)
        {
            for(int j = 0; j < blocks[i].length; j++)
            {
                if(blocks[i][j] != null && !blocks[i][j].getPlayers().isEmpty())
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
        //HashMap<Point2D, Rectangle2D> ammoMap = mapNumber.getLeftAmmoPosition();
        //ammoMap.putAll(mapNumber.getRightAmmoPosition());

        List<BlockData> blocks = map.getBlocksAsList();

        blocks.forEach(blockData ->
        {
            MapBlock mapBlock = findBlockByCoordinates(blockData.getX(), blockData.getY());
            String ammoCardId = blockData.getAmmoCardId();
            if(mapBlock != null)mapBlock.setAmmoCard(ammoCardImages.get(ammoCardId));
        });

        /*for(Point2D coords : ammoMap.keySet())
        {
            MapBlock blockPane = findBlockByCoordinates((int)coords.getX(), (int)coords.getY());
            String ammoCardId = blocks[(int)coords.getY()][(int)coords.getX()].getAmmoCardId();

            if(blockPane != null && ammoCardId != null)
            {
                blockPane.setAmmoCard(ammoCardImages.get(ammoCardId));
            }
        }*/
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
                System.out.println(ammoRect);
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


    void paintSkulls(int skulls, int deaths)
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double skullWidth = 100;
        double skullHeight = 140;

        double sx = 180;
        double sy = 95;

        for(int i = deaths; i < skulls+deaths; i++)
        {
            gc.clearRect((sx+skullWidth*i*1.07)*scale, sy*scale, skullWidth*scale, skullHeight*scale);
            gc.drawImage(skullImage, (sx+skullWidth*i*1.07)*scale, sy*scale, skullWidth*scale, skullHeight*scale);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        initialized = true;

        leftImage.setPreserveRatio(true);
        rightImage.setPreserveRatio(true);

        setMapNumber(MapNumber.MAP_1);

        double height = GUI.getScreenHeight()/1.4;

        leftImage.setFitHeight(height);
        rightImage.setFitHeight(height);

        canvas.setOnMouseMoved(this);
        canvas.setOnMouseClicked(this);

        hBox.widthProperty().addListener((observable, oldValue, newValue) ->
                canvas.setWidth(leftImage.getBoundsInParent().getWidth()+rightImage.getBoundsInParent().getWidth()));

        hBox.heightProperty().addListener((observable, oldValue, newValue) ->
                {
                    canvas.setHeight(newValue.doubleValue());
                    scale = canvas.getHeight()/leftImage.getImage().getHeight();
                    scaleComputed = true;
                    blockStartXScaled = blockStartX*scale;
                    blockStartYScaled = blockStartY*scale;
                    leftImageWidth = leftImage.getImage().getWidth()*scale;

                    afterScale.forEach(Runnable::run);

                    System.out.println("SCALE COMPUTED");
                    paintSkulls(4, 4);
                });

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
        paintSkulls(remainingSkulls, deaths);
    }

    @Override
    public void handle(MouseEvent event)
    {
        if(event.getEventType().equals(MouseEvent.MOUSE_CLICKED))
        {
            for(MapBlock mapBlock : mapBlocks)
            {
                if(mapBlock.isActive())
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