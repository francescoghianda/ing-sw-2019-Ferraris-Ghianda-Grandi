package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.player.PlayerData;
import it.polimi.se2019.utils.gui.PlayerPawnImageFactory;
import it.polimi.se2019.utils.gui.PointsImageFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;


public class ScoreBoardCard extends BorderPane
{


    public ScoreBoardCard(PlayerData player)
    {

        ImageView imageView = new ImageView(PlayerPawnImageFactory.getPlayerPawn(player.getColor()));
        imageView.setPreserveRatio(true);

        imageView.fitHeightProperty().bind(maxHeightProperty());

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setPadding(new Insets(0, 0, 0, 20));
        Label playerNameLabel = new Label(player.getUsername());

        playerNameLabel.setMinWidth(0);
        playerNameLabel.setTextFill(Color.WHITE);
        playerNameLabel.setFont(new Font(45));

        TilePane tilePane = new TilePane();
        tilePane.setMaxWidth(Double.MAX_VALUE);
        tilePane.setHgap(5);
        tilePane.setVgap(5);

        List<Image> pointsImageList = PointsImageFactory.getPointsImages(player.getGameBoard().getPoints());

        pointsImageList.forEach(image ->
        {
            ImageView img = new ImageView(image);
            img.setPreserveRatio(true);
            img.fitHeightProperty().bind(maxHeightProperty().multiply(0.25));
            tilePane.getChildren().add(img);
        });

        vbox.getChildren().add(playerNameLabel);

        setLeft(imageView);
        setCenter(vbox);
        setRight(tilePane);
    }



}
