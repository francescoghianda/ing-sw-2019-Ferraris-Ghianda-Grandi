package it.polimi.se2019.ui.gui.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class CloseDialogController implements Initializable
{
    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;

    @FXML
    private BorderPane borderPane;

    private CloseDialog dialog;

    public CloseDialogController()
    {

    }

    public void setDialog(CloseDialog dialog)
    {
        this.dialog = dialog;
    }

    public void btnClick(ActionEvent actionEvent)
    {
        if(actionEvent.getSource().equals(yesButton)) dialog.setResult(ButtonType.YES);
        else dialog.setResult(ButtonType.NO);
        dialog.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }
}
