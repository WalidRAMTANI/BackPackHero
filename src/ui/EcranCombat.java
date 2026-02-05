package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import Enemies.Enemy;
import Enemies.LivingShadow;
import Enemies.Ratwolf;
import Enemies.QueenBee;
import Enemies.SmallRatwolf;
import Enemies.FrogWizard;
import Items.Weapon;
import Items.Item;
import Items.Curse;
import Rooms.EnemyRoom;
import model.BackPack;
import model.Dungeon;
import model.Hero;

public class EcranCombat implements Screen, DragSupport {
    // a lis of the cells to expand the backpack
    ArrayList<Point> expandBag;
    // expand cells list
    ArrayList<Point> availableCells;
    // boolean for combat mode
    boolean combatMode = true;
    // Reference to the game window/view
    private final ZenGameView fenetre;
    private BufferedImage background; // Background image for combat screen
    private BufferedImage heroine; // Heroine image
    private BackPack sac; // Hero's backpack model

    // Maps graphic item objects to their model item
    private HashMap<ItemGraphique, Item> itemsGraphique;

    // List of items outside the backpack (on the side)
    private ArrayList<ItemGraphique> itemsDepart = new ArrayList<>();
    private ItemGraphique currentHoveredItem = null;
    private ItemGraphique dragging = null; // Currently dragged item graphic
    private ItemGraphique selecteditem = null; // currenly dragged item graphic for after combat
    private int dragOffsetX, dragOffsetY; // Offset of mouse click relative to dragged item

    private int cellSize; // Size of each cell in the backpack grid
    private int sacX, sacY; // Top-left coordinates of backpack grid on screen

    // Maps graphic enemy objects to their enemy model
    private HashMap<EnemyGraphique, Enemy> enemyGraphique;
    
 
    private boolean showVictory = false;
    private boolean showDefeat = false;
    
 // end message timing
    private long endMessageStartTime = 0;
    private static final long END_MESSAGE_DURATION = 2500; // 2.5 seconds




    private EnemyRoom room; // Current combat room holding enemies
    private int totalXP = 0; // Total XP from enemies in this room

    /**
     * Loads enemies for the combat screen and sets up their graphical
     * representation.
     * 
     * @param screenW Width of the screen
     * @param screenH Height of the screen
     */
    private void chargerEnemies(int screenW, int screenH) {
        // Temporary: Create a floor and get the enemy room at position [3][3]

        enemyGraphique = new HashMap<>();
        if (room == null) {
            return;
        }
        var enemies = room.enemies();
        if (enemies == null || enemies.isEmpty()) {
            return;
        }

        // Desired width and height for each enemy image
        int desiredWidth = Math.min(120, screenW / 8);
        int desiredHeight = (int) (desiredWidth * 1.75);
        int marginSouth = 30;
        int marginRight = 100;
        int gap = 20;

        // Vertical position for enemies
        int y = screenH - desiredHeight - marginSouth;

        // Calculate total width occupied by enemies with gaps
        int totalWidth = enemies.size() * desiredWidth + (enemies.size() - 1) * gap;

        // Start X to align enemies to the right side of the screen with margin
        int startX = screenW - marginRight - totalWidth;

        int index = 0;
        for (Enemy enemy : enemies) {
            // Determine enemy image name based on enemy type
            String name = switch (enemy) {
                case Ratwolf _ -> "RatLoup";
                case SmallRatwolf _ -> "PetitRatLoup";
                case FrogWizard _ -> "SorciereGrenouille";
                case QueenBee _ -> "ReineAbeille";
                case LivingShadow _ -> "OmbreVivante";
                default -> "UnknownEnemy";
            };

            // Load the enemy image
            BufferedImage eimage = Screen.load("/ressources/enemies/" + name + ".png");

            // Calculate enemy's X position on screen
            int x = startX + index * (desiredWidth + gap);

            // Create a graphic representation of enemy
            EnemyGraphique eg = new EnemyGraphique(eimage, x, y, desiredWidth, desiredHeight);
            index++;
            // Prepare enemy AI actions (called here on every enemy)
            room.enemies().forEach(e -> room.chooseActionEnemies(e, 2));

            // Store graphic-enemy pair
            enemyGraphique.put(eg, enemy);

        }
    }

    /**
     * Draws the "End Turn" button on screen.
     */
    public void drawButtonRemark(Graphics2D g, String text) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(g);
        // Button size
        int w = 160;
        int h = 50;
        int x = fenetre.getRealWidth() - w - 20;
        int y = 20;
        // endTurnButton = new Button(x, y, w, h);

        // Background
        g.setColor(new Color(40, 40, 40));
        g.fillRoundRect(x, y, w, h, 15, 15);

        // Border
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, w, h, 15, 15);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.WHITE);

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int textX = x + (w - textWidth) / 2;
        int textY = y + (h + textHeight) / 2 - 4;

        g.drawString(text, textX, textY);

    }
    
    // when the hero wins the fight
    private void drawVictoryMessage(Graphics2D g, int W, int H) {
        String text = "VICTORY! All enemies defeated";

        g.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int x = (W - textWidth) / 2;
        int y = H / 2;

        // Dark overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, W, H);

        // Green message background
        g.setColor(new Color(0, 160, 80, 220));
        g.fillRoundRect(x - 25, y - textHeight - 20,
                textWidth + 50, textHeight + 40, 20, 20);

        // Text
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
    }
    
 //  when the hero dies
    private void drawDefeatMessage(Graphics2D g, int W, int H) {
        String text = "DEFEAT... The hero has fallen";

        g.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int x = (W - textWidth) / 2;
        int y = H / 2;

        // Dark overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, W, H);

        // Red message background
        g.setColor(new Color(180, 40, 40, 220));
        g.fillRoundRect(x - 25, y - textHeight - 20,
                textWidth + 50, textHeight + 40, 20, 20);

        // Text
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
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
     * Draws a health bar above the enemy graphic.
     */
    public void drawHealthBar(Graphics2D g, Enemy enemy, EnemyGraphique eg) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(enemy);
        Objects.requireNonNull(eg);
        int x = eg.getScreenX();
        int lift = 80;
        int y = eg.getScreenY() - 15 - lift;
        int w = eg.getWidth();
        int h = 8; // Height of health bar

        // Calculate health ratio
        float ratio = (float) enemy.hp() / enemy.hpMax();
        int filled = (int) (w * ratio);

        // Draw background of health bar (black)
        g.setColor(Color.BLACK);
        g.fillRect(x, y, w, h);

        // Draw health portion (red if low health, green otherwise)
        g.setColor(ratio < 0.3f ? Color.RED : Color.GREEN);
        g.fillRect(x, y, filled, h);

        // Draw border around health bar
        g.setColor(Color.WHITE);
        g.drawRect(x, y, w, h);
    }

    private void drawEnemies(Graphics2D g) {
        Objects.requireNonNull(g);
        if (enemyGraphique == null) {
            return;
        }

        g.setFont(new Font("Arial", Font.BOLD, 14));

        for (Map.Entry<EnemyGraphique, Enemy> entry : enemyGraphique.entrySet()) {
            EnemyGraphique eg = entry.getKey();
            Enemy enemy = entry.getValue();

            // Draw health bar first
            drawHealthBar(g, enemy, eg);

            // Draw enemy image
            int w = (int) (eg.getWidth() * 1.30); // +15%
            int h = (int) (eg.getHeight() * 1.30);

            int lift = 80;

            int x = eg.getScreenX() - (w - eg.getWidth()) / 2;
            int y = eg.getScreenY() - (h - eg.getHeight()) / 2 - lift;

            g.drawImage(eg.getImage(), x, y, w, h, null);

            // Draw actions above the enemy
            var actions = enemy.actions();
            if (actions != null && !actions.isEmpty()) {
                String actionsText = actions.stream()
                        .map(this::actionToString)
                        .collect(Collectors.joining(", "));

                // Background for readability
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(actionsText);
                int textHeight = fm.getHeight();

                int padding = 4;
                int bgX = eg.getScreenX();
                int bgY = eg.getScreenY() - textHeight - 8; // position above enemy

                g.setColor(new Color(0, 0, 0, 150)); // semi-transparent black
                g.fillRoundRect(bgX - padding, bgY - padding, textWidth + 2 * padding, textHeight + 2 * padding, 8, 8);

                g.setColor(Color.WHITE);
                g.drawString(actionsText, bgX, bgY + fm.getAscent());
            }
        }
    }

    private void drawEnemyStats(Graphics2D g, Enemy enemy, int x, int y) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(enemy);
        if (enemy == null)
            return;

        int w = 240;
        int h = 180;

        g.setColor(new Color(90, 30, 120, 170));
        g.fillRoundRect(x, y, w, h, 18, 18);

        g.setColor(new Color(200, 120, 255));
        g.drawRoundRect(x, y, w, h, 18, 18);

        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString(enemy.getClass().getSimpleName(), x + 12, y + 22);

        g.setFont(new Font("Serif", Font.BOLD, 16));
        g.setColor(new Color(255, 200, 255));
        g.drawString(enemy.getClass().getSimpleName(), x + 12, y + 22);

        // barre HP
        int barX = x + 12;
        int barY = y + 50;
        int barW = w - 24;
        int barH = 8;

        float ratio = (float) enemy.hp() / enemy.hpMax();
        int filled = (int) (barW * ratio);

        g.setColor(new Color(40, 0, 60));
        g.fillRect(barX, barY, barW, barH);

        g.setColor(new Color(255, 80, 150));
        g.fillRect(barX, barY, filled, barH);

        g.setColor(new Color(200, 120, 255));
        g.drawRect(barX, barY, barW, barH);

        g.setColor(Color.WHITE);
        g.drawString("ATK: " + enemy.attack(), x + 12, y + 75);
        g.drawString("DEF: " + enemy.defense(), x + 120, y + 75);

        // Draw Description
        g.setColor(new Color(200, 200, 200));
        g.setFont(new Font("Arial", Font.ITALIC, 12));
        String desc = enemy.description();
        if (desc != null) {
            drawStringMultiLine(g, desc, 220, x + 12, y + 95);
        }
    }

    private void drawEnemiesStats(Graphics2D g) {
        Objects.requireNonNull(g);
        if (enemyGraphique == null || enemyGraphique.isEmpty())
            return;

        int x = 20;
        int startY = 250; // sous la fiche héros
        int gap = 10; // espace entre les fiches
        int cardHeight = 180;

        int i = 0;
        for (Enemy enemy : enemyGraphique.values()) {
            if (i >= 3)
                break; // max 3 fiches

            int y = startY + i * (cardHeight + gap);
            drawEnemyStats(g, enemy, x, y);

            i++;
        }
    }

    // Helper method to convert action codes to strings
    private String actionToString(int actionCode) {
        return switch (actionCode) {
            case 1 -> "Attack";
            case 2 -> "Defend";
            case 3 -> "Effect";
            default -> "Unknown";
        };
    }

    /**
     * Constructor initializes the combat screen with the game view and hero.
     */
    public EcranCombat(ZenGameView fenetre, Hero hero, EnemyRoom room) {
        Objects.requireNonNull(fenetre);
        Objects.requireNonNull(hero);
        Objects.requireNonNull(room);
        this.expandBag = new ArrayList<Point>();
        this.fenetre = fenetre;
        this.itemsGraphique = fenetre.getItems(); // Get all items from the view
        this.sac = hero.getBackpack();
        sac.activate(hero);
        this.room = room;
        availableCells = sac.getExpandList();
        this.itemsDepart = new ArrayList<>();
        chargerBG(); // Load background image
        chargerImages(); // Load heroine image
        chargerEnemies(fenetre.getRealWidth(), fenetre.getRealHeight()); // Load enemies for screen

        // Calculate total XP from enemies
        this.totalXP = room.enemies().stream().mapToInt(Enemy::exp).sum();
    }

    /**
     * Loads the background image.
     */
    private void chargerBG() {
        background = Screen.load("/ressources/fond/map_fond.png");
    }

    /**
     * Loads the heroine image.
     */
    private void chargerImages() {
        heroine = Screen.load("/ressources/hero/heroine.png");
    }

    /**
     * Loads and creates all items displayed outside the backpack at game start.
     * Each item has a shape defined by a list of occupied points (cells).
     */
    private void chargerItemsDepart(List<Item> items) {
        Objects.requireNonNull(items);
        int colCount = 4;
        int index = 0;
        for (Item item : items) {
            int x = index % colCount;
            int y = index / colCount;

            // Use lowercase for curse items, normal case for others
            String imageName = item.name().replace(" ", "_");
            if (item instanceof Curse) {
                imageName = imageName.toLowerCase();
            }

            ItemGraphique ig = new ItemGraphique(item.name(),
                    Screen.load("/ressources/icones/" + imageName + ".png"));
            ajouterItemDepart(ig, item, x, y);

            index++;
        }
    }

    /**
     * Adds an item graphic outside of the backpack at the given grid coordinates.
     */

    private void ajouterItemDepart(ItemGraphique it, Item model, int col, int row) {
        Objects.requireNonNull(it);
        Objects.requireNonNull(model);
        int baseX = fenetre.getRealWidth() * 3 / 4;
        int baseY = fenetre.getRealHeight() / 2;
        int cellW = 140;
        int cellH = 140;

        // Position the item outside the backpack grid with spacing
        it.SetscreenX(baseX + col * cellW);
        it.SetscreenY(baseY + row * cellH);

        itemsDepart.add(it);
        itemsGraphique.put(it, model);
    }

    private void drawSelectedCases(Graphics2D g, ArrayList<Point> cases) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(cases);
        if (cases.isEmpty())
            return;

        g.setColor(Color.GREEN);
        g.setStroke(new BasicStroke(3));

        for (Point p : cases) {
            int x = sacX + p.x * cellSize;
            int y = sacY + p.y * cellSize;
            g.drawRect(x, y, cellSize, cellSize);
        }
    }

    private void drawAvailableCases(Graphics2D g, ArrayList<Point> cases) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(cases);
        if (cases.isEmpty())
            return;

        g.setColor(new Color(255, 215, 0)); // doré
        g.setStroke(new BasicStroke(2));

        for (Point p : cases) {
            int x = sacX + p.x * cellSize;
            int y = sacY + p.y * cellSize;

            g.drawRect(x, y, cellSize, cellSize);
        }
    }

    /**
     * Render the entire combat screen.
     */
    @Override
    public void render(Graphics2D g, int W, int H, Hero hero) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(hero);
        // Draw background
        if (background != null) {
            g.drawImage(background, 0, 0, W, H, null);
        }

        // Draw heroine image centered near bottom of screen
        if (heroine != null) {
            int heroW = W / 5;
            int heroH = (int) (heroine.getHeight() * (heroW / (double) heroine.getWidth()));
            g.drawImage(heroine, W / 2 - heroW / 2, H - heroH - 80, heroW, heroH, null);
        }

        // Draw hero stats panel
        drawHeroStats(g, hero);

        if (combatMode) {
            // Draw the "End Turn" Mark
            drawButtonRemark(g, "Press K to end");
        } else {
            // Draw the "Expand" Mark
            drawButtonRemark(g, "Press A to expand");
        }

        // Draw backpack grid background
        boolean[][] open = sac.getOpenGrid();
        int rows = open.length;
        int cols = open[0].length;

        // Calculate cell size to fit backpack and surrounding space
        cellSize = Math.min(W / (cols + 14), H / (rows + 12));
        int sacW = cols * cellSize;
        int sacH = rows * cellSize;
        sacX = (W - sacW) / 2;
        sacY = H / 10;

        // Draw backpack background panel with transparency
        g.setColor(new Color(20, 0, 40, 150));
        g.fillRoundRect(sacX - 15, sacY - 15, sacW + 30, sacH + 30, 25, 25);

        // Draw each backpack cell (color depends if open or not)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = sacX + c * cellSize;
                int y = sacY + r * cellSize;
                g.setColor(open[r][c] ? new Color(90, 0, 160, 150) : new Color(20, 0, 30, 120));
                g.fillRoundRect(x, y, cellSize - 6, cellSize - 6, 15, 15);
            }
        }

        // Draw all items inside the backpack (not in itemsDepart)
        for (Map.Entry<ItemGraphique, Item> entry : itemsGraphique.entrySet()) {
            ItemGraphique ig = entry.getKey();
            if (!itemsDepart.contains(ig)) {
                drawItem(g, ig);
            }
        }

        // Draw items outside the backpack (itemsDepart)
        for (ItemGraphique it : itemsDepart) {
            drawItem(g, it);
        }

        // Draw all enemies with their health bars
        drawEnemies(g);
        // draw the available cells to expand and selected ones after the combat
        if (combatMode == false) {
            drawAvailableCases(g, availableCells);
            drawSelectedCases(g, expandBag);
        }
        if (currentHoveredItem != null) {
            // drawItemDescriptionGraphics(g, 50, fenetre.getRealHeight() - 300);
        }
        drawEnemiesStats(g);
        drawItemInfo(g);
        
        if (showVictory) {
            drawVictoryMessage(g, W, H);
        }

        if (showDefeat) {
            drawDefeatMessage(g, W, H);
        }
        if ((showVictory || showDefeat) &&
        	    System.currentTimeMillis() - endMessageStartTime > END_MESSAGE_DURATION) {

        	    showVictory = false;
        	    showDefeat = false;
        	}


        

    }

    /**
     * Draws the information popup for the selected item.
     */
    private void drawItemInfo(Graphics2D g) {
        Objects.requireNonNull(g);
        ItemGraphique target = dragging != null ? dragging : selecteditem;
        if (target == null)
            return;

        Item item = itemsGraphique.get(target);
        if (item == null)
            return;

        int x = fenetre.getRealWidth() - 260;
        int y = 100; // Below "End Turn" button
        int w = 240;
        int h = 300;

        // Background
        g.setColor(new Color(30, 30, 30, 220));
        g.fillRoundRect(x, y, w, h, 20, 20);

        // Border
        g.setColor(new Color(255, 215, 0)); // Gold color
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, w, h, 20, 20);

        // Title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        int titleW = fm.stringWidth(item.name());
        g.drawString(item.name(), x + (w - titleW) / 2, y + 30);

        // Rarity
        g.setFont(new Font("Arial", Font.ITALIC, 14));
        g.setColor(Color.CYAN);
        g.drawString("Rarity: " + item.rarity(), x + 15, y + 60);

        // Specific Stats
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

        // Description separator
        g.setColor(Color.GRAY);
        g.drawLine(x + 10, yPos, x + w - 10, yPos);
        yPos += 20;

        // Description
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        String desc = item.description();
        if (desc != null) {
            drawStringMultiLine(g, desc, 210, x + 15, yPos);
        }
    }

    private void drawStringMultiLine(Graphics2D g, String text, int lineWidth, int x, int y) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(text);

        FontMetrics m = g.getFontMetrics();
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

    /**
     * Draws an individual item graphic.
     */
    private void drawItem(Graphics2D g, ItemGraphique it) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(it);
        Screen.drawItem(g, it, itemsGraphique, cellSize);
    }

    // Handles click when hero fight enemies.
    void combatClick(int x, int y, Hero hero) {
        Objects.requireNonNull(hero);
        // Case: Using an item on the hero while dragging
        if (dragging != null) {
            if (x >= sacX && x <= sacX + cellSize * 7 &&
                    y >= sacY && y <= sacY + cellSize * 5) {

                int col = Math.min(Math.max((x - sacX) / cellSize, 0), 6);
                int row = Math.min(Math.max((y - sacY) / cellSize, 0), 4);

                Item itemInSac = sac.getItemAtPoint(row, col);
                Item model = itemsGraphique.get(dragging);

                // If the item clicked is the same as the dragged item, use it on hero (except
                // weapon)
                if (itemInSac != null && model.equals(itemInSac) && !(itemInSac instanceof Weapon)) {
                    model.onUse(hero, null, room.enemies());
                    if (!sac.getItems().containsKey(model)) {
                        sac.removeItem(row, col); // Remove item from backpack
                        itemsGraphique.remove(dragging); // Remove from graphics map
                        itemsDepart.remove(dragging); // Remove from outside items
                    }

                    dragging = null;
                    return;
                }
            }
        }

        // Case: Click on an item inside the backpack to pick it up (start dragging)
        if (x >= sacX && x <= sacX + cellSize * 7 &&
                y >= sacY && y <= sacY + cellSize * 5) {

            int col = Math.min(Math.max((x - sacX) / cellSize, 0), 6);
            int row = Math.min(Math.max((y - sacY) / cellSize, 0), 4);

            Item itemInSac = sac.getItemAtPoint(row, col);

            if (itemInSac != null) {
                sac.removeItem(row, col); // Remove item from backpack model

                // Find the corresponding graphic item for the model item
                dragging = itemsGraphique.entrySet().stream()
                        .filter(e -> e.getValue() == itemInSac)
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);

                if (dragging != null) {
                    dragging.SetscreenXO(dragging.getScreenX());
                    dragging.SetscreenYO(dragging.getScreenY());

                    dragOffsetX = dragOffsetY = 0;

                    if (!itemsDepart.contains(dragging))
                        itemsDepart.add(dragging);
                    return;
                }
            }
        }
    }
    
    

    private void startDraggingOutsideItem(ItemGraphique ig, int x, int y) {
        Objects.requireNonNull(ig);
        dragging = ig;
        dragOffsetX = x - ig.getScreenX();
        dragOffsetY = y - ig.getScreenY();
    }

    // Handles clicks after the fight , for bag expansion, and awards.
    private boolean isInsideBackpack(int x, int y) {
        return x >= sacX && x <= sacX + cellSize * 7 &&
                y >= sacY && y <= sacY + cellSize * 5;
    }

    private int getBackpackColumn(int x) {
        return (x - sacX) / cellSize;
    }

    private int getBackpackRow(int y) {
        return (y - sacY) / cellSize;
    }

    private void pickItemFromBackpack(Item item, int x, int y, int row, int col) {
        Objects.requireNonNull(item);
        sac.removeItem(row, col);

        dragging = findGraphicalItem(item);

        if (dragging != null) {
            initializeDraggingFromBackpack(x, y);
            ensureItemInOutsideList();
        }
    }

    private ItemGraphique findGraphicalItem(Item item) {
        Objects.requireNonNull(item);
        return itemsGraphique.entrySet().stream()
                .filter(e -> e.getValue() == item)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private void initializeDraggingFromBackpack(int x, int y) {
        if (dragging == null)
            return;
        dragging.SetscreenXO(dragging.getScreenX());
        dragging.SetscreenYO(dragging.getScreenY());
        dragOffsetX = x - dragging.getScreenX();
        dragOffsetY = y - dragging.getScreenY();
    }

    private void ensureItemInOutsideList() {
        if (!itemsDepart.contains(dragging)) {
            itemsDepart.add(dragging);
        }
    }

    private boolean tryPickFromBackpack(int x, int y) {
        if (!isInsideBackpack(x, y))
            return false;

        int col = getBackpackColumn(x);
        int row = getBackpackRow(y);

        Item item = sac.getItemAtPoint(row, col);
        if (item == null)
            return false;

        pickItemFromBackpack(item, x, y, row, col);
        return true;
    }

    private void tryPickFromOutside(int x, int y) {
        for (ItemGraphique ig : itemsDepart) {
            if (Screen.inside(ig, itemsGraphique, x, y, cellSize)) {
                startDraggingOutsideItem(ig, x, y);
                return;
            }
        }
    }

    /**
     * Handles mouse click events on the combat screen.
     */
    @Override
    public void onClick(int x, int y, Hero hero, Dungeon d) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(d);
        if (combatMode) {
            combatClick(x, y, hero);
        } else {
            if (tryPickFromBackpack(x, y))
                return;
            tryPickFromOutside(x, y);
        }
    }

    public void afterReleaseCombat(int x, int y, Hero hero) {
        Objects.requireNonNull(hero);
        selecteditem = dragging; // update selected item
        if (dragging != null) {
            // Check if dropped inside backpack area
            if (x >= sacX && x <= (sacX + cellSize * 7) && y >= sacY && y <= (sacY + cellSize * 5)) {
                // Calculate grid cell for drop
                int col = Math.min(Math.max((dragging.getScreenX() - sacX) / cellSize, 0), 6);
                int row = Math.min(Math.max((dragging.getScreenY() - sacY) / cellSize, 0), 4);
                // Snap item to cell grid position
                dragging.SetscreenX(sacX + col * cellSize);
                dragging.SetscreenY(sacY + row * cellSize);

                // Get model item
                Item model = itemsGraphique.get(dragging);

                // Attempt to place the item in the backpack model
                if (!sac.placeItem(model, row, col, itemsGraphique, itemsDepart, dragging, true)) {
                    // Placement failed → revert to original position
                    dragging.SetscreenX(dragging.getScreenXO());
                    dragging.SetscreenY(dragging.getScreenYO());
                }
            } else {
                // Dropped outside backpack → stay on left panel
                if (!itemsDepart.contains(dragging)) {
                    itemsDepart.add(dragging);
                }
            }

            selecteditem = dragging;
            dragging = null; // stop dragging
        } else {
            int col = Math.min(Math.max((x - sacX) / cellSize, 0), 6);
            int row = Math.min(Math.max((y - sacY) / cellSize, 0), 4);
            Point p = new Point(col, row);

            // if we click on the same cell two times we cancel it
            if (expandBag.contains(p)) {
                expandBag.remove(p);
            } else if (sac.canExpand(row, col)) {
                // we take only four cells max.
                if (expandBag.size() < 4) {
                    expandBag.add(p);
                }
            }
        }
    }

    public void releaseCombat(int x, int y, Hero hero) {
        Objects.requireNonNull(hero);
        if (dragging != null) {
            for (var e : enemyGraphique.keySet()) {
                if (x >= e.getScreenX() && x <= (e.getScreenX() + e.getWidth()) &&
                        y >= e.getScreenY() && y <= (e.getScreenY() + e.getHeight())) {

                    int col = Math.min(Math.max((dragging.getScreenXO() - sacX) / cellSize, 0), 6);
                    int row = Math.min(Math.max((dragging.getScreenYO() - sacY) / cellSize, 0), 4);

                    dragging.SetscreenX(sacX + col * cellSize);
                    dragging.SetscreenY(sacY + row * cellSize);

                    Item model = itemsGraphique.get(dragging);

                    Enemy new_enemy = enemyGraphique.get(e);
                    model.onUse(hero, new_enemy, room.enemies());
                    if (new_enemy.isDead()) {
                        room.enemies().remove(new_enemy);
                        enemyGraphique.remove(e);
                        // room.setEnnemies(tempList);

                        if (room.enemies().isEmpty()) {
                            room.setCleared(true);
                            hero.gainXP(totalXP);
                            hero.levelUp();
                           
                            showVictory = true;
                            endMessageStartTime = System.currentTimeMillis();
                            combatMode = false;


                            for (var item : itemsDepart) {
                                itemsGraphique.remove(item);
                            }

                            itemsDepart.clear();

                            // Create a list for curses received during battle
                            ArrayList<Item> curseList = new ArrayList<>();
                            int cursesReceived = hero.getCursesReceivedInBattle();
                            for (int i = 0; i < cursesReceived; i++) {
                                curseList.add(new Curse("Curse"));
                            }

                            // Add the curse list to rewards
                            room.rewards().addAll(curseList);
                            hero.resetCursesReceivedInBattle(); // Reset for next battle

                            chargerItemsDepart(room.rewards());
                            combatMode = false;
                        }
                    }

                    itemsGraphique.put(dragging, model);
                    itemsDepart.remove(dragging);

                    if (sac.placeItem(model, row, col, itemsGraphique, itemsDepart, dragging, true)) {

                        itemsDepart.remove(dragging);
                    }

                    dragging = null;
                    return;
                }
            }

            // If not dropped on enemy, snap back to backpack grid

            int col = Math.min(Math.max((dragging.getScreenXO() - sacX) / cellSize, 0), 6);
            int row = Math.min(Math.max((dragging.getScreenYO() - sacY) / cellSize, 0), 4);

            dragging.SetscreenX(sacX + col * cellSize);
            dragging.SetscreenY(sacY + row * cellSize);

            Item model = itemsGraphique.get(dragging);
            itemsGraphique.put(dragging, model);
            itemsDepart.remove(dragging);

            if (model instanceof Items.Curse) {
                if (sac.forcePlaceItem(model, row, col)) {
                    itemsDepart.remove(dragging);
                    // Also cleanup any removed items from itemsGraphique if forcePlaceItem removed
                    // them from model
                    cleanupGraphiqueItems();
                } else {
                    // Failed to force place (out of bounds or locked slot)
                    dragging.SetscreenX(dragging.getScreenXO());
                    dragging.SetscreenY(dragging.getScreenYO());
                }
            } else if (sac.placeItem(model, row, col, itemsGraphique, itemsDepart, dragging, true)) {
                itemsDepart.remove(dragging);
            } else {
                // Failed to place
                dragging.SetscreenX(dragging.getScreenXO());
                dragging.SetscreenY(dragging.getScreenYO());
            }
            // dragging = null;
        }
    }

    /**
     * Handles releasing the mouse button after dragging.
     */
    @Override
    public void onRelease(int x, int y, Hero hero) {
        Objects.requireNonNull(hero);
        if (combatMode) {
            releaseCombat(x, y, hero);
        } else {
            afterReleaseCombat(x, y, hero);
        }

    }

    /**
     * Updates the position of the currently dragged item during mouse drag.
     */
    @Override
    public void onDrag(int x, int y) {
        if (dragging != null) {
            // Move dragged item with cursor offset
            dragging.SetscreenX(x - dragOffsetX);
            dragging.SetscreenY(y - dragOffsetY);
        }
    }

    /**
     * Check if a point (x,y) is inside an item's bounding box on screen.
     */
    private boolean inside(ItemGraphique it, int x, int y) {
        Objects.requireNonNull(it);
        var item = itemsGraphique.get(it);
        var shape = item.occupiedCases();

        // Calculate width and height in pixels based on occupied cells
        int w = (shape.stream().mapToInt(p -> p.x).max().orElse(0) + 1) * cellSize;
        int h = (shape.stream().mapToInt(p -> p.y).max().orElse(0) + 1) * cellSize;

        return x >= it.getScreenX() && x <= it.getScreenX() + w &&
                y >= it.getScreenY() && y <= it.getScreenY() + h;
    }

    public void enemyTurn(Hero hero) {
        // Case: Click on the "End Turn" button
        room.enemiesTurn(hero);
        sac.desactivate(hero);
        sac.activate(hero);
        // Execute enemies' turn
        room.enemies().forEach(e -> {
            room.chooseActionEnemies(e, 3);
            e.baseShape();
        }); // Prepare enemy actions

        // Curses applied during enemy turn are now tracked and will appear as rewards
        // after battle
        // No need to spawn them immediately during combat
        // Clear any pending curses since they'll be added to rewards at battle end
        while (hero.popCurse()) {
            // Just consume the pending curses, they're already counted in
            // cursesReceivedInBattle
        }

        if (hero.isDied()) {
            // display lose screen
      
        	showDefeat = true;
        	endMessageStartTime = System.currentTimeMillis();

            System.out.println("End of the game, the hero is dead");
            return;
        }
        return;
    }

    /**
     * Returns the currently dragged item graphic.
     */
    public ItemGraphique getDragging() {
        return dragging;
    }

    /**
     * Rotates the selected item (changes orientation) and updates its position.
     * Adjusts position to keep item visually centered after rotation.
     */
    public void onRotate() {
        if (!combatMode) {
            if (selecteditem != null) {
                // Get the logical item associated with the selected graphical item
                Item item = itemsGraphique.get(selecteditem);
                if (item == null || item instanceof Curse) {
                    return; // Curses cannot be rotated
                }

                int centerX = calculateCenterX(item);
                int centerY = calculateCenterY(item);
                // rotate the item
                rotateItem(item);

                repositionAfterRotation(item, centerX, centerY);
            }
        }
    }

    private int calculateCenterX(Item item) {
        Objects.requireNonNull(item);
        int oldW = item.widthItem() * cellSize;
        return selecteditem.getScreenX() + oldW / 2;
    }

    private int calculateCenterY(Item item) {
        Objects.requireNonNull(item);
        int oldH = item.heightItem() * cellSize;
        return selecteditem.getScreenY() + oldH / 2;
    }

    private void rotateItem(Item item) {
        Objects.requireNonNull(item);
        selecteditem.setRotation();
        item.rotatePoints();
    }

    private void repositionAfterRotation(Item item, int centerX, int centerY) {
        Objects.requireNonNull(item);
        int newW = item.widthItem() * cellSize;
        int newH = item.heightItem() * cellSize;

        selecteditem.SetscreenX(centerX - newW / 2);
        selecteditem.SetscreenY(centerY - newH / 2);
    }

    /**
     * Expand the backpack.
     */
    public void expand() {
        Objects.requireNonNull(sac);
        sac.expand(expandBag);
        expandBag.clear();
        availableCells.clear();
    }

    public void hover(int x, int y) {
        for (var ig : itemsGraphique.keySet()) {
            if (inside(ig, x, y)) {
                currentHoveredItem = ig;
                return;
            }
        }
    }

    @Override
    public boolean canLeaveScreen() {
        // Check if there are any curse items still outside the backpack
        for (var item : itemsDepart) {
            Item itemModel = itemsGraphique.get(item);
            if (itemModel instanceof Curse) {
                return false; // Don't allow screen transition
            }
        }
        return true;
    }

    public void onLeaveScreen(Hero hero) {
        Objects.requireNonNull(hero);
        // Remove graphical items currently outside the backpack from the main items map
        for (var item : itemsDepart) {
            itemsGraphique.remove(item);
        }
        // Update the main window's items map to the current state (without itemsDepart)
        fenetre.setItems(itemsGraphique);

        // Update the hero's backpack with the current backpack state
        hero.setBackpack(sac);
    }

    /**
     * @deprecated Use canLeaveScreen() and onLeaveScreen(Hero)
     */
    public boolean goCombat(Hero hero) {
        Objects.requireNonNull(hero);
        if (!canLeaveScreen()) {
            return false;
        }
        onLeaveScreen(hero);
        return true;
    }

    /**
     * Spawns a curse and starts dragging it.
     */
    public void spawnCurse() {
        Objects.requireNonNull(sac);
        Curse curse = new Curse("Curse");
        ItemGraphique ig = new ItemGraphique(curse.name(), Screen.load("/ressources/icones/curse.png"));
        if (ig.getImage() == null) {
            System.err.println("Curse image not found, using valid placeholder if available");
        }

        itemsGraphique.put(ig, curse);

        // Start dragging immediately
        dragging = ig;
        dragging.SetscreenX(fenetre.getRealWidth() / 2);
        dragging.SetscreenY(fenetre.getRealHeight() / 2);
        dragOffsetX = 0;
        dragOffsetY = 0;
    }

    private void cleanupGraphiqueItems() {
        // Remove ItemGraphiques whose items are no longer in the backpack (and not in
        // itemsDepart)
        var it = itemsGraphique.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            ItemGraphique ig = entry.getKey();
            Item item = entry.getValue();

            // If item is not in backpack's item map AND not in itemsDepart, remove it
            if (!sac.getItems().containsKey(item) && !itemsDepart.contains(ig)) {
                it.remove();
            }
        }
    }
}
