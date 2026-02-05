package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import Items.Gold;
import Items.Item;
import ui.ItemGraphique;

public record Merchant(ArrayList<Item> inventory) {

    // Compact constructor to ensure inventory is never null
    public Merchant {
        Objects.requireNonNull(inventory, "inventory cannot be null");
    }

    // Compact constructor to ensure inventory is never null
    public Merchant() {
        this(new ArrayList<>(Item.generateItems()));
    }

    /**
     * Attempts to sell an item to the hero.
     *
     * @param item the item to be sold
     * @param hero the hero who wants to buy the item
     * @return true if the hero has enough gold and the purchase succeeds, false
     *         otherwise
     */
    public boolean sellItem(Item item, Hero hero, HashMap<ItemGraphique, Item> itemsGraphique,
            ArrayList<ItemGraphique> itemDeparts, ItemGraphique dragging) {
        Objects.requireNonNull(item, "item cannot be null");
        Objects.requireNonNull(hero, "hero cannot be null");
        Objects.requireNonNull(itemsGraphique, "itemsGraphique cannot be null");
        Objects.requireNonNull(itemDeparts, "itemDeparts cannot be null");
        Objects.requireNonNull(dragging, "dragging cannot be null");
        System.out.println(" BUY ITEM CALLED: " + item.name());

        var backpack = hero.getBackpack();
        Point gPos = Gold.getGold(backpack);

        if (gPos == null) {
            System.out.println("NO GOLD IN BACKPACK");
            return false;
        }

        Gold gold = (Gold) backpack.getGrille()[gPos.x][gPos.y];
        int price = item.calculatePrice();

        System.out.println(
                "TRY BUY " + item.name()
                        + " | price=" + price
                        + " | gold=" + gold.goldValue());

        if (gold.goldValue() < price) {
            System.out.println("NOT ENOUGH GOLD");
            return false;
        }
        gold.setGoldValue(gold.goldValue() - price);
        System.out.println("BUY OK");

        // create and return a NEW item
        // item.createNewInstance();
        ItemGraphique ig = new ItemGraphique(item.name(), dragging.getImage());
        ig.SetscreenX(400);
        ig.SetscreenY(400);
        ig.SetscreenXO(400);
        ig.SetscreenYO(400);
        itemDeparts.add(ig);
        itemsGraphique.put(ig, item.createNewInstance());
        return true;
    }

    /**
     * Attempts to sell an item to the hero.
     *
     * @param item the item to be sold
     * @param hero the hero who wants to buy the item
     * @return true if the hero has enough gold and the purchase succeeds, false
     *         otherwise
     */
    public boolean BuyItem(Item item, Hero hero, HashMap<ItemGraphique, Item> itemsGraphique,
            ArrayList<ItemGraphique> itemDeparts, ArrayList<Item> itemMarchant, ItemGraphique dragging) {
        Objects.requireNonNull(item, "item cannot be null");
        Objects.requireNonNull(hero, "hero cannot be null");
        Objects.requireNonNull(itemsGraphique, "itemsGraphique cannot be null");
        Objects.requireNonNull(itemDeparts, "itemDeparts cannot be null");
        Objects.requireNonNull(dragging, "dragging cannot be null");
        System.out.println(" SELL ITEM CALLED: " + item.name());

        var backpack = hero.getBackpack();
        Point p = Gold.getGold(backpack);
        if (p == null) {
            return false;
        }
        Gold gold = (Gold) backpack.getGrille()[p.x][p.y];
        if (gold == null) {
            return false;
        }
        if (p != null) {

            int price = item.calculatePrice();

            System.out.println(
                    "TRY SELL " + item.name()
                            + " | price=" + price);
            itemMarchant.add(item.createNewInstance());
            System.out.println("SELL OK");
            System.out.println(itemDeparts);
            gold.setGoldValue(gold.goldValue() + price);
            System.out.println("SELL OK");
            itemDeparts.remove(dragging);
            itemsGraphique.remove(dragging);

            System.out.println(itemDeparts);
            return true;
        }
        return false;
    }
}
