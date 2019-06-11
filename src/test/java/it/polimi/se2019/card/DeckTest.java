package it.polimi.se2019.card;

import it.polimi.se2019.card.deck.Deck;
import it.polimi.se2019.card.powerup.PowerUpCard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class DeckTest
{

    private Deck<PowerUpCard> deck;
    private static PowerUpCard card1;
    private static PowerUpCard card2;
    private static PowerUpCard card3;

    @BeforeClass
    public static void setUpClass()
    {
        card1 = new PowerUpCard();
        card1.setId("1");
        card2 = new PowerUpCard();
        card2.setId("2");
        card3 = new PowerUpCard();
        card3.setId("3");
    }

    @Before
    public void setUp()
    {
        deck = new Deck<>();

        deck.addCard(card1);
        deck.addCard(card2);
        deck.addCard(card3);
    }

    @Test
    public void getFirstCardTest()
    {
        int size = deck.size();
        PowerUpCard card1 = deck.getFirstCard();
        if(!card1.equals(DeckTest.card1) || deck.size() != size-1)fail();
        PowerUpCard card2 = deck.getFirstCard();
        if(!card2.equals(DeckTest.card2) || deck.size() != size-2)fail();
        PowerUpCard card3 = deck.getFirstCard();
        if(!card3.equals(DeckTest.card3) || deck.size() != size-3)fail();
    }

    @Test
    public void getRandomCardTest()
    {
        int size = deck.size();
        deck.getRandomCard();
        if(deck.size() != size-1)fail();
        deck.getRandomCard();
        if(deck.size() != size-2)fail();
        deck.getRandomCard();
        if(deck.size() != size-3)fail();
    }

    @Test
    public void shuffleTest()
    {
        int size = deck.size();
        deck.shuffle();
        if(deck.size() != size)fail();
    }
}
