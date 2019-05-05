package it.polimi.se2019.ui.gui;


import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;


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
        super(new GridPane(), screenWidth/widthScale, screenHeight/heightScale);
        getStylesheets().add("css/StartMenuStyle.css");

        GridPane layout = (GridPane)getRoot();

        layout.setAlignment(Pos.CENTER);
        layout.setVgap(20);
        layout.setHgap(30);

        ipLabel = new Label("IP:");
        ipTextFiled = new TextField();
        ipTextFiled.setMinSize(200, ipTextFiled.getMinHeight());

        portLabel = new Label("Porta:");
        portTextFiled = new TextField();
        portTextFiled.setMinSize(200, portTextFiled.getMinHeight());

        modeLabel = new Label("Modalità di connessione:");

        toggleGroup = new ToggleGroup();
        socketMode = new RadioButton("Socket");
        socketMode.setSelected(true);
        rmiMode = new RadioButton("RMI");

        socketMode.setToggleGroup(toggleGroup);
        rmiMode.setToggleGroup(toggleGroup);

        HBox modeMenu = new HBox();
        modeMenu.setSpacing(10);
        modeMenu.getChildren().addAll(socketMode, rmiMode);


        searchServerBtn = new Button("Cerca server");
        GridPane.setHalignment(searchServerBtn, HPos.CENTER);


        layout.addRow(0, ipLabel, ipTextFiled);
        layout.addRow(1, portLabel, portTextFiled);
        layout.addRow(2, modeLabel, modeMenu);
        //layout.addRow(4, searchServerBtn);
        layout.add(searchServerBtn, 1, 4);
    }
}
