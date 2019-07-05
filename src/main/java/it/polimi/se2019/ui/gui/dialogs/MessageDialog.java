package it.polimi.se2019.ui.gui.dialogs;


import it.polimi.se2019.ui.gui.components.ColoredButton;
import it.polimi.se2019.utils.constants.GameColor;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;



public class MessageDialog extends Dialog<ButtonType>
{

    private BorderPane rootPane;
    private Label messageLabel;
    private ColoredButton okButton;
    private HBox bottomHBox;
    private StackPane stackPane;


    public MessageDialog(String message)
    {
        getDialogPane().setMinSize(300, 250);
        getDialogPane().setMaxSize(300, 250);

        rootPane = new BorderPane();
        messageLabel = new Label(message);
        messageLabel.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
        messageLabel.setFont(new Font(20));
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setTextFill(Color.WHITE);
        stackPane = new StackPane(messageLabel);
        stackPane.setAlignment(Pos.CENTER);
        stackPane.setPadding(new Insets(0, 10, 0, 10));
        rootPane.setCenter(stackPane);

        okButton = new ColoredButton();
        okButton.setColor(GameColor.WHITE);
        okButton.setText("Ok");
        okButton.setOnAction(this::closeDialog);
        okButton.setMinSize(100, 50);
        bottomHBox = new HBox(okButton);
        bottomHBox.setAlignment(Pos.CENTER);
        rootPane.setBottom(bottomHBox);

        Image background = new Image(getClass().getResourceAsStream("/img/texture3.png"));
        getDialogPane().setBackground(new Background(new BackgroundImage(background, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        getDialogPane().setContent(rootPane);
    }

    private void closeDialog(ActionEvent event)
    {
        setResult(ButtonType.OK);
        close();
    }
}
