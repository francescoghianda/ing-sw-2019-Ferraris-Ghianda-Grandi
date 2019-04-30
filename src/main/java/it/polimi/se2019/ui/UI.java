package it.polimi.se2019.ui;

/**
 * initializes the user interface
 */
public interface UI
{
    void update();
    void init();

    String getUsername();
    void logged();
}
