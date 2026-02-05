package ui;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class ItemGraphique {

    static int ID = 0;
    // Name identifier of the item (e.g., "sword", "shield")
    private String name;
    // id identiffier of the item
    private int id;

    // Image representing the item to draw on screen
    private BufferedImage image;

    // Current position of the item on the screen (top-left corner)
    private int screenX;
    private int screenY;

    // Current rotation state (0 to 3), each representing 90° steps
    private int rotation = 0;

    // Old position used to revert to if item drop fails (drag & drop rollback)
    private int oldX;
    private int oldY;

    /**
     * Constructor to initialize with name and image only.
     * Initial position and old position will default to 0.
     */
    public ItemGraphique(String name, BufferedImage image) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(image);
        this.name = name;
        this.image = image;
        this.id = ID++;
    }

    /**
     * Constructor with name, image, and initial position.
     * Also sets oldX and oldY to initial position for rollback.
     */
    public ItemGraphique(String name, BufferedImage image, int screenX, int screenY) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(image);
        this.name = name;
        this.image = image;
        this.screenX = screenX;
        this.screenY = screenY;
        this.oldX = screenX;
        this.oldY = screenY;
    }

    // Getter for the item image
    public BufferedImage getImage() {
        return image;
    }

    // Setter for the item image (non-null)
    public void SetImage(BufferedImage img) {
        Objects.requireNonNull(img);
        image = img;
    }

    // Get current rotation value (0-3)
    public int getRotation() {
        return rotation;
    }

    /**
     * Increment rotation by 1 step (90 degrees),
     * wrapping around after 3 back to 0.
     * Returns the new rotation value.
     */
    public int setRotation() {
        rotation = (rotation + 1) % 4;
        return rotation;
    }

    // Set current screen X position
    public void SetscreenX(int x) {
        screenX = x;
    }

    // Set current screen Y position
    public void SetscreenY(int y) {
        screenY = y;
    }

    // Get current screen X position
    public int getScreenX() {
        return screenX;
    }

    // Get current screen Y position
    public int getScreenY() {
        return screenY;
    }

    // Set old X position (used to revert on failed drop)
    public void SetscreenXO(int x) {
        oldX = x;
    }

    // Set old Y position (used to revert on failed drop)
    public void SetscreenYO(int y) {
        oldY = y;
    }

    // Get old X position
    public int getScreenXO() {
        return oldX;
    }

    // Get old Y position
    public int getScreenYO() {
        return oldY;
    }

    // Get item name
    public String getName() {
        return name;
    }
}
