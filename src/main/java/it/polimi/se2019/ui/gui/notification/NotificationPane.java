package it.polimi.se2019.ui.gui.notification;

import it.polimi.se2019.ui.gui.GUI;
import it.polimi.se2019.utils.timer.Timer;
import it.polimi.se2019.utils.timer.TimerAdapter;
import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class NotificationPane extends Stage
{
    private static List<NotificationPane> notifications = new ArrayList<>();
    private static long ID = 0;
    private static int defaultWidth = 200;
    private static int defaultHeight = 100;

    private long id;

    private NotificationPane(String text, int width, int height)
    {
        super();
        this.id = ID;
        ID = ID == Long.MAX_VALUE ? 0 : ID+1;
        init(text, width, height);
    }

    public static void showNotification(String text)
    {
        NotificationPane notificationPane = new NotificationPane(text, defaultWidth, defaultHeight);
        notifications.forEach(pane -> pane.setY(pane.getY()-(defaultHeight+10)));
        notifications.add(notificationPane);
        notificationPane.show();
    }

    private void init(String text, int width, int height)
    {
        setWidth(width);
        setHeight(height);
        initStyle(StageStyle.TRANSPARENT);

        setX(GUI.getScreenWidth()-width-20);
        setY(GUI.getScreenHeight()-height-20);
        setAlwaysOnTop(true);

        Label label = new Label(text);
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
        BorderPane pane = new BorderPane();
        pane.getStylesheets().add("/css/NotificationPaneStyle.css");
        pane.setCenter(label);
        pane.setMaxWidth(Double.MAX_VALUE);
        pane.setMaxHeight(Double.MAX_VALUE);

        pane.setPrefSize(width, height);

        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
        setOnShown(this::onShown);
    }

    private void onShown(WindowEvent event)
    {
        String timerName = "notification_pane_"+id;
        Timer.destroyTimer(timerName);
        Timer timer = Timer.createTimer(timerName, 3);
        timer.addTimerListener(new TimerAdapter() {
            @Override
            public void onTimerEnd(String timerName)
            {
                fadeOut();
            }
        });
        timer.start();
    }

    private void fadeOut()
    {
        FadeTransition transition = new FadeTransition();
        transition.setOnFinished(event ->
        {
            notifications.remove(this);
            this.close();
        });
        transition.setNode(getScene().getRoot());
        transition.setDuration(Duration.seconds(1.5));
        transition.setFromValue(getOpacity());
        transition.setToValue(0);
        transition.playFromStart();
    }



}
