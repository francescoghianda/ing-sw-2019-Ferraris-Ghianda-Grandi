package it.polimi.se2019.network.message;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.ui.UI;

import java.io.Serializable;

public interface SerializableFunction extends Serializable
{
    Serializable apply(UI ui) throws CanceledActionException;
}
