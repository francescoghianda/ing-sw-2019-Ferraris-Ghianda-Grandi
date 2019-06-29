package it.polimi.se2019;

import it.polimi.se2019.network.message.Bundle;
import it.polimi.se2019.player.Action;
import org.junit.Test;

import java.io.Serializable;

public class BundleTest
{

    @Test
    public void test()
    {
        Object obj = Bundle.of("aaa", Action.MOVE);

        Bundle<Serializable, Action> bundle = Bundle.cast(obj, Serializable.class, Action.class);

        System.out.println(bundle.getFirst() + " - " + bundle.getSecond());
    }
}
