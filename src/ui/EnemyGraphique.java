package ui;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class EnemyGraphique {
    // Image representing the enemy
    private BufferedImage image;
    // Position of the enemy on the screen (top-left corner)
    private int screenX;
    private int screenY;
    // Dimensions to draw the enemy (width and height)
    private int width;
    private int height;
    // Flag to indicate if the enemy is currently in a "hit" state (e.g. flashing
    // when damaged)
    private boolean isHit = false;
    // Number of frames remaining for the "hit" effect
    private int hitFrames = 0;

    // Constructor initializing with only the image; positions and size default to
    // zero
    public EnemyGraphique(BufferedImage image) {
        Objects.requireNonNull(image);
        this.image = image;
        this.screenX = 0;
        this.screenY = 0;
        this.width = 0;
        this.height = 0;
    }

    // Constructor initializing all properties
    public EnemyGraphique(BufferedImage image, int sx, int sy, int w, int h) {
        Objects.requireNonNull(image);

        this.image = image;
        this.screenX = sx;
        this.screenY = sy;
        this.width = w;
        this.height = h;
    }

    // Alternative constructor for the game screen, with name (name is unused here)
    public EnemyGraphique(String name, BufferedImage image, int screenX, int screenY) {
        Objects.requireNonNull(image);
        Objects.requireNonNull(name);
        Objects.requireNonNull(screenX);
        Objects.requireNonNull(screenY);
        this.image = image;
        this.screenX = screenX;
        this.screenY = screenY;
    }

    // Getters and setters for image
    public BufferedImage getImage() {
        return image;
    }

    public void SetImage(BufferedImage img) {
        Objects.requireNonNull(img);
        image = img;
    }

    // Getters and setters for width and height
    public int getWidth() {
        return width;
    }

    public int setWidth(int width) {
        this.width = width;
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int setHeight(int height) {
        this.height = height;
        return height;
    }

    // Getters and setters for screen position
    public int getScreenX() {
        return screenX;
    }

    public int SetscreenX(int x) {
        screenX = x;
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }

    public int SetscreenY(int y) {
        screenY = y;
        return screenY;
    }

    /**
     * Called when the enemy is hit by the player or an attack.
     * This triggers the "hit" visual effect.
     */
    public void hit() {
        isHit = true;
        hitFrames = 10; // Duration of hit effect in frames (~10 frames)
    }

    /**
     * Update method to be called every frame/tick.
     * Decreases hitFrames counter and disables hit state when time runs out.
     */
    public void update() {
        if (hitFrames > 0) {
            hitFrames--;
            if (hitFrames == 0) {
                isHit = false;
            }
        }
    }

    // Getter for remaining hit effect frames
    public int getHitFrames() {
        return hitFrames;
    }

    // Check if the enemy is currently in hit state
    public boolean isHit() {
        return isHit;
    }
}
