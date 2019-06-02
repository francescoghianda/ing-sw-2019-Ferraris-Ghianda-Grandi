package it.polimi.se2019.card;

import it.polimi.se2019.card.cardscript.LogicExpression;
import org.junit.Test;

public class LogicExpressionTest
{

    @Test
    public void test()
    {
        LogicExpression expression = new LogicExpression("(visible context_player)");
    }
}
