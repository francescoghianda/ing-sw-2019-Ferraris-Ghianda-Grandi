package it.polimi.se2019.ui.gui.dialogs;

import it.polimi.se2019.card.Card;
import it.polimi.se2019.ui.gui.components.CardPane;
import it.polimi.se2019.ui.gui.components.CardView;
import it.polimi.se2019.ui.gui.components.ColoredButton;
import it.polimi.se2019.utils.constants.GameColor;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReloadWeaponsDialog extends Dialog<ArrayList<Card>> implements Initializable, CardView.OnCardViewClickListener
{
    public static final int SINGLE_SELECTION_MODE = 0;
    public static final int MULTIPLE_SELECTION_MODE = 1;


    @FXML
    private CardPane cardPane;

    @FXML
    private ColoredButton okButton;

    @FXML
    private BorderPane rootPane;

    private int selected;
    private int maxSelected;

    private int selectionMode;

    public ReloadWeaponsDialog(List<Card> cards, int selectionMode)
    {
        this.selectionMode = selectionMode;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReloadWeaponsDialog.fxml"));
        loader.setController(this);
        try
        {
            Parent root = loader.load();

            getDialogPane().setMinSize(300, 250);
            getDialogPane().setContent(root);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if(cards.size() > 3)throw new IllegalArgumentException();
        cards.forEach(card -> cardPane.addCard(card, this));

        cardPane.enableAllCards();
    }


    public void closeDialog()
    {
        setResult(new ArrayList<>(cardPane.getSelectedCards()));
        close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        cardPane.setCardSelectable(true);
        cardPane.setCardViewTransition(CardView.FADE_TRANSITION);
        cardPane.setTitleSize(20);

        okButton.setFont(new Font(15));
        okButton.setColor(GameColor.RED);

        Image backgroundImage = new Image(getClass().getResourceAsStream("/img/texture2.png"));
        getDialogPane().setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        if(selectionMode == SINGLE_SELECTION_MODE) cardPane.setTitle("Seleziona l'arma che vuoi ricaricare");

        setResult(new ArrayList<>());
    }

    @Override
    public void onCardClick(CardView cardView)
    {
        if(selectionMode == SINGLE_SELECTION_MODE && cardView.isSelected())
        {
            cardPane.deselectAllCards();
            cardView.setSelected(true);
        }

        if(cardPane.getSelectedCards().size() > 0)
        {
            okButton.setText("Ricarica");
            okButton.setColor(GameColor.GREEN);
        }
        else
        {
            okButton.setText("Annulla");
            okButton.setColor(GameColor.RED);
        }
    }
}
