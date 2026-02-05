package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import Items.Item;
import Items.Curse;
import model.BackPack;
import model.Dungeon;
import model.Hero;

/**
 * Interface representing a game screen.
 */
public interface Screen extends GameInterface {

    /**
     * Renders the screen.
     * 
     * @param g     Graphics context
     * @param realW Window width
     * @param realH Window height
     * @param hero  Hero reference
     */
    void render(Graphics2D g, int realW, int realH, Hero hero);

    /**
     * Handles mouse clicks.
     * 
     * @param x    Click X
     * @param y    Click Y
     * @param hero Hero reference
     * @param d    Dungeon reference
     */
    void onClick(int x, int y, Hero hero, Dungeon d);

    /**
     * @return true if the player can leave the screen
     */
    default boolean canLeaveScreen() {
        return true;
    }

    /**
     * Called when leaving the screen.
     * 
     * @param hero Hero reference
     */
    default void onLeaveScreen(Hero hero) {
    }

    /**
     * Handles mouse release.
     * 
     * @param x Release X
     * @param y Release Y
     */
    default void onRelease(int x, int y) {
    }

    /**
     * Handles mouse release with hero context.
     * 
     * @param x    Release X
     * @param y    Release Y
     * @param hero Hero reference
     */
    default void onRelease(int x, int y, Hero hero) {
    }

    /**
     * Loads an image from resources.
     * 
     * @param path Asset path
     * @return Loaded image
     */
    static BufferedImage load(String path) {
        try (var in = Screen.class.getResourceAsStream(path)) {
            return ImageIO.read(in);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Draws hero statistics on screen.
     * 
     * @param g    Graphics context
     * @param x    Start X
     * @param y    Start Y
     * @param hero Hero reference
     */
    default void drawStats(Graphics2D g, int x, int y, Hero hero) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        int line = y;
        g.drawString("===== HERO STATS =====", x, line);
        line += 25;
        g.drawString("Name       : " + hero.getName(), x, line);
        line += 20;
        g.drawString("Level      : " + hero.getLevel(), x, line);
        line += 20;
        g.drawString("XP         : " + hero.getXp(), x, line);
        line += 30;
        g.drawString("HP         : " + hero.getHp() + " / " + hero.getHpMax(), x, line);
        line += 20;
        g.drawString("Energy     : " + hero.getEnergy(), x, line);
        line += 20;
        g.drawString("Mana       : " + hero.getMana(), x, line);
        line += 20;
        g.drawString("Defence    : " + hero.getDefence(), x, line);
        line += 20;
        g.drawString("Protection : " + hero.getProtection(), x, line);
        line += 30;
    }

    /**
     * Populates items into the graphical list.
     * 
     * @param items          Models list
     * @param startCol       Starting column
     * @param startRow       Starting row
     * @param maxCols        Column count before wrapping
     * @param itemsDepart    Graphical list (out)
     * @param itemsGraphique Graphical mapping (out)
     */
    static void ajouterItemsFromList(List<Item> items, int startCol, int startRow, int maxCols,
            ArrayList<ItemGraphique> itemsDepart, HashMap<ItemGraphique, Item> itemsGraphique) {
        int col = startCol;
        int row = startRow;
        for (Item item : items) {
            ItemGraphique ig = new ItemGraphique(item.name(),
                    load("/ressources/icones/" + item.name().replace(" ", "_") + ".png"));
            ajouterItemDepart(ig, item, col, row, itemsDepart, itemsGraphique);
            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Adds an item to the starting list with fixed grid positioning.
     * 
     * @param it             Graphical item
     * @param model          Item model
     * @param col            Grid column
     * @param row            Grid row
     * @param itemsDepart    Starting list
     * @param itemsGraphique Graphical mapping
     */
    static void ajouterItemDepart(ItemGraphique it, Item model, int col, int row, ArrayList<ItemGraphique> itemsDepart,
            HashMap<ItemGraphique, Item> itemsGraphique) {
        Objects.requireNonNull(it);
        int baseX = 40;
        int baseY = 200;
        int cellW = 140;
        int cellH = 140;
        it.SetscreenX(baseX + col * cellW);
        it.SetscreenY(baseY + row * cellH);
        itemsDepart.add(it);
        itemsGraphique.put(it, model);
    }

    /**
     * Draws an item with its rotation.
     * 
     * @param g              Graphics context
     * @param it             Graphical item
     * @param itemsGraphique Graphical mapping
     * @param cellSize       Cell pixel size
     */
    static void drawItem(Graphics2D g, ItemGraphique it, HashMap<ItemGraphique, Item> itemsGraphique, int cellSize) {
        if (it.getImage() == null)
            return;
        Item item = itemsGraphique.get(it);
        if (item == null)
            return;

        int baseW = item.x() * cellSize;
        int baseH = item.y() * cellSize;
        int displayW = item.widthItem() * cellSize;
        int displayH = item.heightItem() * cellSize;
        int x = it.getScreenX();
        int y = it.getScreenY();
        int centerX = x + displayW / 2;
        int centerY = y + displayH / 2;
        AffineTransform old = g.getTransform();
        g.rotate(it.getRotation() * Math.PI / 2, centerX, centerY);
        int drawX = centerX - baseW / 2;
        int drawY = centerY - baseH / 2;
        g.drawImage(it.getImage(), drawX, drawY, baseW, baseH, null);
        g.setTransform(old);
        g.setColor(Color.GREEN);
        g.drawRect(x - 1, y - 1, displayW + 2, displayH + 2);
    }

    /**
     * Checks if coordinates are inside an item box.
     * 
     * @param ig             Graphical item
     * @param itemsGraphique Graphical mapping
     * @param x              Test X
     * @param y              Test Y
     * @param cellSize       Cell size
     * @return true if inside
     */
    static boolean inside(ItemGraphique ig, HashMap<ItemGraphique, Item> itemsGraphique, int x, int y, int cellSize) {
        if (ig == null)
            return false;
        Item item = itemsGraphique.get(ig);
        if (item == null)
            return false;
        int w = item.widthItem() * cellSize;
        int h = item.heightItem() * cellSize;
        int ix = ig.getScreenX();
        int iy = ig.getScreenY();
        return (x >= ix && x <= ix + w && y >= iy && y <= iy + h);
    }

    /**
     * Handles item rotation logic with centering.
     * 
     * @param selecteditem   Graphical item
     * @param itemsGraphique Graphical mapping
     * @param cellSize       Cell size
     */
    default void onRotate(ItemGraphique selecteditem, HashMap<ItemGraphique, Item> itemsGraphique, int cellSize) {
        if (selecteditem == null)
            return;
        Item item = itemsGraphique.get(selecteditem);
        if (item == null || item instanceof Curse)
            return;
        int oldW = item.widthItem() * cellSize;
        int oldH = item.heightItem() * cellSize;
        int centerX = selecteditem.getScreenX() + oldW / 2;
        int centerY = selecteditem.getScreenY() + oldH / 2;
        selecteditem.setRotation();
        item.rotatePoints();
        int newW = item.widthItem() * cellSize;
        int newH = item.heightItem() * cellSize;
        selecteditem.SetscreenX(centerX - newW / 2);
        selecteditem.SetscreenY(centerY - newH / 2);
    }

    /**
     * Draws detailed item info popup.
     * 
     * @param g              Graphics context
     * @param target         Targeted item
     * @param itemsGraphique Graphical mapping
     * @param screenWidth    Screen width for positioning
     */
    default void drawItemInfo(Graphics2D g, ItemGraphique target, HashMap<ItemGraphique, Item> itemsGraphique,
            int screenWidth) {
        if (target == null)
            return;
        Item item = itemsGraphique.get(target);
        if (item == null)
            return;
        int x = screenWidth - 260;
        int y = 100;
        int w = 240;
        int h = 300;
        g.setColor(new Color(30, 30, 30, 220));
        g.fillRoundRect(x, y, w, h, 20, 20);
        g.setColor(new Color(255, 215, 0));
        java.awt.Stroke oldStroke = g.getStroke();
        g.setStroke(new java.awt.BasicStroke(2));
        g.drawRoundRect(x, y, w, h, 20, 20);
        g.setStroke(oldStroke);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        java.awt.FontMetrics fm = g.getFontMetrics();
        int titleW = fm.stringWidth(item.name());
        g.drawString(item.name(), x + (w - titleW) / 2, y + 30);
        g.setFont(new Font("Arial", Font.ITALIC, 14));
        g.setColor(Color.CYAN);
        g.drawString("Rarity: " + item.rarity(), x + 15, y + 60);
        int yPos = y + 85;
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        if (item instanceof Items.MeleeWeapon melee) {
            g.drawString("Energy Cost: " + melee.energyCost(), x + 15, yPos);
            yPos += 20;
            g.drawString("Damage: " + melee.damage(), x + 15, yPos);
            yPos += 20;
        } else if (item instanceof Items.RangedWeapon ranged) {
            g.drawString("Energy Cost: " + ranged.energyCost(), x + 15, yPos);
            yPos += 20;
            g.drawString("Damage: " + ranged.damage(), x + 15, yPos);
            yPos += 20;
        } else if (item instanceof Items.Armor armor) {
            g.drawString("Energy Cost: " + armor.energyCost(), x + 15, yPos);
            yPos += 20;
            g.drawString("Block: " + armor.protectionValue(), x + 15, yPos);
            yPos += 20;
        } else if (item instanceof Items.Shield shield) {
            g.drawString("Energy Cost: " + shield.energyCost(), x + 15, yPos);
            yPos += 20;
            g.drawString("Block: " + shield.protectionValue(), x + 15, yPos);
            yPos += 20;
        } else if (item instanceof Items.MagicItem magic) {
            g.drawString("Mana Cost: " + magic.manaCost(), x + 15, yPos);
            yPos += 20;
            g.drawString("Damage: " + magic.damage(), x + 15, yPos);
            yPos += 20;
        } else if (item instanceof Items.Gold gold) {
            g.drawString("Value: " + gold.goldValue(), x + 15, yPos);
            yPos += 20;
        } else if (item instanceof Items.ManaStone) {
            g.drawString("Mana Restore: (check desc)", x + 15, yPos);
            yPos += 20;
        }
        g.setColor(Color.GRAY);
        g.drawLine(x + 10, yPos, x + w - 10, yPos);
        yPos += 20;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        String desc = item.description();
        if (desc != null) {
            drawStringMultiLine(g, desc, 210, x + 15, yPos);
        }
    }

    /**
     * Utility method to draw multi-line strings.
     * 
     * @param g         Graphics context
     * @param text      String to draw
     * @param lineWidth Line width limit
     * @param x         Start X
     * @param y         Start Y
     */
    private void drawStringMultiLine(Graphics2D g, String text, int lineWidth, int x, int y) {
        java.awt.FontMetrics m = g.getFontMetrics();
        if (m.stringWidth(text) < lineWidth) {
            g.drawString(text, x, y);
        } else {
            String[] words = text.split(" ");
            String currentLine = words[0];
            for (int i = 1; i < words.length; i++) {
                if (m.stringWidth(currentLine + " " + words[i]) < lineWidth) {
                    currentLine += " " + words[i];
                } else {
                    g.drawString(currentLine, x, y);
                    y += m.getHeight();
                    currentLine = words[i];
                }
            }
            if (currentLine.trim().length() > 0) {
                g.drawString(currentLine, x, y);
            }
        }
    }
}