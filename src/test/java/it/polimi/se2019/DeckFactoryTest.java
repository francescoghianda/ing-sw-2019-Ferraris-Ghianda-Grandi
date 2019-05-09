package it.polimi.se2019;

import it.polimi.se2019.card.AmmoCard;
import it.polimi.se2019.card.Deck;
import it.polimi.se2019.card.DeckFactory;
import it.polimi.se2019.card.weapon.WeaponCard;
import org.junit.Test;

public class DeckFactoryTest
{

    @Test
    public void createAmmoDeckTest()
    {
        Deck<AmmoCard> ammoCards = DeckFactory.createAmmoDeck();
        assert ammoCards.size() == 36;
    }

    @Test
    public void createWeaponDeckTest()
    {
        Deck<WeaponCard> weaponCards = DeckFactory.createWeaponDeck();
    }
}
