package it.polimi.se2019.ui.gui;

import it.polimi.se2019.ui.NetworkInterface;
import it.polimi.se2019.utils.network.NetworkUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartMenuScene extends BorderPane implements Initializable
{
    @FXML
    private Pane formBg;

    @FXML
    private TextField portTextFiled;

    @FXML
    private TextField ipTextFiled;

    @FXML
    private Button searchServerBtn;

    @FXML
    private ImageView loadingGifView;

    @FXML
    private RadioButton rmiMode;

    private final String errorStyle = "-fx-background-color: rgba(255,0,0,0.45);" + "-fx-text-fill: white";

    public StartMenuScene()
    {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StartMenuScene.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        load(loader);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        getStylesheets().add("css/StartMenuStyle.css");

        loadingGifView.setImage(new Image(getClass().getResourceAsStream("/img/loading.gif")));

        formBg.setStyle("-fx-background-color: rgba(255,255,255,0.6);"+"-fx-background-radius: 20 20 20 20");
        portTextFiled.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if (!newValue.matches("\\d*"))portTextFiled.setText(oldValue);
        });

    }

    @FXML
    public void search(ActionEvent event)
    {
        if(event.getSource().equals(searchServerBtn))
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

            if(ok)
            {
                searchServerBtn.setVisible(false);
                loadingGifView.setVisible(true);
                SceneManager.getInstance().connect(ip, port, connectionMode);
            }
        }
    }

    void connectionRefused()
    {
        searchServerBtn.setVisible(true);
        loadingGifView.setVisible(false);
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
}
