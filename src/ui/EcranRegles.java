package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Objects;

import model.Dungeon;
import model.Hero;

public class EcranRegles implements Screen {

    private final ZenGameView fenetre; // Reference to the main game window/view
    // Constructor receives main window reference to allow screen switching

    public EcranRegles(ZenGameView fenetre) {
        this.fenetre = fenetre;
    }

    /**
     * Render method draws the rules screen.
     * 
     * @param g     Graphics2D context used for drawing
     * @param realW Width of the screen in pixels
     * @param realH Height of the screen in pixels
     * @param hero  The hero model (unused here, but required by interface)
     */
    @Override
    public void render(Graphics2D g, int realW, int realH, Hero hero) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(fenetre);
        // Draw background as dark blue rectangle covering whole screen
        g.setColor(new Color(20, 0, 60));
        g.fillRect(0, 0, realW, realH);

        // Draw title text "Règles du jeu" (Game Rules) in white, bold, large font
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, realH / 12));
        g.drawString("Règles du jeu", realW / 2 - 200, realH / 6);

        // Draw the list of rules with smaller, plain font
        g.setFont(new Font("Arial", Font.PLAIN, realH / 25));
        int x = realW / 6; // left margin
        int y = realH / 3; // vertical start position

        g.drawString("- Explore le donjon.", x, y);
        g.drawString("- Combat au tour par tour.", x, y + 40);
        g.drawString("- Optimise ton sac à dos.", x, y + 80);
        g.drawString("- Trouve objets et reliques.", x, y + 120);

        // Draw a "Retour" (Back) button at top-left corner

        int bw = realW / 6; // button width
        int bh = realH / 14; // button height

        // Button background in teal color, with rounded corners
        g.setColor(new Color(0, 180, 200));
        g.fillRoundRect(20, 20, bw, bh, 20, 20);

        // Button text "Retour" in black, bold font, vertically centered
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, bh / 2));
        g.drawString("Retour", 40, 20 + bh / 2 + 5);
    }

    /**
     * Handles click events on the screen.
     * 
     * @param x    X coordinate of the mouse click
     * @param y    Y coordinate of the mouse click
     * @param hero The hero model (unused here)
     */
    @Override
    public void onClick(int x, int y, Hero hero, Dungeon d) {
        Objects.requireNonNull(fenetre);
        Objects.requireNonNull(hero);
        Objects.requireNonNull(d);
        int bw = fenetre.getRealWidth() / 6; // button width
        int bh = fenetre.getRealHeight() / 14; // button height

        // Check if click is inside the "Retour" button area
        if (x >= 20 && x <= 20 + bw && y >= 20 && y <= 20 + bh) {
            // Switch back to main menu screen when button clicked
            fenetre.setScreen(new EcranMenuPrincipal(fenetre));
        }
    }
}
