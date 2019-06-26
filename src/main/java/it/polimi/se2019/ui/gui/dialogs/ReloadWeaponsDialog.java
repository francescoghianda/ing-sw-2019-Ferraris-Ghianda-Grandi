package it.polimi.se2019.ui.gui.dialogs;

import it.polimi.se2019.card.Card;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ReloadWeaponsDialog extends Dialog<ArrayList<Card>> implements Initializable
{


    public ReloadWeaponsDialog()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReloadWeaponsDialog.fxml"));

        try
        {
            Parent root = loader.load();

            ReloadWeaponsDialogController controller = loader.getController();
            controller.setDialog(this);
            getDialogPane().setMinSize(300, 250);
            getDialogPane().setContent(root);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }
}
