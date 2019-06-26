package it.polimi.se2019.ui.gui.dialogs;

import it.polimi.se2019.ui.gui.components.CardPane;
import it.polimi.se2019.ui.gui.components.ColoredButton;
import javafx.fxml.FXML;

public class ReloadWeaponsDialogController
{
    @FXML
    private CardPane cardPane;

    @FXML
    private ColoredButton okButton;

    private ReloadWeaponsDialog dialog;


    public void setDialog(ReloadWeaponsDialog dialog)
    {
        this.dialog = dialog;
    }

}
