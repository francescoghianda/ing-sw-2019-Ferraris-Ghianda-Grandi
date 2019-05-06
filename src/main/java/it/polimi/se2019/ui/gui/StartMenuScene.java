package it.polimi.se2019.ui.gui;

import javafx.animation.FadeTransition;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;


public class StartMenuScene extends Scene
{
    private static final int widthScale = 6;
    private static final int heightScale = 3;

    private Button searchServerBtn;
    private Label ipLabel;
    private Label portLabel;
    private Label modeLabel;
    private TextField ipTextFiled;
    private TextField portTextFiled;
    private RadioButton socketMode;
    private RadioButton rmiMode;
    private ToggleGroup toggleGroup;

    public StartMenuScene(int screenWidth, int screenHeight)
    {
        super(new BorderPane(), 600, 403);
        getStylesheets().add("css/StartMenuStyle.css");

        BorderPane layout = (BorderPane)getRoot();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setAlignment(Pos.BOTTOM_CENTER);

        ipLabel = new Label("IP:");
        ipLabel.getStyleClass().add("outline");
        portLabel = new Label("Porta:");
        portLabel.getStyleClass().add("outline");

        GridPane.setHalignment(ipLabel, HPos.RIGHT);
        GridPane.setHalignment(portLabel, HPos.RIGHT);

        ipTextFiled = new TextField();
        ipTextFiled.setMinSize(200, ipTextFiled.getMinHeight());

        portTextFiled = new TextField();
        portTextFiled.setMinSize(200, portTextFiled.getMinHeight());

        modeLabel = new Label("Modalità:");
        modeLabel.getStyleClass().add("outline");

        toggleGroup = new ToggleGroup();
        socketMode = new RadioButton("Socket");
        socketMode.getStyleClass().add("outline");
        socketMode.setSelected(true);
        rmiMode = new RadioButton("RMI");

        socketMode.setToggleGroup(toggleGroup);
        rmiMode.setToggleGroup(toggleGroup);

        HBox modeMenu = new HBox();
        modeMenu.setSpacing(10);
        modeMenu.getChildren().addAll(socketMode, rmiMode);

        VBox btnBox = new VBox();
        btnBox.setMinHeight(150);
        btnBox.setAlignment(Pos.CENTER);
        searchServerBtn = new Button("Cerca server");
        btnBox.getChildren().add(searchServerBtn);

        gridPane.addRow(0, ipLabel, ipTextFiled);
        gridPane.addRow(1, portLabel, portTextFiled);
        gridPane.addRow(2, modeLabel, modeMenu);

        layout.setCenter(gridPane);
        layout.setBottom(btnBox);

    }
}
