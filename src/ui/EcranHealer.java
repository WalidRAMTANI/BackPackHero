package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Items.Item;
import Rooms.HealerRoom;
import model.BackPack;
import model.Dungeon;
import model.Hero;

public class EcranHealer implements Screen {

    // Reference to the main game window
    private final ZenGameView fenetre;

    // Background image of the game screen
    private BufferedImage background;

    // Image of the hero displayed on screen
    private BufferedImage heroine;

    // Image of the hero displayed on screen
    private BufferedImage healer;

    // Hero's backpack containing items
    private BackPack sac;

    // Map linking graphical items to their logical model items
    private HashMap<ItemGraphique, Item> itemsGraphique;

    // Size in pixels of one cell in the backpack grid
    private int cellSize;

    // Coordinates (top-left corner) of the backpack on the screen
    private int sacX, sacY;

    // display button to heal
    private Button button;
    private HealerRoom healerRoom;

    /**
     * Constructor: Initializes the game screen with references to the main window
     * and the hero.
     * Loads images and initial items outside the backpack.
     */
    public EcranHealer(ZenGameView fenetre, Hero hero, HealerRoom room) {
        Objects.requireNonNull(fenetre);
        Objects.requireNonNull(hero);
        Objects.requireNonNull(room);
        this.fenetre = fenetre;
        this.healerRoom = room;
        // Get the current graphical items from the game window
        this.itemsGraphique = fenetre.getItems();

        // Get the hero's backpack model
        sac = hero.getBackpack();

        // Load the background image
        chargerBG();
        // Load hero image
        chargerImages();
    }

    /**
     * Load the background image for the game screen.
     */
    private void chargerBG() {
        background = Screen.load("/ressources/fond/healer_fond.png");
    }

    /**
     * Load the hero image.
     */
    private void chargerImages() {
        heroine = Screen.load("/ressources/hero/heroine.png");
    }

    /**
     * Draws the hero's statistics panel.
     */
    public void drawHeroStats(Graphics2D g, Hero hero) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(hero);
        int x = 20;
        int y = 20;
        int width = 240;
        int height = 210;

        // Draw background panel with transparency
        g.setColor(new Color(30, 30, 30, 200));
        g.fillRoundRect(x, y, width, height, 20, 20);

        // Draw border around panel
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, width, height, 20, 20);

        // Draw hero name as title
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString(hero.getName(), x + 15, y + 30);

        // Draw hero stats with different colors and lines
        g.setFont(new Font("Arial", Font.PLAIN, 15));

        int line = 60; // Starting vertical position for stats

        g.setColor(Color.RED);
        g.drawString("HP: " + hero.getHp() + "/" + hero.getHpMax(), x + 15, y + line);
        line += 22;

        g.setColor(Color.YELLOW);
        g.drawString("Energy: " + hero.getEnergy(), x + 15, y + line);
        line += 22;

        g.setColor(new Color(100, 150, 255));
        g.drawString("Mana: " + hero.getMana(), x + 15, y + line);
        line += 22;

        g.setColor(Color.WHITE);
        g.drawString("Level: " + hero.getLevel(), x + 15, y + line);
        line += 22;

        g.setColor(Color.GREEN);
        g.drawString("XP: " + hero.getXp(), x + 15, y + line);
        line += 22;

        g.setColor(Color.CYAN);
        g.drawString("Defence: " + hero.getDefence(), x + 15, y + line);
        line += 22;

        g.setColor(Color.ORANGE);
        g.drawString("Protection: " + hero.getProtection(), x + 15, y + line);
    }

    /**
     * Renders the entire game screen including background, hero, backpack, and
     * items.
     * 
     * @param g    Graphics2D context to draw on
     * @param W    Width of the drawing area
     * @param H    Height of the drawing area
     * @param hero Current hero object
     */
    @Override
    public void render(Graphics2D g, int W, int H, Hero hero) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(hero);
        // Draw background if loaded
        if (background != null) {
            g.drawImage(background, 0, 0, W, H, null);
        }

        // Draw hero image centered near the bottom of the screen
        if (heroine != null) {
            int heroW = W / 5; // width of hero image
            int heroH = (int) (heroine.getHeight() * (heroW / (double) heroine.getWidth())); // height keeping aspect
                                                                                             // ratio
            g.drawImage(heroine, W / 2 - heroW / 2, H - heroH - 80, heroW, heroH, null);
        }

        // Draw hero image centered near the bottom of the screen
        if (healer != null) {
            int heroW = W / 7; // width of hero image
            int heroH = (int) (healer.getHeight() * (heroW / (double) healer.getWidth())); // height keeping aspect
                                                                                           // ratio
            g.drawImage(healer, 3 * W / 4 - heroW / 2, H - heroH - 30, heroW, heroH, null);
        }

        // Draw backpack background panel with some padding and rounded corners
        boolean[][] open = sac.getOpenGrid(); // get grid cells availability
        int rows = open.length;
        int cols = open[0].length;

        // Compute cell size to fit backpack grid and margin inside screen
        cellSize = Math.min(W / (cols + 14), H / (rows + 12));
        int sacW = cols * cellSize; // backpack width in pixels
        int sacH = rows * cellSize; // backpack height in pixels

        // Position backpack centered horizontally and near top vertically
        sacX = (W - sacW) / 2;
        sacY = H / 10;

        // Draw translucent background rectangle for backpack grid
        g.setColor(new Color(20, 0, 40, 150));
        g.fillRoundRect(sacX - 15, sacY - 15, sacW + 30, sacH + 30, 25, 25);

        // Draw each cell in the backpack grid
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = sacX + c * cellSize;
                int y = sacY + r * cellSize;
                // Color depends on if the cell is open or closed
                g.setColor(open[r][c] ? new Color(90, 0, 160, 150) : new Color(20, 0, 30, 120));
                g.fillRoundRect(x, y, cellSize - 6, cellSize - 6, 15, 15);
            }
        }
        // Draw all items inside the backpack (not in itemsDepart)
        for (Map.Entry<ItemGraphique, Item> entry : itemsGraphique.entrySet()) {
            ItemGraphique ig = entry.getKey();
            drawItem(g, ig);
        }

        // draw the heal button
        drawEndTurnRemark(g);
        drawHeroStats(g, hero);
    }

    /**
     * Draws the "ENTER DUNGEON" button on screen.
     */
    public void drawEndTurnRemark(Graphics2D g) {
        Objects.requireNonNull(g);
        // Button size
        int w = 140;
        int h = 50;

        // Position: TOP-RIGHT of the screen
        // Assuming you know screenWidth — if not, replace with your panel width
        int x = fenetre.getRealWidth() - w - 20;
        int y = 20;

        button = new Button(x, y, w, h);

        // Background
        g.setColor(new Color(40, 40, 40));
        g.fillRoundRect(x, y, w, h, 15, 15);

        // Border
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, w, h, 15, 15);

        // Text
        String text = "Press S to heal";
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.WHITE);

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int textX = x + (w - textWidth) / 2;
        int textY = y + (h + textHeight) / 2 - 4;

        g.drawString(text, textX, textY);

    }

    /**
     * Draws a single item at its current screen position with correct size.
     * 
     * @param g  Graphics2D context
     * @param it Graphical item to draw
     */
    private void drawItem(Graphics2D g, ItemGraphique it) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(it);
        if (it.getImage() == null) {
            return; // skip if no image loaded
        }

        // Get the model item associated with this graphical item
        var item = itemsGraphique.get(it);

        // Get shape (occupied cells) to calculate width and height in cells
        var shape = item.occupiedCases();

        // Width and height in cells (grid units)
        int wCases = shape.stream().mapToInt(p -> p.x).max().orElse(0) + 1;
        int hCases = shape.stream().mapToInt(p -> p.y).max().orElse(0) + 1;

        // Convert to pixel size using cellSize
        int w = wCases * cellSize;
        int h = hCases * cellSize;

        // Draw the image at the item's screen position with correct size
        g.drawImage(it.getImage(), it.getScreenX(), it.getScreenY(), w, h, null);

        // Optionally, you could draw rotated image here if needed
        // it.drawRotatedImage(g, it.getImage(), it.getScreenX(), it.getScreenY(),
        // it.getRotation() * Math.PI);
    }

    @Override
    public void onClick(int x, int y, Hero hero, Dungeon d) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(d);
        // TODO Auto-generated method stub

    }

    public void heal(Hero hero) {
        Objects.requireNonNull(hero);
        System.out.println("I'm here");
        if (!healerRoom.getHealed()) {
            if (hero.setHp(hero.getHp() + healerRoom.getHealer().healAmount())) {
                healerRoom.setHealed(true);
            }

        } else {
            System.out.println("already healed");
        }
    }

}
