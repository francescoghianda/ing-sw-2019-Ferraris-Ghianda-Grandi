package it.polimi.se2019.ui.gui.server;
import javax.swing.*;
import java.awt.*;

public class LabelServer extends JPanel {
    public LabelServer() {
        super(new GridLayout(3,1));
        JLabel label1, label2, label3;

        ImageIcon icon = createImageIcon("",
                "a pretty but meaningless splat");


        label1 = new JLabel("CLIENTS , SERVERS , IP AND PORT",
                icon,
                JLabel.CENTER);

        label1.setVerticalTextPosition(JLabel.BOTTOM);
        label1.setHorizontalTextPosition(JLabel.CENTER);


        label2 = new JLabel("Text-Only Label");
        label3 = new JLabel(icon);


        label1.setToolTipText("A label containing both image and text");
        label2.setToolTipText("A label containing only text");
        label3.setToolTipText("A label containing only an image");


        add(label1);
        add(label2);
        add(label3);
    }

    protected static ImageIcon createImageIcon(String path,
                                               String description) {
        java.net.URL imgURL = LabelServer.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private static void createLabelServer() {

        JFrame frame = new JFrame("Label Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.add(new LabelServer());


        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                UIManager.put("swing.boldMetal", Boolean.FALSE);

                createLabelServer();
            }
        });
    }
}
