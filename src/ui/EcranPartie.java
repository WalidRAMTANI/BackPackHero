package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Items.Item;
import Items.Curse;
import model.BackPack;
import model.Dungeon;
import model.Hero;

public class EcranPartie implements Screen, DragSupport {

    private final ZenGameView fenetre;
    private BufferedImage background;
    private BufferedImage heroine;
    private BackPack sac;
    private HashMap<ItemGraphique, Item> itemsGraphique;
    private ArrayList<ItemGraphique> itemsDepart = new ArrayList<>();
    private ItemGraphique selecteditem = null;
    private ItemGraphique dragging = null;
    private int dragOffsetX, dragOffsetY;
    // Size in pixels of one cell in the backpack grid
    private int cellSize;
    private int sacX, sacY;
    private Button button;

    public EcranPartie(ZenGameView fenetre, Hero hero, List<Item> awards) {
        Objects.requireNonNull(fenetre);
        Objects.requireNonNull(hero);
        this.fenetre = fenetre;
        this.itemsGraphique = fenetre.getItems();
        this.sac = hero.getBackpack();

        chargerBG();
        chargerImages();

        if (awards.isEmpty()) {
            chargerItemsEcranPartie();
        } else {
            Screen.ajouterItemsFromList(awards, 0, 0, 4, itemsDepart, itemsGraphique);
        }
    }

    private void chargerBG() {
        background = Screen.load("/ressources/menu_fond.png");
    }

    private void chargerImages() {
        heroine = Screen.load("/ressources/hero/heroine.png");
    }

    private void chargerItemsEcranPartie() {
        Objects.requireNonNull(itemsGraphique);
        List<Item> items = Item.generateItems();
        int colCount = 4;
        int index = 0;

        for (Item item : items) {
            int x = index % colCount;
            int y = index / colCount;

            ItemGraphique ig = new ItemGraphique(item.name(),
                    Screen.load("/ressources/icones/" + item.name().replace(" ", "_") + ".png"));
            Screen.ajouterItemDepart(ig, item, x, y, itemsDepart, itemsGraphique);
            index++;
        }
    }

    @Override
    public void render(Graphics2D g, int W, int H, Hero hero) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(hero);
        drawBackground(g, W, H);
        drawHero(g, W, H);
        calculateBackpackDimensions(W, H);
        drawBackpackGrid(g);
        drawAllItems(g);
        drawEndTurnRemark(g);

        ItemGraphique target = dragging != null ? dragging : selecteditem;
        drawItemInfo(g, target, itemsGraphique, fenetre.getRealWidth());
    }

    private void drawBackground(Graphics2D g, int W, int H) {
        Objects.requireNonNull(g);
        if (background != null) {
            g.drawImage(background, 0, 0, W, H, null);
        }
    }

    private void drawHero(Graphics2D g, int W, int H) {
        Objects.requireNonNull(g);
        if (heroine != null) {
            int heroW = W / 5;
            int heroH = (int) (heroine.getHeight() * (heroW / (double) heroine.getWidth()));
            g.drawImage(heroine, W / 2 - heroW / 2, H - heroH - 80, heroW, heroH, null);
        }
    }

    private void calculateBackpackDimensions(int W, int H) {
        Objects.requireNonNull(sac);
        boolean[][] open = sac.getOpenGrid();
        int rows = open.length;
        int cols = open[0].length;

        cellSize = Math.min(W / (cols + 14), H / (rows + 12));
        int sacW = cols * cellSize;
        int sacH = rows * cellSize;

        sacX = (W - sacW) / 2;
        sacY = H / 10;
    }

    private void drawBackpackGrid(Graphics2D g) {
        Objects.requireNonNull(g);
        boolean[][] open = sac.getOpenGrid();
        int rows = open.length;
        int cols = open[0].length;
        int sacW = cols * cellSize;
        int sacH = rows * cellSize;

        // Draw background panel
        g.setColor(new Color(20, 0, 40, 150));
        g.fillRoundRect(sacX - 15, sacY - 15, sacW + 30, sacH + 30, 25, 25);

        // Draw cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                drawCell(g, r, c, open[r][c]);
            }
        }
    }

    private void drawCell(Graphics2D g, int r, int c, boolean isOpen) {
        Objects.requireNonNull(g);
        int x = sacX + c * cellSize;
        int y = sacY + r * cellSize;
        g.setColor(isOpen ? new Color(90, 0, 160, 150) : new Color(20, 0, 30, 120));
        g.fillRoundRect(x, y, cellSize - 6, cellSize - 6, 15, 15);
    }

    private void drawAllItems(Graphics2D g) {
        Objects.requireNonNull(g);
        // Draw items inside backpack
        for (Map.Entry<ItemGraphique, Item> entry : itemsGraphique.entrySet()) {
            ItemGraphique ig = entry.getKey();
            if (!itemsDepart.contains(ig)) {
                Screen.drawItem(g, ig, itemsGraphique, cellSize);
            }
        }

        // Draw items outside backpack
        for (ItemGraphique it : itemsDepart) {
            Screen.drawItem(g, it, itemsGraphique, cellSize);
        }
    }

    public void drawEndTurnRemark(Graphics2D g) {
        Objects.requireNonNull(g);
        String text = "Press M to return to the map";

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int padding = 20;
        int w = textWidth + 2 * padding;
        int h = textHeight + 2 * padding;

        int x = fenetre.getRealWidth() - w - 20;
        int y = 20;

        button = new Button(x, y, w, h);

        drawButton(g, x, y, w, h);
        drawButtonText(g, x, y, w, h, text);
    }

    private void drawButton(Graphics2D g, int x, int y, int w, int h) {
        Objects.requireNonNull(g);
        g.setColor(new Color(120, 0, 180));
        g.fillRoundRect(x, y, w, h, 15, 15);

        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, w, h, 15, 15);
    }

    private void drawButtonText(Graphics2D g, int x, int y, int w, int h, String text) {
        Objects.requireNonNull(g);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.WHITE);

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int textX = x + (w - textWidth) / 2;
        int textY = y + (h + textHeight) / 2 - 4;

        g.drawString(text, textX, textY);
    }

    @Override
    public void onClick(int x, int y, Hero hero, Dungeon d) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(d);
        if (dragging != null)
            return;

        System.out.println(hero.getBackpack());

        if (tryPickFromBackpack(x, y))
            return;
        tryPickFromOutside(x, y);
    }

    private boolean tryPickFromBackpack(int x, int y) {
        if (!isInsideBackpack(x, y))
            return false;

        int col = getBackpackColumn(x);
        int row = getBackpackRow(y);

        Item itemInSac = sac.getItemAtPoint(row, col);
        if (itemInSac == null)
            return false;

        pickItemFromBackpack(itemInSac, x, y);
        return true;
    }

    private boolean isInsideBackpack(int x, int y) {
        return x >= sacX && x <= sacX + cellSize * 7 &&
                y >= sacY && y <= sacY + cellSize * 5;
    }

    private int getBackpackColumn(int x) {
        return Math.min(Math.max((x - sacX) / cellSize, 0), 6);
    }

    private int getBackpackRow(int y) {
        return Math.min(Math.max((y - sacY) / cellSize, 0), 4);
    }

    private void pickItemFromBackpack(Item itemInSac, int x, int y) {
        Objects.requireNonNull(itemInSac);
        int col = getBackpackColumn(x);
        int row = getBackpackRow(y);

        sac.removeItem(row, col);

        dragging = findGraphicalItem(itemInSac);
        selecteditem = dragging;

        if (dragging != null) {
            initializeDragging(x, y);
            if (!itemsDepart.contains(dragging)) {
                itemsDepart.add(dragging);
            }
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

    private void initializeDragging(int x, int y) {
        dragging.SetscreenXO(x);
        dragging.SetscreenYO(y);
        dragOffsetX = 0;
        dragOffsetY = 0;
    }

    private void tryPickFromOutside(int x, int y) {
        Objects.requireNonNull(itemsDepart);
        for (ItemGraphique ig : itemsDepart) {
            if (Screen.inside(ig, itemsGraphique, x, y, cellSize)) {
                startDraggingOutsideItem(ig, x, y);
                return;
            }
        }
    }

    private void startDraggingOutsideItem(ItemGraphique ig, int x, int y) {
        Objects.requireNonNull(ig);
        dragging = ig;
        selecteditem = dragging;

        dragOffsetX = x - ig.getScreenX();
        dragOffsetY = y - ig.getScreenY();

        ig.SetscreenXO(ig.getScreenX());
        ig.SetscreenYO(ig.getScreenY());
    }

    @Override
    public void onRelease(int x, int y) {
        if (dragging == null)
            return;

        selecteditem = dragging;

        if (isInsideBackpack(x, y)) {
            tryPlaceInBackpack(x, y);
        } else {
            ensureItemInOutsideList();
        }

        dragging = null;
    }

    private void tryPlaceInBackpack(int x, int y) {
        int col = getBackpackColumn(x);
        int row = getBackpackRow(y);

        Item model = itemsGraphique.get(dragging);

        // D'abord essayer de placer l'item
        if (sac.placeItem(model, row, col, itemsGraphique, itemsDepart, dragging, true)) {
            // Placement réussi - snap à la grille
            snapToGrid(col, row);
        } else {
            // Placement échoué - revenir à la position d'origine
            revertToOriginalPosition();
        }
    }

    private void snapToGrid(int col, int row) {
        Objects.requireNonNull(dragging);
        dragging.SetscreenX(sacX + col * cellSize);
        dragging.SetscreenY(sacY + row * cellSize);
    }

    private void revertToOriginalPosition() {
        Objects.requireNonNull(dragging);
        dragging.SetscreenX(dragging.getScreenXO());
        dragging.SetscreenY(dragging.getScreenYO());
    }

    private void ensureItemInOutsideList() {
        Objects.requireNonNull(itemsDepart);
        if (!itemsDepart.contains(dragging)) {
            itemsDepart.add(dragging);
        }
    }

    @Override
    public void onDrag(int x, int y) {
        if (dragging != null) {
            dragging.SetscreenX(x - dragOffsetX);
            dragging.SetscreenY(y - dragOffsetY);
        }
    }

    public void onRotate() {
        if (selecteditem == null)
            return;

        Item item = itemsGraphique.get(selecteditem);
        if (item == null || item instanceof Curse) {
            return;
        }

        int centerX = calculateCenterX(item);
        int centerY = calculateCenterY(item);

        rotateItem(item);

        repositionAfterRotation(item, centerX, centerY);
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

    @Override
    public boolean canLeaveScreen() {
        Objects.requireNonNull(itemsDepart);
        // Check if there are any curse items still outside the backpack
        for (var item : itemsDepart) {
            Item itemModel = itemsGraphique.get(item);
            if (itemModel instanceof Items.Curse) {
                return false; // Don't allow screen transition
            }
        }
        return true;
    }

    public void onLeaveScreen(Hero hero) {
        Objects.requireNonNull(hero);
        removeOutsideItems();
        updateGameState(hero);
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

    private void removeOutsideItems() {
        Objects.requireNonNull(itemsDepart);
        for (var item : itemsDepart) {
            itemsGraphique.remove(item);
        }
    }

    private void updateGameState(Hero hero) {
        Objects.requireNonNull(hero);
        fenetre.setItems(itemsGraphique);
        hero.setBackpack(sac);
    }
}