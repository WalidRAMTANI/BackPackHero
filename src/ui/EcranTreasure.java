package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Items.Item;
import Items.Key;
import Items.Curse;
import Rooms.TreasureRoom;
import model.BackPack;
import model.Dungeon;
import model.Hero;

public class EcranTreasure implements Screen, DragSupport {

    private final Hero hero;
    private final ZenGameView fenetre;
    private BufferedImage background;
    private BufferedImage heroine;
    private BackPack sac;
    private HashMap<ItemGraphique, Item> itemsGraphique;
    private ArrayList<ItemGraphique> itemsDepart = new ArrayList<>();
    private ArrayList<ItemGraphique> itemsTreasure = new ArrayList<>();
    private ItemGraphique selecteditem = null;
    private ItemGraphique dragging = null;
    private int dragOffsetX, dragOffsetY;
    private int cellSize;
    private int sacX, sacY;
    private TreasureRoom room;
    private BufferedImage treasuredImage;

    public EcranTreasure(ZenGameView fenetre, Hero hero, TreasureRoom room) {
        Objects.requireNonNull(fenetre);
        Objects.requireNonNull(hero);
        Objects.requireNonNull(room);

        this.room = room;
        this.fenetre = fenetre;
        this.hero = hero;
        this.itemsGraphique = fenetre.getItems();
        this.sac = hero.getBackpack();

        ajouterItemsDepart(room.treasures(), 0, 0, 4);
        chargerTreasorIg();
        chargerImages();
        chargerBG();
    }

    private void chargerBG() {
        background = Screen.load("/ressources/fond/treasure_fond.png");
    }

    private void chargerImages() {
        heroine = Screen.load("/ressources/hero/heroine.png");
    }

    private void chargerTreasorIg() {
        treasuredImage = Screen.load("/ressources/icones/treasure.png");
    }

    private void ajouterItemDepart(ItemGraphique it, Item model, int col, int row) {
        Objects.requireNonNull(it);
        Objects.requireNonNull(model);
        Objects.requireNonNull(fenetre);

        int cellW = 140;
        int cellH = 140;
        int baseX = fenetre.getRealWidth() - 3 * 140 - 40;
        int baseY = 100;

        it.SetscreenX(baseX + col * cellW);
        it.SetscreenY(baseY + row * cellH);

        addToAppropriateList(it);
        itemsGraphique.put(it, model);
    }

    private void addToAppropriateList(ItemGraphique it) {
        Objects.requireNonNull(it);
        if (room.needKey()) {
            itemsTreasure.add(it);
        } else {
            itemsDepart.add(it);
        }
    }

    public void ajouterItemsDepart(List<Item> items, int startCol, int startRow, int maxCols) {
        Objects.requireNonNull(items);
        Objects.requireNonNull(itemsDepart);
        Objects.requireNonNull(itemsTreasure);
        clearExistingItems();

        int col = startCol;
        int row = startRow;

        for (Item item : items) {
            ItemGraphique ig = createItemGraphique(item);
            ajouterItemDepart(ig, item, col, row);

            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }

    private void clearExistingItems() {
        Objects.requireNonNull(itemsDepart);
        Objects.requireNonNull(itemsTreasure);
        for (var ig : itemsDepart) {
            itemsGraphique.remove(ig);
        }
        itemsDepart.clear();
        itemsTreasure.clear();
    }

    private ItemGraphique createItemGraphique(Item item) {
        Objects.requireNonNull(item);
        return new ItemGraphique(
                item.name(),
                Screen.load("/ressources/icones/" + item.name().replace(" ", "_") + ".png"));
    }

    @Override
    public void render(Graphics2D g, int W, int H, Hero hero) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(hero);
        drawBackground(g, W, H);
        drawHero(g, W, H);
        drawTreasureChest(g, W, H);
        calculateBackpackDimensions(W, H);
        drawBackpackGrid(g);
        drawAllItems(g);

        if (room.needKey()) {
            drawBuyButton(g, "Open with key");
        }

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
        if (heroine == null)
            return;

        int heroW = W / 5;
        int heroH = (int) (heroine.getHeight() * (heroW / (double) heroine.getWidth()));
        g.drawImage(heroine, W / 3 - heroW / 2, H - heroH - 60, heroW, heroH, null);
    }

    private void drawTreasureChest(Graphics2D g, int W, int H) {
        Objects.requireNonNull(g);
        if (treasuredImage == null || !room.needKey())
            return;

        int heroW = W / 5;
        int heroH = (int) (treasuredImage.getHeight() * (heroW / (double) treasuredImage.getWidth()));
        g.drawImage(treasuredImage, 3 * W / 4 - heroW / 2, H - heroH - 30, heroW, heroH, null);
    }

    private void calculateBackpackDimensions(int W, int H) {
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

        drawBackpackBackground(g, sacW, sacH);
        drawBackpackCells(g, open, rows, cols);
    }

    private void drawBackpackBackground(Graphics2D g, int sacW, int sacH) {
        Objects.requireNonNull(g);
        g.setColor(new Color(20, 0, 40, 150));
        g.fillRoundRect(sacX - 15, sacY - 15, sacW + 30, sacH + 30, 25, 25);
    }

    private void drawBackpackCells(Graphics2D g, boolean[][] open, int rows, int cols) {
        Objects.requireNonNull(g);
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
        drawBackpackItems(g);
        drawOutsideItems(g);
    }

    private void drawBackpackItems(Graphics2D g) {
        for (Map.Entry<ItemGraphique, Item> entry : itemsGraphique.entrySet()) {
            ItemGraphique ig = entry.getKey();
            if (!itemsDepart.contains(ig) && !itemsTreasure.contains(ig)) {
                Screen.drawItem(g, ig, itemsGraphique, cellSize);
            }
        }
    }

    private void drawOutsideItems(Graphics2D g) {
        Objects.requireNonNull(g);
        for (ItemGraphique it : itemsDepart) {
            Screen.drawItem(g, it, itemsGraphique, cellSize);
        }
    }

    private void drawBuyButton(Graphics2D g, String text) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(text);

        int paddingX = 24;
        int paddingY = 14;

        Font font = new Font("Arial", Font.BOLD, 18);
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int w = textWidth + paddingX * 2;
        int h = textHeight + paddingY * 2;
        int x = 40;
        int y = fenetre.getRealHeight() - h - 40;

        drawButtonBackground(g, x, y, w, h);
        drawButtonText(g, text, fm, x, y, w, h, textWidth);
    }

    private void drawButtonBackground(Graphics2D g, int x, int y, int w, int h) {
        Objects.requireNonNull(g);
        g.setColor(new Color(60, 160, 60));
        g.fillRoundRect(x, y, w, h, 15, 15);
    }

    private void drawButtonText(Graphics2D g, String text, FontMetrics fm, int x, int y, int w, int h, int textWidth) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(text);
        Objects.requireNonNull(fm);
        g.setColor(Color.WHITE);
        int textX = x + (w - textWidth) / 2;
        int textY = y + (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, textX, textY);
    }

    @Override
    public void onClick(int x, int y, Hero hero, Dungeon d) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(d);
        if (tryPickFromBackpack(x, y))
            return;
        tryPickFromOutside(x, y);
    }

    private boolean tryPickFromBackpack(int x, int y) {
        Objects.requireNonNull(sac);
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
        Objects.requireNonNull(dragging);
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
        dragOffsetX = x - ig.getScreenX();
        dragOffsetY = y - ig.getScreenY();
    }

    @Override
    public void onRelease(int x, int y) {
        if (dragging == null)
            return;

        selecteditem = dragging;

        if (isInsideBackpack(x, y)) {
            tryPlaceInBackpack();
        } else {
            ensureItemInOutsideList();
        }

        dragging = null;
    }

    private void tryPlaceInBackpack() {
        if (dragging == null)
            return;
        int col = calculateDropColumn();
        int row = calculateDropRow();

        snapToGrid(col, row);

        Item model = itemsGraphique.get(dragging);

        if (!sac.placeItem(model, row, col, itemsGraphique, itemsDepart, dragging, true)) {
            revertToOriginalPosition();
        }
    }

    private int calculateDropColumn() {
        Objects.requireNonNull(dragging);
        return Math.min(Math.max((dragging.getScreenX() - sacX) / cellSize, 0), 6);
    }

    private int calculateDropRow() {
        Objects.requireNonNull(dragging);
        return Math.min(Math.max((dragging.getScreenY() - sacY) / cellSize, 0), 4);
    }

    private int calculateDropColumnKey() {
        Objects.requireNonNull(selecteditem);
        return Math.min(Math.max((selecteditem.getScreenX() - sacX) / cellSize, 0), 6);

    }

    private int calculateDropRowKey() {
        Objects.requireNonNull(selecteditem);
        return Math.min(Math.max((selecteditem.getScreenY() - sacY) / cellSize, 0), 4);
    }

    private void snapToGrid(int col, int row) {
        dragging.SetscreenX(sacX + col * cellSize);
        dragging.SetscreenY(sacY + row * cellSize);
    }

    private void revertToOriginalPosition() {
        Objects.requireNonNull(dragging);
        dragging.SetscreenX(dragging.getScreenXO());
        dragging.SetscreenY(dragging.getScreenYO());
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
        Objects.requireNonNull(selecteditem);
        int oldW = item.widthItem() * cellSize;
        return selecteditem.getScreenX() + oldW / 2;
    }

    private int calculateCenterY(Item item) {
        Objects.requireNonNull(item);
        Objects.requireNonNull(selecteditem);
        int oldH = item.heightItem() * cellSize;
        return selecteditem.getScreenY() + oldH / 2;
    }

    private void rotateItem(Item item) {
        Objects.requireNonNull(selecteditem);
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

    public void openTreasure() {
        System.out.println("call openTresure : ");

        if (!room.needKey())
            return;

        System.out.println("Need KEY : ");

        if (tryOpenWithKey()) {
            unlockTreasure();
        }

        System.out.println("Need KEY : ");
    }

    private boolean tryOpenWithKey() {
        Objects.requireNonNull(selecteditem);
        Item item = itemsGraphique.get(selecteditem); // Use selecteditem instead
        if (item == null || !(item instanceof Key))
            return false;

        int col = calculateDropColumnKey();
        int row = calculateDropRowKey();
        itemsDepart.remove(selecteditem); // Also use selecteditem here
        itemsGraphique.remove(selecteditem);
        return true;
    }

    private void unlockTreasure() {
        Objects.requireNonNull(room);
        room.setneedKey(false);
        itemsDepart.addAll(itemsTreasure);
        itemsTreasure.clear();
    }

    @Override
    public boolean canLeaveScreen() {
        Objects.requireNonNull(itemsDepart);
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

        // Also remove treasure items from the main items map
        for (var item : itemsTreasure) {
            itemsGraphique.remove(item);
        }

        // Update the main window's items map
        fenetre.setItems(itemsGraphique);
        // Update the hero's backpack
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
}