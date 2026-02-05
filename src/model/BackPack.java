package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import Items.Gold;
import Items.Item;
import ui.ItemGraphique;

public class BackPack {
    // Grid representing items, each cell can hold one item reference or null
    private Item[][] grille;
    // Boolean grid marking accessible cells (true = accessible)
    private boolean[][] openGrid;
    // Map storing each item and the list of points (cells) it occupies
    private HashMap<Item, ArrayList<Point>> items;

    public BackPack() {
        grille = new Item[5][7];
        openGrid = new boolean[5][7];

        // Initialize accessible cells (3x3 block in the middle)
        for (int i = 1; i <= 3; i++) {
            for (int j = 2; j <= 4; j++) {
                openGrid[i][j] = true;
            }
        }

        items = new HashMap<>();
    }

    // Returns the Gold item in the backpack, or null if none
    public Gold getGold() {
        for (var item : items.keySet()) {
            if (item instanceof Gold gold) {
                return gold;
            }
        }
        return null;
    }

    // Getter for the map of items and their occupied cells
    public HashMap<Item, ArrayList<Point>> getItems() {
        return items;
    }

    // Getter for the item grid
    public Item[][] getGrille() {
        return grille;
    }

    // Getter for accessible cells grid
    public boolean[][] getOpenGrid() {
        return openGrid;
    }

    private boolean check_boundaries(List<Point> s, int row, int col) {
        Objects.requireNonNull(s);
        // Check boundaries and availability for all points the item will occupy
        for (Point p : s) {
            int r = row + p.y;
            int c = col + p.x;

            if (r < 0 || c < 0 || r >= openGrid.length || c >= openGrid[0].length) {
                return false;
            }

            if (!openGrid[r][c] || grille[r][c] != null) {
                return false;
            }
        }
        return true;
    }

    // Attempt to place an item at (row, col) if all occupied cells are free and
    // accessible
    public boolean placeItem(Item item, int row, int col, HashMap<ItemGraphique, Item> itemsGraphique,
            ArrayList<ItemGraphique> itemDeparts, ItemGraphique dragging, boolean addGold) {

        Objects.requireNonNull(item);
        Objects.requireNonNull(itemsGraphique);
        Objects.requireNonNull(itemDeparts);
        Objects.requireNonNull(dragging);

        var shape = item.occupiedCases();
        if (item instanceof Gold && Gold.hasGold(this)) {
            Point coordinate = Gold.getGold(this);
            if (coordinate == null) {
                return false;
            }
            Gold existingGold = (Gold) grille[coordinate.x][coordinate.y];

            // Supprimer l'item déplacé (celui qu'on veut fusionner)
            items.remove(item);
            itemsGraphique.remove(dragging, item);

            // Trouver l'objet graphique correspondant à l'or existant
            Optional<ItemGraphique> optGraphicItem = itemsGraphique.keySet().stream()
                    .filter(k -> itemsGraphique.get(k).equals(existingGold))
                    .findFirst();

            // Supprimer l'item en cours de déplacement
            itemDeparts.remove(dragging);

            if (addGold) {
                // Ajouter la valeur du gold déplacé à l'existant
                existingGold.setGoldValue(existingGold.goldValue() + ((Gold) item).goldValue());
            }
            // Mettre à jour la map graphique pour refléter la modification si besoin
            optGraphicItem.ifPresent(graphicItem -> itemsGraphique.put(graphicItem, existingGold));

            row = coordinate.x;
            col = coordinate.y;

            // Pas besoin de supprimer ou ajouter un nouvel objet Gold, on modifie
            // l'existant

        } else {
            if (!check_boundaries(shape, row, col)) {
                return false;
            }
            itemDeparts.remove(dragging);
        }

        // Mark all occupied cells as taken by this item and no longer accessible
        ArrayList<Point> realPoints = new ArrayList<>();
        for (Point p : shape) {
            int r = row + p.y;
            int c = col + p.x;

            grille[r][c] = item;
            // openGrid[r][c] = false;
            realPoints.add(new Point(r, c));
        }

        // Store item and its occupied points in the map
        items.put(item, realPoints);

        return true;
    }

    /**
     * Forces placement of an item at (row, col), removing any existing items in the
     * way.
     * Used for Curses.
     */
    public boolean forcePlaceItem(Item item, int row, int col) {
        Objects.requireNonNull(item);
        ArrayList<Point> shape = item.occupiedCases();

        // 1. Check if the placement is within valid grid boundaries (ignoring openGrid
        // for now if we assume curses can land anywhere valid,
        // OR we strictly respect openGrid. User said "choisi une case", presumably an
        // unlocked one.
        // Let's assume we respect grid boundaries and unlocked status.)
        for (Point p : shape) {
            int r = row + p.y;
            int c = col + p.x;
            if (r < 0 || c < 0 || r >= openGrid.length || c >= openGrid[0].length) {
                return false; // Out of bounds
            }
            if (!openGrid[r][c]) {
                // Determine if we want to allow curses on locked slots.
                // Usually not, as they aren't "in the bag". Return false for now.
                return false;
            }
        }

        // 2. Identify and Remove items in the way
        // We might encounter multiple items or the same item multiple times. Use a Set
        // to avoid double removal.
        HashSet<Item> itemsToRemove = new HashSet<>();
        for (Point p : shape) {
            int r = row + p.y;
            int c = col + p.x;
            Item existing = grille[r][c];
            if (existing != null) {
                itemsToRemove.add(existing);
            }
        }

        for (Item toRemove : itemsToRemove) {
            // Need the top-left coordinate of the item to call removeItem properly?
            // Actually removeItem(r, c) works by finding the item at that point.
            // But since we have the item object, we can just remove it directly from
            // maps/grid.
            // Re-using removeItem might be safer if we can find a point it occupies.
            ArrayList<Point> pts = items.get(toRemove);
            if (pts != null && !pts.isEmpty()) {
                Point firstPt = pts.get(0);
                removeItem(firstPt.x, firstPt.y); // This clears the grid for ALL points of that item
            }
        }

        // 3. Place the new item
        // We can just call placeItem now since the space should be free.
        // However, placeItem might require `itemsGraphique` which we don't have here.
        // Wait, placeItem method signature in this file is:
        // placeItem(Item item, int row, int col, HashMap<ItemGraphique, Item>
        // itemsGraphique, ArrayList<ItemGraphique> itemDeparts, ItemGraphique dragging,
        // boolean addGold)
        // That is very coupled to the UI. We need a simpler internal place logic or we
        // need to pass nulls if safe.
        // But looking at existing methods, `grille` and `items` are updated.
        // Let's implement the core logic here directly to avoid UI dependency issues in
        // the Model.

        ArrayList<Point> realPoints = new ArrayList<>();
        for (Point p : shape) {
            int r = row + p.y;
            int c = col + p.x;
            grille[r][c] = item;
            realPoints.add(new Point(r, c));
        }
        items.put(item, realPoints);

        return true;
    }

    // Custom toString for debugging, printing grid, accessibility, and items
    // mapping
    @Override
    public String toString() {
        return "BackPack{items=" + items.size() + "}";
    }

    // Get the item located at a specific cell (x,y)
    public Item getItemAtPoint(int x, int y) {
        Point p = new Point(x, y);

        for (Map.Entry<Item, ArrayList<Point>> entry : items.entrySet()) {
            if (entry.getValue().contains(p)) {
                return entry.getKey();
            }
        }

        // No item at this position
        return null;
    }

    // Remove an item by clicking on any of its occupied cells (row, col)
    public boolean removeItem(int row, int col) {
        // Find the item at the clicked position
        Item item = getItemAtPoint(row, col);
        if (item == null) {
            return false;
        }

        // Get all cells occupied by this item
        ArrayList<Point> cases = items.get(item);
        if (cases == null) {
            return false;
        }

        // Free all cells occupied by the item
        for (Point p : cases) {
            grille[p.x][p.y] = null;
            openGrid[p.x][p.y] = true;
        }

        // Remove the item from the map
        items.remove(item);

        return true;
    }

    // Check if any item occupies the point (x,y)
    private boolean containPointItems(int x, int y) {
        return items.values().stream().anyMatch(t -> t.contains(new Point(x, y)));
    }

    // Check if item with occupied shape can be placed at (x,y)
    public boolean canPlace(ArrayList<Point> lst, int x, int y) {
        for (Point p : lst) {
            int r = x + p.x;
            int c = y + p.y;

            if (r < 0 || r >= openGrid.length || c < 0 || c >= openGrid[0].length) {
                return false;
            }

            if (!openGrid[r][c] || containPointItems(r, c)) {
                return false;
            }
        }
        return true;
    }

    // Placeholder, currently empty - meant for possible future interactions with
    // all items
    public void interactAll() {
    }

    // Expand accessible grid based on provided points, only if all those points are
    // currently inaccessible
    public void expand(List<Point> l) {
        Objects.requireNonNull(l);
        boolean allFalse = l.stream()
                .allMatch(p -> p.x >= 0 && p.x < openGrid[0].length &&
                        p.y >= 0 && p.y < openGrid.length &&
                        !openGrid[p.y][p.x]);
        if (allFalse) {
            l.forEach(p -> openGrid[p.y][p.x] = true);
        }
    }

    private boolean isInside(int x, int y) {
        return x >= 0 && x < 5 && y >= 0 && y < 7;
    }

    // a method to test if we can expand at a certain point.
    public boolean canExpand(int x, int y) {

        if (!isInside(x, y) || openGrid[x][y] == true)
            return false;
        // check if any of the 4 neighbors are accessible
        int[][] directions = {
                { 1, 0 },
                { -1, 0 },
                { 0, 1 },
                { 0, -1 }
        };

        for (int[] d : directions) {
            int nx = x + d[0];
            int ny = y + d[1];

            if (isInside(nx, ny) && openGrid[nx][ny]) {
                return true;
            }
        }

        return false;
    }

    // get list of accessible cells to expand
    public ArrayList<Point> getExpandList() {
        ArrayList<Point> list = new ArrayList<>();

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 7; y++) {
                if (canExpand(x, y)) {
                    list.add(new Point(y, x));
                }
            }
        }
        return list;
    }

    public List<Item> getAdjacentItems(Item item) {
        Objects.requireNonNull(item);
        if (!items.containsKey(item))
            return List.of();

        Set<Item> adjacentItems = new HashSet<>();
        ArrayList<Point> occupiedPoints = items.get(item);

        for (Point p : occupiedPoints) {
            // Check neighbors: up, down, left, right
            int[][] directions = {
                    { 0, 1 }, // down
                    { 0, -1 }, // up
                    { 1, 0 }, // right
                    { -1, 0 } // left
            };

            for (int[] dir : directions) {
                int newRow = p.x + dir[0];
                int newCol = p.y + dir[1];

                // Check boundaries
                if (newRow >= 0 && newRow < grille.length && newCol >= 0 && newCol < grille[0].length) {
                    Item adjacent = grille[newRow][newCol];
                    if (adjacent != null && !adjacent.equals(item)) {
                        adjacentItems.add(adjacent);
                    }
                }
            }
        }

        return new ArrayList<>(adjacentItems);
    }

    public void activate(Hero hero) {
        for (var item : items.keySet()) {
            item.notOnUse(hero, null, null);
        }
    }

    public void desactivate(Hero hero) {
        for (var item : items.keySet()) {
            item.baseShape();
        }
    }

}
