package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.map.Coordinates;
import it.polimi.se2019.map.MapData;
import it.polimi.se2019.player.PlayerData;
import it.polimi.se2019.ui.gui.MatchScene;
import it.polimi.se2019.utils.constants.GameColor;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.*;


public class MapBlock extends Rectangle2D implements CardView.OnCardViewClickListener
{
    private static final EnumMap<GameColor, Image> playersPawn = loadPlayersPawn();

    private final MapView mapView;
    private boolean active;
    private Image ammoImage;
    private final Rectangle2D ammoRect;

    private List<PlayerData> players;

    private List<Rectangle2D> playerPositions;

    private final int mapX;
    private final int mapY;

    private Random random;


    public MapBlock(MapView mapView, double x, double y, double width, double height, double startXScaled, double startYScaled, Rectangle2D ammoRect)
    {
        super(x, y, width, height);
        this.mapView = mapView;

        players = new ArrayList<>();

        mapX = (int)((getMinX()-startXScaled)/getWidth());
        mapY = (int)((getMinY()-startYScaled)/getHeight());

        double scale = mapView.getScale();
        double offset = mapX > 1 ? mapView.getLeftImageWidth() : 0;
        ammoRect = new Rectangle2D((ammoRect.getMinX()*scale)+offset, ammoRect.getMinY()*scale, ammoRect.getWidth()*scale, ammoRect.getHeight()*scale);
        this.ammoRect = ammoRect;

        random = new Random();

        playerPositions = new ArrayList<>();

        computePlayerPosition();
    }

    private void computePlayerPosition()
    {
        double pawnSize = getWidth()/4;
        for(int i = 0; i < 5; i++)
        {
            playerPositions.add(getPlayerPos(pawnSize));
        }
    }

    private Rectangle2D getPlayerPos(double pawnSize)
    {
        double cx = getMinX()+getWidth()/2;
        double cy = getMinY()+getHeight()/2;
        Rectangle2D rectangle2D = new Rectangle2D(cx-pawnSize/2, cy-pawnSize/2, pawnSize, pawnSize); //center
        if(!intersectOtherRectangles(rectangle2D))return rectangle2D;
        rectangle2D = new Rectangle2D(cx-(pawnSize+pawnSize/2)-1, cy-pawnSize/2, pawnSize, pawnSize); //center-left
        if(!intersectOtherRectangles(rectangle2D))return rectangle2D;
        rectangle2D = new Rectangle2D(cx+pawnSize/2+1, cy-pawnSize/2, pawnSize, pawnSize); //center-right
        if(!intersectOtherRectangles(rectangle2D))return rectangle2D;
        rectangle2D = new Rectangle2D(cx-pawnSize/2, cy-(pawnSize+pawnSize/2)-1, pawnSize, pawnSize);//top-center
        if(!intersectOtherRectangles(rectangle2D))return rectangle2D;
        rectangle2D = new Rectangle2D(cx-pawnSize/2, cy+pawnSize/2+1, pawnSize, pawnSize);//bottom-center
        if(!intersectOtherRectangles(rectangle2D))return rectangle2D;
        rectangle2D = new Rectangle2D(cx+pawnSize/2+1, cy+pawnSize/2+1, pawnSize, pawnSize);//bottom-right
        if(!intersectOtherRectangles(rectangle2D))return rectangle2D;
        rectangle2D = new Rectangle2D(cx-(pawnSize+pawnSize/2)-1, cy+pawnSize/2+1, pawnSize, pawnSize);//bottom-left
        if(!intersectOtherRectangles(rectangle2D))return rectangle2D;
        rectangle2D = new Rectangle2D(cx-(pawnSize+pawnSize/2)-1, cy-(pawnSize+pawnSize/2)-1, pawnSize, pawnSize);//top-left
        if(!intersectOtherRectangles(rectangle2D))return rectangle2D;
        rectangle2D = new Rectangle2D(cx+pawnSize/2+1, cy-(pawnSize+pawnSize/2)-1, pawnSize, pawnSize);//top-right
        if(!intersectOtherRectangles(rectangle2D))return rectangle2D;

        else return getRandomRectangle(pawnSize, pawnSize);
    }

    private Point2D getRandomCoordinates(double widthOffset, double heightOffset)
    {
        return new Point2D(getMinX()+random.nextInt((int)(getWidth()-widthOffset)), getMinY()+random.nextInt((int)(getHeight()-heightOffset)));
    }

    private Rectangle2D getRandomRectangle(double width, double height)
    {
        Point2D coords = getRandomCoordinates(width, height);
        Rectangle2D rectangle = new Rectangle2D(coords.getX(), coords.getY(), width, height);
        while (intersectOtherRectangles(rectangle))
        {
            coords = getRandomCoordinates(width, height);
            rectangle = new Rectangle2D(coords.getX(), coords.getY(), width, height);
        }
        return rectangle;
    }

    private boolean intersectOtherRectangles(Rectangle2D rect)
    {
        if(rect.intersects(ammoRect))return true;
        for(Rectangle2D playerRect : playerPositions)
        {
            if(rect.intersects(playerRect))return true;
        }
        return false;
    }

    public void paint(GraphicsContext gc)
    {
        gc.clearRect(getMinX(), getMinY(), getWidth(), getHeight());
        paintPlayers(gc);
        if(ammoImage != null)gc.drawImage(ammoImage, ammoRect.getMinX(), ammoRect.getMinY(), ammoRect.getWidth(), ammoRect.getHeight());
        if(active)
        {
            gc.setFill(new Color(1, 1, 1, 0.2));
            gc.fillRect(getMinX(), getMinY(), getWidth(), getHeight());
        }
    }

    private void paintPlayers(GraphicsContext gc)
    {
        for(int i = 0; i < players.size(); i++)
        {
            Image pawnImage = playersPawn.get(players.get(i).getColor());
            Rectangle2D rect = playerPositions.get(i);
            gc.drawImage(pawnImage, rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
        }

        /*playerPositions.forEach(rect ->
        {
            gc.setFill(new Color(Math.random(), Math.random(), Math.random(), 1));
            gc.fillRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
        });*/
    }

    public void updatePlayers(List<PlayerData> players)
    {
        this.players = players;
        mapView.repaintBlock(this);
    }

    public boolean isActive()
    {
        return this.active;
    }

    public int getMapX()
    {
        return this.mapX;
    }

    public int getMapY()
    {
        return this.mapY;
    }

    public void setAmmoCard(Image image)
    {
        this.ammoImage = image;
        this.mapView.repaintBlock(this);
    }

    public void handle(MouseEvent event, MapData map)
    {
        if(event.getEventType().equals(MouseEvent.MOUSE_MOVED))
        {
            boolean oldActive = active;
            active = contains(new Point2D(event.getX(), event.getY())) && map != null && map.getBlocks()[mapY][mapX] != null;
            if(oldActive != active)mapView.repaintBlock(this);
        }
    }

    private static EnumMap<GameColor, Image> loadPlayersPawn()
    {
        EnumMap<GameColor, Image> playersPawn = new EnumMap<>(GameColor.class);

        playersPawn.put(GameColor.YELLOW, new Image(MapBlock.class.getResourceAsStream("/img/pawns/yellow_pawn.png")));
        playersPawn.put(GameColor.BLUE, new Image(MapBlock.class.getResourceAsStream("/img/pawns/blue_pawn.png")));
        playersPawn.put(GameColor.WHITE, new Image(MapBlock.class.getResourceAsStream("/img/pawns/white_pawn.png")));
        playersPawn.put(GameColor.PURPLE, new Image(MapBlock.class.getResourceAsStream("/img/pawns/purple_pawn.png")));
        playersPawn.put(GameColor.GREEN, new Image(MapBlock.class.getResourceAsStream("/img/pawns/green_pawn.png")));

        return playersPawn;
    }

    @Override
    public void onCardClick(CardView cardView)
    {
        MatchScene matchScene = MatchScene.getInstance();

        if(matchScene.getGameData().getPlayer().getCoordinates().equals(new Coordinates(mapX, mapY)))
        {
            matchScene.onCardClick(cardView);
        }
    }
}
