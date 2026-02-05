package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Objects;

import Rooms.CorridorRoom;
import Rooms.EnemyRoom;
import Rooms.HealerRoom;
import Rooms.MerchantRoom;
import Rooms.RoomType;
import Rooms.TreasureRoom;
import model.Dungeon;
import model.Hero;

/**
 * Screen overlay representing the dungeon map.
 */
public class MapOverlay implements Screen {

    private final ZenGameView fenetre;
    private final Dungeon dungeon;

    private BufferedImage background;
    private BufferedImage iconEnemy;
    private BufferedImage iconTreasure;
    private BufferedImage iconMerchant;
    private BufferedImage iconHealer;
    private BufferedImage iconExit;
    private BufferedImage iconEntrance;
    private BufferedImage iconWall;
    private BufferedImage iconHero;

    private final int ROWS = 5;
    private final int COLS = 11;
    private int cell;
    private int heroR = 2;
    private int heroC = 0;
    
    private boolean dungeonWon = false;
    private boolean victory = false;

    
    public boolean isVictory() {
        return victory;
    }



    /**
     * @param fenetre Game view reference
     * @param d       Dungeon model reference
     */
    public MapOverlay(ZenGameView fenetre, Dungeon d) {
        this.fenetre = Objects.requireNonNull(fenetre);
        this.dungeon = Objects.requireNonNull(d);
        findEntrance();
        loadImages();
    }

    /**
     * Locates the entrance room and sets the initial hero position on map.
     */
    private void findEntrance() {
        RoomType[][] grid = dungeon.getCurrentFloor().getRooms();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c] instanceof CorridorRoom cr && cr.isEntry()) {
                    heroR = r;
                    heroC = c;
                    return;
                }
            }
        }
    }

    /**
     * Loads graphical assets for the map.
     */
    private void loadImages() {
        background = Screen.load("/ressources/fond/map_fond.png");
        iconEnemy = Screen.load("/ressources/icones/ennemis.png");
        iconTreasure = Screen.load("/ressources/icones/tresor.png");
        iconMerchant = Screen.load("/ressources/icones/marchand.png");
        iconHealer = Screen.load("/ressources/icones/Aqualith.png");
        iconExit = Screen.load("/ressources/icones/sortie.png");
        iconEntrance = Screen.load("/ressources/icones/entrance.png");
        iconWall = Screen.load("/ressources/icones/wall.png");
        iconHero = Screen.load("/ressources/icones/heroine.png");

    }

    /**
     * Renders the map overlay.
     * 
     * @param g    Graphics context
     * @param W    Window width
     * @param H    Window height
     * @param hero Hero reference
     */
    @Override
    public void render(Graphics2D g, int W, int H, Hero hero) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(hero);
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, W, H);

        if (background != null) {
            g.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.7f));
            g.drawImage(background, 0, 0, W, H, null);
            g.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
        }

        int cw = W / (COLS + 2);
        int ch = H / (ROWS + 2);
        cell = Math.min(cw, ch);

        int offsetX = (W - COLS * cell) / 2;
        int offsetY = (H - ROWS * cell) / 2;

        RoomType[][] grid = dungeon.getCurrentFloor().getRooms();

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int x = offsetX + c * cell;
                int y = offsetY + r * cell;

                g.setColor(new Color(20, 0, 40, 130));
                g.fillRoundRect(x, y, cell - 4, cell - 4, 10, 10);

                RoomType room = grid[r][c];
                String type = room.getTypeName();

                switch (type) {
                    case "ENEMY" -> {
                        if (!((EnemyRoom) room).isCleared()) {
                            drawIcon(g, iconEnemy, x, y, cell);
                        }
                    }
                    case "TREASURE" -> drawIcon(g, iconTreasure, x, y, cell);
                    case "MERCHANT" -> drawIcon(g, iconMerchant, x, y, cell);
                    case "HEALER" -> drawIcon(g, iconHealer, x, y, cell);
                    case "WALL" -> drawIcon(g, iconWall, x, y, cell);
                    case "CORRIDOR" -> {
                        CorridorRoom cr = (CorridorRoom) room;
                        if (cr.isEntry())
                            drawIcon(g, iconEntrance, x, y, cell);
                        else if (cr.isExit())
                            drawIcon(g, iconExit, x, y, cell);
                        else if (cr.closed())
                            drawIcon(g, iconWall, x, y, cell);
                    }
                }
            }
        }

        int bw = W / 6;
        int bh = H / 14;
        int bx = 20;
        int by = 20;

        g.setColor(new Color(150, 80, 200, 220));
        g.fillRoundRect(bx, by, bw, bh, 20, 20);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, bh / 2));
        g.drawString("CLOSE", bx + 20, by + bh / 2 + 10);

        int hx = offsetX + heroC * cell + cell / 4;
        int hy = offsetY + heroR * cell + cell / 4;

        if (iconHero != null) {
            int s = (int) (cell * 0.8);
            int offset = (cell - s) / 2;
            g.drawImage(
                    iconHero,
                    offsetX + heroC * cell + offset,
                    offsetY + heroR * cell + offset,
                    s,
                    s,
                    null);
        }
        if (dungeonWon) {
            drawWinMessage(g, W, H);
        }

    }

    /**
     * Draws a room icon.
     * 
     * @param g    Graphics context
     * @param img  Icon image
     * @param x    X screen position
     * @param y    Y screen position
     * @param size Size of the icon
     */
    private void drawIcon(Graphics2D g, BufferedImage img, int x, int y, int size) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(img);
        if (img != null) {
            int s = (int) (size * 0.95); // 95% de la case
            int offset = (size - s) / 2; // centrage parfait
            g.drawImage(img, x + offset, y + offset, s, s, null);
        }

        // g.drawImage(img, x + size / 10, y + size / 10, size * 4 / 5, size * 4 / 5,
        // null);
    }
    
    private void drawWinMessage(Graphics2D g, int W, int H) {
        String text1 = "You have escaped the dungeon!";
        String text2 = "Press P to play again";

        // fond sombre
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, W, H);

        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(Color.WHITE);

        int w1 = g.getFontMetrics().stringWidth(text1);
        g.drawString(text1, (W - w1) / 2, H / 2 - 20);

        g.setFont(new Font("Arial", Font.PLAIN, 22));
        int w2 = g.getFontMetrics().stringWidth(text2);
        g.drawString(text2, (W - w2) / 2, H / 2 + 30);
    }


    /**
     * Handles clicks on the map overlay.
     * 
     * @param x    Click X position
     * @param y    Click Y position
     * @param hero Hero reference
     * @param d    Dungeon reference
     */
    @Override
    public void onClick(int x, int y, Hero hero, Dungeon d) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(d);
        int W = fenetre.getRealWidth();
        int H = fenetre.getRealHeight();

        int bw = W / 6;
        int bh = H / 14;
        int bx = 20;
        int by = 20;
        if (x >= bx && x <= bx + bw && y >= by && y <= by + bh) {
            fenetre.closeMap();
            return;
        }

        int cellW = W / (COLS + 2);
        int cellH = H / (ROWS + 2);
        int cell = Math.min(cellW, cellH);

        int offsetX = (W - COLS * cell) / 2;
        int offsetY = (H - ROWS * cell) / 2;

        if (x < offsetX || x >= offsetX + COLS * cell)
            return;
        if (y < offsetY || y >= offsetY + ROWS * cell)
            return;

        int c = (x - offsetX) / cell;
        int r = (y - offsetY) / cell;

        if (r < 0 || r >= ROWS || c < 0 || c >= COLS)
            return;

        int dist = dungeon.getCurrentFloor().allowedFromTo(dungeon.getHeroPosition(), new Point(r, c));

        if (dist != -1) {
            heroR = r;
            heroC = c;
            dungeon.setHeroPosition(new Point(r, c));

            RoomType room = dungeon.getCurrentFloor().getRooms()[r][c];
            if (room instanceof CorridorRoom cr && cr.isExit()) {
                if (dungeon.isLastFloor()) {
                	dungeonWon = true; 
                    fenetre.closeMap();
                    System.out.println("Congratulations! You cleared the dungeon!");
                    return;
                }
                dungeon.goNextFloor();
                findEntrance();
                return;
            }
            handleRoomClick(room, hero);
        }


    }

    /**
     * Triggers screen transition when a room is clicked.
     * 
     * @param room The clicked room
     * @param hero Hero reference
     */
    private void handleRoomClick(RoomType room, Hero hero) {
        Objects.requireNonNull(room);
        Objects.requireNonNull(hero);
        fenetre.getCurrentScreen().onLeaveScreen(hero);

        switch (room) {
            case EnemyRoom r -> {
                if (!r.isCleared()) {
                    fenetre.setScreen(new EcranCombat(fenetre, hero, r));
                }
            }
            case TreasureRoom r -> fenetre.setScreen(new EcranTreasure(fenetre, hero, r));
            case MerchantRoom _ -> fenetre.setScreen(new EcranMerchant(fenetre, hero));
            case HealerRoom r -> fenetre.setScreen(new EcranHealer(fenetre, hero, r));
            case CorridorRoom _ -> {
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + room);
        }
    }
}
