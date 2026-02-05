package Items;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Enemies.Enemy;
import effects.AddDamageAdjWeapon;
import effects.AddEnergyCost;
import effects.AddHpHero;
import effects.AddMana;
import effects.AddProtectionAdj;
import effects.AttackAllEnemies;
import effects.Effect;
import effects.NumberUses;
import effects.PoisonEffect;
import effects.PoisonHero;
import effects.ReduceDamageEffectEnemy;
import effects.RemovePoisonFromSelf;
import model.Hero;

public interface Item {

    int x(); // height

    int y(); // width

    default void baseShape() {

    }

    ArrayList<Point> occupiedCases();

    int rarity();

    String name();

    String description();

    ArrayList<Effect> effects();

    Item createNewInstance();

    void notOnUse(Hero hero, Enemy e, ArrayList<Enemy> enemies);

    void onUse(Hero hero, Enemy e, ArrayList<Enemy> enemies);

    default int widthItem() {
        return occupiedCases().stream().mapToInt(p -> p.x).max().orElse(0) + 1;
    }

    default int heightItem() {
        return occupiedCases().stream().mapToInt(p -> p.y).max().orElse(0) + 1;
    }

    default int calculatePrice() {
        return rarity();
    }

    // ================== CASE SHAPES ==================

    static ArrayList<Point> oneCase() {
        return new ArrayList<>(List.of(new Point(0, 0)));
    }

    static ArrayList<Point> twoCasesArmor() {
        return new ArrayList<>(List.of(
                new Point(0, 0), new Point(1, 0),
                new Point(0, 1), new Point(1, 1)));
    }

    static ArrayList<Point> twoFoodCases() {
        return new ArrayList<>(List.of(
                new Point(0, 0), new Point(1, 0) // vertical
        ));
    }

    static ArrayList<Point> twoBatonCases() {
        return new ArrayList<>(List.of(
                new Point(0, 0), new Point(0, 1) // changed from horizontal to vertical
        ));
    }

    static ArrayList<Point> threeBatonCases() {
        return new ArrayList<>(List.of(
                new Point(0, 0), new Point(0, 1), new Point(0, 2) // vertical line of 3
        ));
    }

    static ArrayList<Point> fourBatonCases() {
        return new ArrayList<>(List.of(
                new Point(0, 0), new Point(0, 1),
                new Point(0, 2), new Point(0, 3) // vertical line of 4
        ));
    }

    static ArrayList<Point> threeBatonAxeCases() {
        return new ArrayList<>(List.of(
                new Point(0, 0), new Point(0, 1), new Point(1, 1) // vertical stem + one to right
        ));
    }

    static ArrayList<Point> threeAxeRare() {
        return new ArrayList<>(List.of(
                new Point(0, 0), new Point(1, 0), new Point(1, 1) // "L" shape rotated
        ));
    }

    static ArrayList<Point> twoWandCases() {
        return new ArrayList<>(List.of(
                new Point(0, 0), new Point(1, 1) // diagonal from top-left to bottom-right
        ));
    }

    // ================== GOLD ==================

    static Gold generateGold() {
        int prob = new Random().nextInt(100);
        return new Gold("Gold", prob, prob);
    }

    static Item generateKey() {
        return new Key("Key", 1);
    }

    // ================== FOOD ==================

    static Food generateFood() {
        Random r = new Random();
        int prob = r.nextInt(100);

        return switch (r.nextInt(3)) {
            case 0 -> new Food("Apple", 1, 1, prob, true,
                    new ArrayList<>(List.of(new AddHpHero(2), new NumberUses(1))),
                    oneCase(),
                    "Adds 2 HP");

            case 1 -> new Food("Meal", 2, 1, prob, true,
                    new ArrayList<>(List.of(new AddEnergyCost(2), new NumberUses(1))),
                    twoFoodCases(),
                    "Adds 2 Energy");

            default -> new Food("Steak", 2, 1, prob, true,
                    new ArrayList<>(List.of(new AddEnergyCost(3), new NumberUses(1), new AddProtectionAdj(1))),
                    twoFoodCases(),
                    "Adds 3 Energy and add 1 protection for each adj Armor item");
        };
    }

    // ================== WEAPONS ==================
    static Item generateWeaponMelee(Random r) {
        int prob = r.nextInt(100);

        if (prob < 70) {
            return new MeleeWeapon(
                    "Wooden Sword",
                    1, 3, 1, prob, 10, false,
                    new ArrayList<>(),
                    threeBatonCases(),
                    "Deals 10 damage");
        } else if (prob < 95) {
            return new MeleeWeapon(
                    "Bowblade",
                    1, 2, 1, prob, 15, false,
                    new ArrayList<>(List.of(new AddDamageAdjWeapon(1))),
                    twoBatonCases(),
                    "Deals 15 damage\n add 1 damage for adj weapons\n");
        } else {
            return new MeleeWeapon(
                    "Hatchet",
                    1, 4, 1, prob, 20, false,
                    new ArrayList<>(List.of(new ReduceDamageEffectEnemy(2))),
                    fourBatonCases(),
                    "Deals 20 damage\n Reduce damage ennemies attack by 2\n");
        }
    }

    static Item generateWeaponRanged(Random r) {
        int prob = r.nextInt(100);

        if (prob < 70) {
            return new RangedWeapon(
                    "Crossbow",
                    1, 3, 1, prob, 10, false,
                    new ArrayList<>(),
                    threeBatonCases(),
                    "Deals 10 damage");
        } else if (prob < 95) {
            return new RangedWeapon(
                    "Shiv",
                    1, 2, 1, prob, 15, false,
                    new ArrayList<>(List.of(new AddDamageAdjWeapon(1))),
                    twoBatonCases(),
                    "Deals 15 damage\n add 1 damage for adj weapons\n");
        } else {
            return new RangedWeapon(
                    "Poison Arrow",
                    2, 1, 1, prob, 20, false,
                    new ArrayList<>(List.of(new ReduceDamageEffectEnemy(2))),
                    twoFoodCases(),
                    "Deals 20 damage\n Reduce damage ennemies attack by 2\n");
        }
    }

    // ================== ARMOR ==================

    static Armor generateArmor() {
        Random r = new Random();
        int prob = r.nextInt(100);

        if (prob < 70) {
            return new Armor(
                    "Tunic",
                    2, 2, 1, prob, 5,
                    "Adds 5 Block\nAdjacent Armor gets +1 Block",
                    new ArrayList<>(List.of(new AddProtectionAdj(1))),
                    twoCasesArmor());
        }

        if (prob < 90) {
            return new Armor(
                    "Chainmail",
                    2, 2, 1, prob, 10,
                    "Adds 10 Block",
                    new ArrayList<>(),
                    twoCasesArmor());
        }

        return new Armor(
                "Wizards Robe",
                2, 2, 1, prob, 6,
                "Adds 6 Block\n+2 damage for each adjascent weapon",
                new ArrayList<>(List.of(new AddDamageAdjWeapon(2))),
                twoCasesArmor());
    }

    // ================== SHIELDS ==================

    static Shield generateShield() {
        Random r = new Random();
        int prob = r.nextInt(100);

        if (prob < 70) {
            return new Shield(
                    "Rough Buckler",
                    1, 1, 1, prob, 7,
                    "Adds 7 Block",
                    new ArrayList<>(),
                    oneCase());
        }

        if (prob < 90) {
            return new Shield(
                    "Knight's Shield",
                    1, 2, 1, prob, 7,
                    "Adds 7 Block and 1 protection for adj items",
                    new ArrayList<>(List.of(new AddProtectionAdj(1))),
                    twoBatonCases());
        }

        return new Shield(
                "Mirror Shield",
                1, 3, 1, prob, 6,
                "Adds 6 Block\n Poison all enemies by 2",
                new ArrayList<>(List.of(new PoisonEffect(2))),
                threeBatonCases());
    }

    // ================== MAGIC ITEMS ==================

    static MagicItem generateMagicItem(Random r) {
        int prob = r.nextInt(100);

        if (prob < 70) {
            return new MagicItem(
                    "Cleansing Wand",
                    2, 2, 1, prob, 6, false,
                    new ArrayList<>(List.of(new PoisonHero(2))),
                    twoWandCases(),
                    "Deals 6 Damage\n" + " Poisoned by 2\n");
        }

        if (prob < 90) {
            return new MagicItem(
                    "Wizard Staff",
                    1, 2, 1, prob, 15, false,
                    new ArrayList<>(List.of(new AddDamageAdjWeapon(2))),
                    twoBatonCases(),
                    "Deals 15 Damage\n Add 2 damage for adj weapons\n");
        }

        return new MagicItem(
                "Apprentice Staff",
                1, 3, 1, prob, 20, false,
                new ArrayList<>(List.of(new AttackAllEnemies(3))),
                threeBatonCases(),
                "Deals 20 Damage \n Attack all enemies with 3");
    }

    // ================== POTIONS ==================

    static Potion generatePotion() {
        Random r = new Random();
        int prob = r.nextInt(100);

        if (prob < 70) {
            return new Potion(
                    "Blessed Potion",
                    prob,
                    "Adds 6 Poison to all enemies",
                    new ArrayList<>(List.of(new PoisonEffect(6), new NumberUses(1))));
        }

        if (prob < 90) {
            return new Potion(
                    "Heat Potion",
                    prob,
                    "Adds 6 Poison to all enemies",
                    new ArrayList<>(List.of(new PoisonEffect(6), new NumberUses(1), new RemovePoisonFromSelf())));
        }

        return new Potion(
                "Regen Potion",
                prob,
                "Adds 5 hp to hero\n add 2 energy", new ArrayList<>(List.of(
                        new AddHpHero(5),
                        new AddEnergyCost(2),
                        new NumberUses(1)))

        );
    }

    static ManaStone generateManaStone() {
        Random r = new Random();
        int prob = r.nextInt(100);

        if (prob < 90) {
            return new ManaStone(
                    "Crystale",
                    prob, true,
                    "Adds 1 mana to hero, remove 1 energy from hero",
                    new ArrayList<>(List.of(
                            new AddMana(1),
                            new AddEnergyCost(-1),
                            new NumberUses(1))));
        } else {
            return new ManaStone(
                    "Manarion",
                    prob, true,
                    "Adds 2 mana to hero\n",
                    new ArrayList<>(List.of(
                            new AddMana(2),
                            new NumberUses(1))));
        }
    }

    static Item generateWeapon(Random r) {
        if (r.nextInt(1) == 0) {
            return generateWeaponMelee(r);
        } else {
            return generateWeaponRanged(r);
        }
    }
    // ================== ITEM LIST GENERATION ==================

    static List<Item> generateItems() {
        Random r = new Random();
        int min = 4;
        int size = r.nextInt(10) + min;

        ArrayList<Item> items = new ArrayList<>();

        while (size-- > 0) {
            int roll = r.nextInt(9);

            Item item = switch (roll) {
                case 0 -> generateArmor();
                case 1 -> generateShield();
                case 2 -> generateFood();
                case 3 -> generatePotion();
                case 4 -> generateMagicItem(r);
                case 5 -> generateWeapon(r);
                case 6 -> generateGold();
                case 7 -> generateManaStone();
                default -> generateKey();
            };
            items.add(item);
        }

        // ensure at least one weapon
        if (items.stream().noneMatch(i -> i instanceof Weapon)) {
            items.add(generateWeapon(r));
        }

        return List.copyOf(items);
    }

    // Helper method: rotate a point 90 degrees clockwise within a bounding box of
    // given width and height
    private Point rotatePoint90Clockwise(Point p, int width, int height) {
        int x = p.x;
        int y = p.y;
        int newX = y;
        int newY = width - 1 - x;
        return new Point(newX, newY);
    }

    // Rotate all occupied points 90 degrees clockwise and update the occupiedCases
    // list accordingly
    public default List<Point> rotatePoints() {
        List<Point> rotated = new ArrayList<>();
        for (Point p : this.occupiedCases()) {
            rotated.add(rotatePoint90Clockwise(p, this.widthItem(), this.heightItem()));
        }
        // Clear and replace the occupiedCases with rotated points (modifies state!)
        this.occupiedCases().clear();
        this.occupiedCases().addAll(rotated);
        return rotated;
    }

}
