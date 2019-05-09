package it.polimi.se2019.ui.gui.dialogs;

import it.polimi.se2019.utils.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Random;

public class CloseDialog extends Dialog<ButtonType> implements EventHandler<DialogEvent>
{
    private static Random random;
    private static int lastBg;
    private Stage window;

    public CloseDialog(Stage window)
    {
        this.window = window;
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CloseDialog.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add("/css/CloseDialogStyle.css");
            CloseDialogController controller = loader.getController();
            controller.setDialog(this);
            getDialogPane().setMinSize(300, 250);
            getDialogPane().setContent(root);

            setOnCloseRequest(this);

            GaussianBlur blurEffect = new GaussianBlur(10);
            window.getScene().getRoot().setEffect(blurEffect);

            String bgUrl = "img/close_dialog_bg/close_dialog_bg_"+getBgNumber()+".png";
            getDialogPane().setBackground(new Background(new BackgroundImage(new Image(bgUrl), null, null, BackgroundPosition.CENTER, new BackgroundSize(300, 300, false, false, true, true))));

            setResultConverter(buttonType -> buttonType);
        }
        catch (IOException e)
        {
            Logger.exception(e);
        }
    }

    private static int getBgNumber()
    {
        int number;
        if(random == null)random = new Random();
        do
        {
            number = random.nextInt(5) + 1;
        }
        while (number == lastBg);
        lastBg = number;
        return number;
    }

    @Override
    public void handle(DialogEvent event)
    {
        if(event.getEventType().equals(DialogEvent.DIALOG_CLOSE_REQUEST))window.getScene().getRoot().setEffect(null);
    }
}
