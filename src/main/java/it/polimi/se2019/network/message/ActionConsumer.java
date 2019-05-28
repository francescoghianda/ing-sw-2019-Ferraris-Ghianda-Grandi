package it.polimi.se2019.network.message;

import it.polimi.se2019.ui.UI;

import java.io.Serializable;

public interface ActionConsumer extends Serializable
{
    void accept(UI ui);
}
