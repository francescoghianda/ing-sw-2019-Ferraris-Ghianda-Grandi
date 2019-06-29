package it.polimi.se2019.utils.xml;


import it.polimi.se2019.card.ammo.AmmoCard;
import it.polimi.se2019.card.Card;
import it.polimi.se2019.card.cost.Cost;
import it.polimi.se2019.card.powerup.PowerUpCard;
import it.polimi.se2019.card.weapon.OptionalEffect;
import it.polimi.se2019.card.weapon.WeaponCard;
import it.polimi.se2019.utils.constants.GameColor;
import it.polimi.se2019.utils.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLDeckReader
{
    private Document xmlDocument;

    private enum DeckType
    {
        WEAPON_CARDS("weaponCards"), POWER_UP_CARDS("powerUpCards"), AMMO_CARDS("ammoCards");

        final String name;

        DeckType(String name)
        {
            this.name = name;
        }

        String getName()
        {
            return this.name;
        }

    }

    public XMLDeckReader(String filePath) throws NotValidXMLException
    {

        try
        {
            if(!XMLValidator.isValid(filePath, "/xml/decks/validator.xsd"))throw new NotValidXMLException(filePath+" - "+XMLValidator.getError());

            InputStream is = getClass().getResourceAsStream(filePath);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            xmlDocument = documentBuilder.parse(is);
            xmlDocument.getDocumentElement().normalize();
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            Logger.exception(e);
        }
    }

    public List<AmmoCard> parseAmmoCards()
    {
        List<AmmoCard> ammoCards = new ArrayList<>();

        NodeList cards = getCards(DeckType.AMMO_CARDS);

        if(cards == null)return ammoCards;

        for(int i = 0; i < cards.getLength(); i++)ammoCards.add(parseAmmoCard((Element) cards.item(i)));

        return ammoCards;
    }

    public List<PowerUpCard> parsePowerUpCards()
    {
        List<PowerUpCard> powerUpCards = new ArrayList<>();

        NodeList cards = getCards(DeckType.POWER_UP_CARDS);

        if(cards == null)return powerUpCards;

        for(int i = 0; i < cards.getLength(); i++)powerUpCards.addAll(parsePowerUpCard((Element)cards.item(i)));

        powerUpCards.forEach(card -> PowerUpCard.addCard(card.getId(), card));

        return powerUpCards;
    }

    public List<WeaponCard> parseWeaponCards()
    {
        List<WeaponCard> weaponCards = new ArrayList<>();

        NodeList cards = getCards(DeckType.WEAPON_CARDS);

        if(cards == null)return weaponCards;

        for(int i = 0; i < cards.getLength(); i++) weaponCards.add(parseWeaponCard((Element) cards.item(i)));

        weaponCards.forEach(card -> WeaponCard.addCard(card.getId(), card));

        //TEST

        /*String script = "select_player(visible context_player)->player1\n"+
                        "hit player1 3\n"+
                        "mark player1 1";*/
        String script = "select_block(player1,direction north)\n"+
                "select_block(player2, same-direction-of player1)\n"+
                "hit(player1, 3)\n"+
                "ask_and_select(Vuoi spostare il il giocatore di un blocco?,block1,block,maxd player1 1)\n"+
                "move(player1,block1)";
        //weaponCards.forEach(card -> card.setBasicModeScript(script));


        //

        return weaponCards;
    }

    private NodeList getCards(DeckType deckType)
    {
        NodeList decks = xmlDocument.getElementsByTagName(XMLTags.DECK);
        if(decks.getLength() == 0)return null;

        Element deck = null;
        for(int i = 0; i < decks.getLength(); i++) if(((Element)decks.item(i)).getAttribute(XMLTags.TYPE).equals(deckType.getName()))deck = (Element) decks.item(i);

        if(deck == null)return null;

        String tag;

        switch (deckType)
        {
            case WEAPON_CARDS:
                tag = XMLTags.WEAPON_CARD;
                break;
            case POWER_UP_CARDS:
                tag = XMLTags.POWER_UP_CARD;
                break;
            case AMMO_CARDS:
                tag = XMLTags.AMMO_CARD;
                break;
            default:
                return null;
        }

        return deck.getElementsByTagName(tag);
    }

    private AmmoCard parseAmmoCard(Element cardElement)
    {
        String id = cardElement.getAttribute("id");
        boolean pickUpPowerUp = Boolean.parseBoolean(cardElement.getElementsByTagName(XMLTags.PICK_UP_POWER_UP).item(0).getTextContent());
        Cost value = parseCostElement((Element) cardElement.getElementsByTagName(XMLTags.VALUE).item(0));
        return new AmmoCard(value.getRedAmmo(), value.getBlueAmmo(), value.getYellowAmmo(), pickUpPowerUp, id);
    }

    private List<PowerUpCard> parsePowerUpCard(Element cardElement)
    {
        List<PowerUpCard> cards = new ArrayList<>();

        PowerUpCard redCard = new PowerUpCard();
        PowerUpCard blueCard = new PowerUpCard();
        PowerUpCard yellowCard = new PowerUpCard();

        cards.add(redCard);
        cards.add(blueCard);
        cards.add(yellowCard);

        redCard.setColor(GameColor.RED);
        blueCard.setColor(GameColor.BLUE);
        yellowCard.setColor(GameColor.YELLOW);

        String[] ids =  cardElement.getAttribute("id").split(",");

        cards.forEach(card -> setBasicCardAttribute(cardElement, card));

        blueCard.setId(ids[0]+"_1");
        redCard.setId(ids[1]+"_1");
        yellowCard.setId(ids[2]+"_1");

        String useStr = cardElement.getElementsByTagName(XMLTags.USE).item(0).getTextContent();
        PowerUpCard.Use use;

        switch (useStr)
        {
            case "onDamage":
                use = PowerUpCard.Use.ON_DAMAGE;
                break;
            case "onFire":
                use = PowerUpCard.Use.ON_FIRE;
                break;
            default:
                use = PowerUpCard.Use.ALWAYS;
        }

        String description = parseMultilineElement(((Element)cardElement.getElementsByTagName(XMLTags.DESCRIPTION).item(0)));
        String script = parseMultilineElement((Element) cardElement.getElementsByTagName(XMLTags.CARD_SCRIPT).item(0));

        cards.forEach(card ->
        {
            card.setDescription(description);
            card.setUse(use);
            card.setScript(script);
        });

        cards.add(redCard.clone());
        cards.add(blueCard.clone());
        cards.add(yellowCard.clone());

        return cards;
    }

    private void setBasicCardAttribute(Element element, Card card)
    {
        card.setId(element.getAttribute("id"));
        card.setName(element.getElementsByTagName(XMLTags.NAME).item(0).getTextContent());
        card.setDescription(parseMultilineElement((Element) element.getElementsByTagName(XMLTags.DESCRIPTION).item(0)));
    }

    private WeaponCard parseWeaponCard(Element card)
    {
        WeaponCard wpCard = new WeaponCard();

        setBasicCardAttribute(card, wpCard);
        wpCard.setBuyCost(parseCostElement((Element)card.getElementsByTagName(XMLTags.BUY_COST).item(0)));
        wpCard.setReloadCost(parseCostElement((Element)card.getElementsByTagName(XMLTags.RELOAD_COST).item(0)));

        Element basicMode = (Element) card.getElementsByTagName(XMLTags.BASIC_MODE).item(0);
        wpCard.setBasicModeScript(parseMultilineElement((Element) basicMode.getElementsByTagName(XMLTags.CARD_SCRIPT).item(0)));

        Element alternateMode = (Element) card.getElementsByTagName(XMLTags.ALTERNATE_MODE).item(0);
        if(alternateMode != null)
        {
            wpCard.setAlternateModeCost(parseCostElement((Element) alternateMode.getElementsByTagName(XMLTags.COST).item(0)));
            wpCard.setAlternateModeScript(parseMultilineElement((Element) alternateMode.getElementsByTagName(XMLTags.CARD_SCRIPT).item(0)));
        }

        Element optionalEffectsElement = (Element) card.getElementsByTagName(XMLTags.OPTIONAL_EFFECTS).item(0);
        if(optionalEffectsElement != null)
        {
            NodeList optionalEffects = optionalEffectsElement.getElementsByTagName(XMLTags.OPTIONAL_EFFECT);

            for(int i = 0; i < optionalEffects.getLength(); i++)
            {
                Element optionalEffectElement = (Element) optionalEffects.item(i);
                wpCard.addOptionalEffect(parseOptionalEffect(optionalEffectElement), optionalEffectElement.getAttribute("name"));
            }
        }


        return wpCard;
    }

    private OptionalEffect parseOptionalEffect(Element element)
    {
        OptionalEffect optionalEffect = new OptionalEffect(element.getAttribute("name"));

        optionalEffect.setEnabled(Boolean.parseBoolean(element.getElementsByTagName(XMLTags.ENABLED).item(0).getTextContent()));
        optionalEffect.setCost(parseCostElement((Element) element.getElementsByTagName(XMLTags.COST).item(0)));
        optionalEffect.setScript(parseMultilineElement((Element) element.getElementsByTagName(XMLTags.CARD_SCRIPT).item(0)));

        return optionalEffect;
    }

    private Cost parseCostElement(Element element)
    {
        if(element == null)return new Cost(0, 0, 0);

        Element blue = (Element) element.getElementsByTagName(XMLTags.BLUE).item(0);
        Element red = (Element) element.getElementsByTagName(XMLTags.RED).item(0);
        Element yellow = (Element) element.getElementsByTagName(XMLTags.YELLOW).item(0);

        int blueValue = blue == null ? 0 : Integer.parseInt(blue.getTextContent());
        int redValue = red == null ? 0 : Integer.parseInt(red.getTextContent());
        int yellowValue = yellow == null ? 0 : Integer.parseInt(yellow.getTextContent());

        return new Cost(redValue, blueValue, yellowValue);
    }

    private String parseMultilineElement(Element element)
    {
        StringBuilder stringBuilder = new StringBuilder();
        NodeList lines = element.getElementsByTagName(XMLTags.LINE);
        for(int i = 0; i < lines.getLength(); i++)
        {
            if(i != 0)stringBuilder.append("\n");
            stringBuilder.append(lines.item(i).getTextContent());
        }

        return stringBuilder.toString();
    }
}
