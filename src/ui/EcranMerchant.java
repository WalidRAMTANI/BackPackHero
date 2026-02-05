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
import java.awt.BasicStroke;

import Items.Item;
import Items.Curse;
import model.BackPack;
import model.Dungeon;
import model.Hero;
import model.Merchant;

public class EcranMerchant implements Screen, DragSupport {

    private final ZenGameView fenetre;
    private final Hero hero;
    private final Merchant merchant;

    private BufferedImage background;
    private BufferedImage heroine;
    private BufferedImage merchantIG;

    private BackPack sac;
    private HashMap<ItemGraphique, Item> itemsGraphique;

    private ArrayList<ItemGraphique> itemsDepart = new ArrayList<>(); // items hors sac (joueur)
    private ArrayList<ItemGraphique> itemsMerchant = new ArrayList<>(); // items du marchand

    private ItemGraphique selecteditem = null;
    private ItemGraphique selectedMerchantItem = null;
    private ItemGraphique dragging = null;

    private int dragOffsetX, dragOffsetY;
    private int cellSize;
    private int sacX, sacY;

    private Button buyButton;

    private String merchantMessage = null;
    private long messageTime = 0;

    /* ========================= CONSTRUCTEUR ========================= */

    public EcranMerchant(ZenGameView fenetre, Hero hero) {
        Objects.requireNonNull(fenetre);
        Objects.requireNonNull(hero);

        this.fenetre = fenetre;
        this.hero = hero;
        this.sac = hero.getBackpack();
        this.itemsGraphique = fenetre.getItems();
        this.merchant = new Merchant();

        chargerBG();
        chargerImages();
        initializeBuyButton();

        chargerItemsMerchant();
    }

    /* ========================= CHARGEMENTS ========================= */

    private void chargerBG() {
        background = Screen.load("/ressources/fond/merchant_fond.png");
    }

    private void chargerImages() {
        heroine = Screen.load("/ressources/hero/heroine.png");
    }

    private void initializeBuyButton() {
        buyButton = new Button(
                fenetre.getRealWidth() - 200,
                fenetre.getRealHeight() - 90,
                160,
                50);
    }

    /*
     * ========================= ITEMS MERCHANT (COMME
     * ECRANPARTIE)=======================
     */

    private void chargerItemsMerchant() {
        clearMerchantItems();

        List<Item> items = merchant.inventory();
        int index = 0;
        int col = 0;
        for (Item item : items) {

            ItemGraphique ig = new ItemGraphique(
                    item.name(),
                    Screen.load("/ressources/icones/" + item.name().replace(" ", "_") + ".png"));

            ajouterItemMerchant(
                    ig,
                    item,
                    col,
                    index);
            col++;
            if (col >= 4) {
                col = 0;
                index++;
            }
        }
    }

    private void ajouterItemMerchant(ItemGraphique it, Item model, int col, int row) {
        Objects.requireNonNull(it);
        Objects.requireNonNull(model);

        // Base coordinates for the left panel where items are shown
        int baseX = fenetre.getRealWidth() - 4 * 140 + 10;
        int baseY = 200;

        // Cell width and height for positioning items in grid layout outside backpack
        int cellW = 140;
        int cellH = 140;

        // Set screen position for the graphical item
        it.SetscreenX(baseX + col * cellW);
        it.SetscreenY(baseY + row * cellH);

        // Add the item to the outside-backpack list
        itemsMerchant.add(it);

        // Map the graphical item to its model item in the main item map
        itemsGraphique.put(it, model);
    }

    private void clearMerchantItems() {
        for (ItemGraphique ig : itemsMerchant) {
            itemsGraphique.remove(ig);
        }
        itemsMerchant.clear();
    }

    /* ========================= RENDER ========================= */

    @Override
    public void render(Graphics2D g, int W, int H, Hero hero) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(hero);
        drawBackground(g, W, H);
        drawHero(g, W, H);
        // drawMerchant(g, W, H);

        calculateBackpackDimensions(W, H);
        drawBackpackGrid(g);
        drawAllItems(g);
        drawBuyButton(g, "Press S to sell, B to buy");

        ItemGraphique target = dragging != null ? dragging
                : (selecteditem != null ? selecteditem : selectedMerchantItem);
        drawItemInfo(g, target, itemsGraphique, fenetre.getRealWidth());

        drawMerchantMessage(g);
        drawGoldCounter(g);

    }

    private void drawBackground(Graphics2D g, int W, int H) {
        Objects.requireNonNull(g);
        if (background != null) {
            g.drawImage(background, 0, 0, W, H, null);
        }
    }

    private void drawHero(Graphics2D g, int W, int H) {
        Objects.requireNonNull(g);
        int heroW = W / 5;
        int heroH = (int) (heroine.getHeight() * (heroW / (double) heroine.getWidth()));
        g.drawImage(heroine, W / 3 - heroW / 2, H - heroH - 80, heroW, heroH, null);
    }

    private void calculateBackpackDimensions(int W, int H) {
        boolean[][] open = sac.getOpenGrid();
        int rows = open.length;
        int cols = open[0].length;

        cellSize = Math.min(W / (cols + 14), H / (rows + 12));
        sacX = (W - cols * cellSize) / 2;
        sacY = H / 10;
    }

    private void drawBackpackGrid(Graphics2D g) {
        Objects.requireNonNull(g);
        boolean[][] open = sac.getOpenGrid();
        int rows = open.length;
        int cols = open[0].length;

        g.setColor(new Color(20, 0, 40, 150));
        g.fillRoundRect(
                sacX - 15,
                sacY - 15,
                cols * cellSize + 30,
                rows * cellSize + 30,
                25,
                25);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = sacX + c * cellSize;
                int y = sacY + r * cellSize;
                g.setColor(open[r][c]
                        ? new Color(90, 0, 160, 150)
                        : new Color(20, 0, 30, 120));
                g.fillRoundRect(x, y, cellSize - 6, cellSize - 6, 15, 15);
            }
        }
    }

    private void drawAllItems(Graphics2D g) {
        Objects.requireNonNull(g);
        for (Map.Entry<ItemGraphique, Item> e : itemsGraphique.entrySet()) {
            if (!itemsDepart.contains(e.getKey()) && !itemsMerchant.contains(e.getKey())) {
                Screen.drawItem(g, e.getKey(), itemsGraphique, cellSize);
            }
        }

        for (ItemGraphique ig : itemsDepart) {
            Screen.drawItem(g, ig, itemsGraphique, cellSize);
        }

        for (ItemGraphique ig : itemsMerchant) {
            Screen.drawItem(g, ig, itemsGraphique, cellSize);
        }
    }

    /* ========================= BOUTON ========================= */

    private void drawBuyButton(Graphics2D g, String text) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(text);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();

        int w = fm.stringWidth(text) + 48;
        int h = fm.getAscent() + 28;
        int x = 40;
        int y = fenetre.getRealHeight() - h - 40;

        g.setColor(new Color(60, 160, 60));
        g.fillRoundRect(x, y, w, h, 15, 15);

        g.setColor(Color.WHITE);
        g.drawString(
                text,
                x + (w - fm.stringWidth(text)) / 2,
                y + (h + fm.getAscent()) / 2 - 4);
    }
    /* ========================= MESSAGE ========================== */

    private void drawMerchantMessage(Graphics2D g) {
        Objects.requireNonNull(g);
        if (merchantMessage == null) {
            return;
        }
        int w = 420;
        int h = 90;
        int x = (fenetre.getRealWidth() - w) / 2;
        int y = 40;

        // fond violet sombre translucide
        g.setColor(new Color(80, 20, 120, 200));
        g.fillRoundRect(x, y, w, h, 24, 24);

        // glow externe (effet cristal)
        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(180, 100, 255, 120));
        g.drawRoundRect(x - 2, y - 2, w + 4, h + 4, 26, 26);

        // bordure principale
        g.setStroke(new BasicStroke(2));
        g.setColor(new Color(220, 160, 255));
        g.drawRoundRect(x, y, w, h, 24, 24);

        // texte
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(245, 235, 255));

        FontMetrics fm = g.getFontMetrics();
        String[] lines = merchantMessage.split("\n");

        int ty = y + 32;
        for (String line : lines) {
            int tx = x + (w - fm.stringWidth(line)) / 2;
            g.drawString(line, tx, ty);
            ty += 22;
        }

    }

    private void drawGoldCounter(Graphics2D g) {
        Objects.requireNonNull(g);
        int gold = getHeroGold();

        int w = 180;
        int h = 50;
        int x = fenetre.getRealWidth() - w - 30;
        int y = 30;

        // fond violet sombre
        g.setColor(new Color(60, 20, 100, 200));
        g.fillRoundRect(x, y, w, h, 18, 18);

        // glow
        g.setStroke(new BasicStroke(2));
        g.setColor(new Color(190, 130, 255, 140));
        g.drawRoundRect(x - 2, y - 2, w + 4, h + 4, 20, 20);
        g.setStroke(new BasicStroke(1));

        // texte
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(new Color(245, 235, 255));

        String text = "Gold : " + gold;
        FontMetrics fm = g.getFontMetrics();
        int tx = x + (w - fm.stringWidth(text)) / 2;
        int ty = y + (h + fm.getAscent()) / 2 - 4;

        g.drawString(text, tx, ty);
    }

    /* ========================= INTERACTIONS ========================= */

    @Override
    public void onClick(int x, int y, Hero hero, Dungeon d) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(d);
        for (ItemGraphique ig : itemsMerchant) {
            if (Screen.inside(ig, itemsGraphique, x, y, cellSize)) {
                selectedMerchantItem = ig;
                return;
            }
        }

        if (tryPickFromBackpack(x, y))
            return;
        tryPickFromOutside(x, y);
    }

    private boolean tryPickFromBackpack(int x, int y) {
        if (x < sacX || y < sacY)
            return false;

        int col = (x - sacX) / cellSize;
        int row = (y - sacY) / cellSize;

        Item item = sac.getItemAtPoint(row, col);
        if (item == null)
            return false;

        sac.removeItem(row, col);
        dragging = findGraphicalItem(item);
        if (dragging == null)
            return false;
        selecteditem = dragging;

        dragOffsetX = x - dragging.getScreenX();
        dragOffsetY = y - dragging.getScreenY();

        itemsDepart.add(dragging);
        return true;
    }

    private int getHeroGold() {
        BackPack bp = hero.getBackpack();

        for (int r = 0; r < bp.getGrille().length; r++) {
            for (int c = 0; c < bp.getGrille()[0].length; c++) {
                if (bp.getGrille()[r][c] instanceof Items.Gold g) {
                    return g.goldValue();
                }
            }
        }
        return 0;
    }

    private int getBackpackColumn(int x) {
        return Math.min(Math.max((x - sacX) / cellSize, 0), 6);
    }

    private int getBackpackRow(int y) {
        return Math.min(Math.max((y - sacY) / cellSize, 0), 4);
    }

    private void snapToGrid(int col, int row) {
        dragging.SetscreenX(sacX + col * cellSize);
        dragging.SetscreenY(sacY + row * cellSize);
    }

    private void revertToOriginalPosition() {
        dragging.SetscreenX(dragging.getScreenXO());
        dragging.SetscreenY(dragging.getScreenYO());
    }

    private void tryPlaceInBackpack(int x, int y) {
        int col = getBackpackColumn(x);
        int row = getBackpackRow(y);

        snapToGrid(col, row);

        Item model = itemsGraphique.get(dragging);

        if (!sac.placeItem(model, row, col, itemsGraphique, itemsDepart, dragging, true)) {
            revertToOriginalPosition();
        }
    }

    private void tryPickFromOutside(int x, int y) {
        for (ItemGraphique ig : itemsDepart) {
            if (Screen.inside(ig, itemsGraphique, x, y, cellSize)) {
                dragging = ig;
                selecteditem = ig;
                dragOffsetX = x - ig.getScreenX();
                dragOffsetY = y - ig.getScreenY();
                return;
            }
        }
    }

    @Override
    public void onDrag(int x, int y) {
        if (dragging != null) {
            dragging.SetscreenX(x - dragOffsetX);
            dragging.SetscreenY(y - dragOffsetY);
        }
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

    /* ========================= ACHAT / VENTE ========================= */

    public void buySelectedItem() {
        if (selectedMerchantItem == null)
            return;

        Item item = itemsGraphique.get(selectedMerchantItem);
        if (item == null) {
            return;
        }
        boolean success = merchant.sellItem(
                item,
                hero,
                itemsGraphique,
                itemsDepart,
                selectedMerchantItem);
        if (!success) {
            merchantMessage = "Not enough gold\nYou can sell items to earn more gold.";
            messageTime = System.currentTimeMillis();
            return;
        }
        chargerItemsMerchant();
    }

    public void SellSelectedItem() {
        if (selecteditem == null)
            return;

        Item item = itemsGraphique.get(selecteditem);
        if (item == null) {
            return;
        }
        if (merchant.BuyItem(item, hero, itemsGraphique, itemsDepart, merchant.inventory(), selecteditem)) {
            chargerItemsMerchant();
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

    /* ========================= UTIL ========================= */

    private ItemGraphique findGraphicalItem(Item item) {
        Objects.requireNonNull(item);
        return itemsGraphique.entrySet().stream()
                .filter(e -> e.getValue() == item)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private boolean isInsideBackpack(int x, int y) {
        return x >= sacX && x <= sacX + cellSize * 7 &&
                y >= sacY && y <= sacY + cellSize * 5;
    }

    private void ensureItemInOutsideList() {
        if (!itemsDepart.contains(dragging)) {
            itemsDepart.add(dragging);
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

    @Override
    public boolean canLeaveScreen() {
        // Check if there are any curse items still outside the backpack
        for (var item : itemsDepart) {
            Item itemModel = itemsGraphique.get(item);
            if (itemModel == null) {
                return false;
            }
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

        // Also remove merchant items from the main items map
        for (var item : itemsMerchant) {
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
