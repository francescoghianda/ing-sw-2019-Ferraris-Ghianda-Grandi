package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.controller.GameData;
import it.polimi.se2019.player.GameBoardData;
import it.polimi.se2019.player.PlayerData;
import it.polimi.se2019.ui.gui.MatchScene;
import it.polimi.se2019.utils.constants.GameColor;
import javafx.beans.binding.Bindings;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.util.List;

public class PlayersInfoTable extends TableView<PlayersInfoTable.PlayerTableData>
{

    private TableColumn<PlayerTableData, String> playerColumn;
    private TableColumn<PlayerTableData, Integer> damageColumn;
    private TableColumn<PlayerTableData, Integer> marksColumn;

    public PlayersInfoTable()
    {
        playerColumn = new TableColumn<>("Player");
        damageColumn = new TableColumn<>("Danno");
        marksColumn = new TableColumn<>("Marchi");

        setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        getStylesheets().add("/css/PlayersInfoTableStyle.css");

        setRowFactory(tv -> new TableRow<PlayerTableData>()
        {
            @Override
            public void updateItem(PlayerTableData item, boolean empty)
            {
                super.updateItem(item, empty);

                if(!empty && item != null)
                {
                    styleProperty().bind(Bindings.createStringBinding(() -> "-fx-background-color: "+getPlayerColor(item.getUsername())));
                }
            }
        });

        playerColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        damageColumn.setCellValueFactory(new PropertyValueFactory<>("damage"));
        marksColumn.setCellValueFactory(new PropertyValueFactory<>("marks"));

        getColumns().addAll(playerColumn, damageColumn, marksColumn);

        widthProperty().addListener((observable, oldValue, newValue) ->
        {
            double columnWidth = newValue.doubleValue()/3d;
            playerColumn.setPrefWidth(columnWidth);
            damageColumn.setPrefWidth(columnWidth);
            marksColumn.setPrefWidth(columnWidth);
        });
    }

    private String getPlayerColor(String username)
    {
        List<PlayerData> players = MatchScene.getInstance().getGameDataProperty().get().getPlayers();

        for(PlayerData player : players)
        {
            if(player.getUsername().equals(username))
            {
                return player.getColor().getColor();
            }
        }
        return "#FFFFFF";
    }

    public void update()
    {
        GameData gameData = MatchScene.getInstance().getGameDataProperty().getValue();
        GameColor playerColor = gameData.getPlayer().getColor();
        List<PlayerData> players = gameData.getPlayers();

        getItems().clear();

        players.forEach(player ->
        {
            GameBoardData gameBoard = player.getGameBoard();
            int damage = gameBoard.getDamages().getOrDefault(playerColor, 0);
            int marks = gameBoard.getMarkers().getOrDefault(playerColor, 0);

            getItems().add(new PlayerTableData(player.getUsername(), damage, marks));
        });

        refresh();
    }

    public class PlayerTableData
    {
        private String username;
        private Integer damage;
        private Integer marks;

        PlayerTableData(String username, int damage, int marks)
        {
            this.username = username;
            this.damage = damage;
            this.marks = marks;
        }

        public String getUsername()
        {
            return username;
        }

        public Integer getDamage()
        {
            return damage;
        }

        public Integer getMarks()
        {
            return marks;
        }
    }

}
