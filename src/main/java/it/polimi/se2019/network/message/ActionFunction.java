package it.polimi.se2019.network.message;

import it.polimi.se2019.ui.UI;

import java.io.Serializable;

public interface ActionFunction extends Serializable
{
    Serializable apply(UI ui);
}
