package it.polimi.se2019.utils.gui;

import it.polimi.se2019.ui.gui.components.MapBlock;
import it.polimi.se2019.utils.constants.GameColor;
import javafx.scene.image.Image;

import java.util.EnumMap;

public abstract class PlayerPawnImageFactory
{
    private static final EnumMap<GameColor, Image> playersPawn = loadPlayersPawn();

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

    public static Image getPlayerPawn(GameColor gameColor)
    {
        return playersPawn.get(gameColor);
    }
}
