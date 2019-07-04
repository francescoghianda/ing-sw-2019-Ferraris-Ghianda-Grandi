package it.polimi.se2019.card.weapon;


public class WeaponNotLoadException extends Exception
{
    public WeaponNotLoadException(WeaponCard weaponCard)
    {
        super(weaponCard.toString());
    }
}
