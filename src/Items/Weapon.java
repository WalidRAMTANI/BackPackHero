package Items;

/**
 * Represents a weapon item that can deal damage.
 */
public interface Weapon {
    /**
     * Returns the damage value of this weapon.
     *
     * @return the damage inflicted by the weapon
     */
    int damage();

    void setAttack(int atta);

    /**
     * Returns the name of this weapon.
     *
     * @return the weapon's name
     */
    String name();

    public static void displayWeaponCharacteristics(Weapon weapon) {
        // Method emptied to remove console output
    }
}
