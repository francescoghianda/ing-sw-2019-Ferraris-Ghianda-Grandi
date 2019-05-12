package it.polimi.se2019.card;

import it.polimi.se2019.card.ammo.AmmoCard;
import it.polimi.se2019.card.deck.Deck;
import it.polimi.se2019.card.deck.DeckFactory;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.utils.xml.NotValidXMLException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class DeckFactoryTest
{
    private DeckFactory deckFactory;

    @Before
    public void setUp()
    {
        try
        {
            deckFactory = DeckFactory.newInstance("/xml/decks/decks.xml");
        }
        catch (NotValidXMLException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void parseWeaponCardsTest()
    {
        Deck<WeaponCard> weaponCards = deckFactory.createWeaponDeck();
        System.out.println(weaponCards);
    }

    @Test
    public void parsePowerUpCardsTest()
    {
        Deck<PowerUpCard> powerUpCards = deckFactory.createPowerUpDeck();
        System.out.println(powerUpCards);
    }

    @Test
    public void parseAmmoCardsTest()
    {
        Deck<AmmoCard> ammoCards = deckFactory.createAmmoDeck();
        System.out.println(ammoCards);
        if(ammoCards.size() != 36)fail();
    }
}
