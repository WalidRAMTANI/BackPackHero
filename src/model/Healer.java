package model;

import java.util.Objects;

// Simple record representing a healer with a cost and amount of healing
public record Healer(int healAmount) {

    // Compact constructor to ensure no negative values for healing cost or amount
    public Healer {
        if (healAmount < 0) {
            throw new IllegalArgumentException("healingCost or healAmount is negative");
        }
    }

    // Method to heal a hero; currently just returns the hero unchanged (to be
    // implemented)
    public Hero heal(Hero hero) {
        Objects.requireNonNull(hero);

        return hero;
    }
}
