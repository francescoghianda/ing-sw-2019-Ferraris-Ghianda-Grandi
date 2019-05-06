package it.polimi.se2019.ui.gui;

import it.polimi.se2019.ui.NetworkInterface;
import it.polimi.se2019.utils.network.NetworkUtils;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class StartMenuScene extends Scene implements EventHandler<MouseEvent>
{
    private Button searchServerBtn;
    private Label ipLabel;
    private Label portLabel;
    private Label modeLabel;
    private TextField ipTextFiled;
    private TextField portTextFiled;
    private RadioButton socketMode;
    private RadioButton rmiMode;
    private ToggleGroup toggleGroup;
    private ImageView loadingGifView;

    private final String errorStyle = "-fx-background-color: rgba(255,0,0,0.45);" +
            "-fx-text-fill: white";

    public StartMenuScene()
    {
        super(new BorderPane(), 600, 403);
        getStylesheets().add("css/StartMenuStyle.css");

        BorderPane layout = (BorderPane)getRoot();

        StackPane form = new StackPane();
        Pane formBg = new Pane();
        formBg.setMaxWidth(360);
        formBg.setMaxHeight(145);
        formBg.setStyle("-fx-background-color: rgba(255,255,255,0.6);"+"-fx-background-radius: 20 20 20 20");
        StackPane.setAlignment(formBg, Pos.BOTTOM_CENTER);

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

        portTextFiled.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if (!newValue.matches("\\d*"))portTextFiled.setText(oldValue);
        });

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

        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);
        stackPane.setMinHeight(150);
        //VBox btnBox = new VBox();
        //btnBox.setMinHeight(150);
        //btnBox.setAlignment(Pos.CENTER);
        searchServerBtn = new Button("Cerca server");

        loadingGifView = new ImageView();

        loadingGifView.setImage(new Image("/img/loading.gif"));
        loadingGifView.setFitHeight(50);
        loadingGifView.setFitWidth(50);
        loadingGifView.setSmooth(true);
        loadingGifView.setVisible(false);

        stackPane.getChildren().addAll(searchServerBtn, loadingGifView);

        gridPane.addRow(0, ipLabel, ipTextFiled);
        gridPane.addRow(1, portLabel, portTextFiled);
        gridPane.addRow(2, modeLabel, modeMenu);

        form.getChildren().addAll(formBg, gridPane);

        layout.setCenter(form);
        layout.setBottom(stackPane);

        searchServerBtn.setOnMouseEntered(e -> searchServerBtn.setStyle("-fx-border-color: red"));
        searchServerBtn.setOnMouseExited(e -> searchServerBtn.setStyle("-fx-border-color: white"));
        searchServerBtn.setOnMouseClicked(this);

    }

    @Override
    public void handle(MouseEvent event)
    {
        if(event.getSource().equals(searchServerBtn) && event.getEventType().equals(MouseEvent.MOUSE_CLICKED))
        {
             String ip = ipTextFiled.getText();
             int port = portTextFiled.getText().isEmpty() ? 0 : Integer.parseInt(portTextFiled.getText());
             boolean ok = true;

            if(!NetworkUtils.isIp(ip))
            {
                ipTextFiled.setStyle(errorStyle);
                ok = false;
            }
            else ipTextFiled.setStyle("");
            if(!NetworkUtils.isValidPort(port))
            {
                portTextFiled.setStyle(errorStyle);
                ok = false;
            }
            else portTextFiled.setStyle("");

            int connectionMode = NetworkInterface.SOCKET_MODE;
            if(rmiMode.isSelected())connectionMode = NetworkInterface.RMI_MODE;

            //searchServerBtn.setStyle("-fx-background-image: url('/img/loading.gif')");
            searchServerBtn.setVisible(false);
            loadingGifView.setVisible(true);

            if(ok)
            {

                SceneManager.getInstance().connect(ip, port, connectionMode);
            }
        }
    }


}
