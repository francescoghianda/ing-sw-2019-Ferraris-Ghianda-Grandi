package it.polimi.se2019.ui.gui.server;

import javax.swing.*;
import java.awt.*;

public class ServerGui extends JFrame
{
    private JPanel panel;
    private JLabel connectionMode;
    private JLabel clientTitle;
    private JTable clientTable;
    private JLabel socketModeLabel;
    private JLabel rmiModeLabel;
    private ImageIcon socketIcon;
    private ImageIcon rmiIcon;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public ServerGui()
    {
        this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        GridLayout gridLayout = new GridLayout(0, 5);

        panel = new JPanel(gridLayout);
        panel.setLocation(0, 0);
        panel.setSize(WIDTH, HEIGHT);
        panel.setBackground(Color.WHITE);
        add(panel);

        connectionMode = new JLabel("Stato server: ");
        connectionMode.setLocation(0, 0);
        panel.add(connectionMode);

        clientTitle = new JLabel("Client:");
        clientTable = new JTable();

        this.setVisible(true);
    }


    public static void main(String[] args)
    {
        new ServerGui();
    }
}
