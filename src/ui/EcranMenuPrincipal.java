package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;

import model.Dungeon;
import model.Hero;

public class EcranMenuPrincipal implements Screen {

    private final ZenGameView fenetre;

    private BufferedImage imageFond;
    private Hero hero;

    public EcranMenuPrincipal(ZenGameView fenetre) {
        Objects.requireNonNull(fenetre);
        this.fenetre = fenetre;

        try {
            chargerBG();
        } catch (Exception e) {
            System.out.println("Menu image not found");
            imageFond = null;
        }
    }

    private void chargerBG() {
        imageFond = Screen.load("/ressources/menu_fond.png");
    }

    @Override
    public void render(Graphics2D g, int realW, int realH, Hero hero) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(hero);
        // fullscreen background
        if (!imageFond.equals(null)) {
            g.drawImage(imageFond, 0, 0, realW, realH, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, realW, realH);
        }

        // centered title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        String titre = "Backpack Hero";
        int tw = g.getFontMetrics().stringWidth(titre);
        g.drawString(titre, (realW - tw) / 2, realH / 6);

        drawButton(g, "Play", realW, realH, 0);
        drawButton(g, "Rules", realW, realH, 1);
        drawButton(g, "Quit", realW, realH, 2);
    }

    private void drawButton(Graphics2D g, String txt, int realW, int realH, int index) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(txt);
        int bw = realW / 4;
        int bh = realH / 12;
        int bx = (realW - bw) / 2;
        int by = realH / 3 + index * (bh + 40);

        g.setColor(new Color(120, 0, 180, 200));
        g.fillRoundRect(bx, by, bw, bh, 30, 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, bh / 3));

        int tw = g.getFontMetrics().stringWidth(txt);
        g.drawString(txt, bx + (bw - tw) / 2, by + bh / 2 + 10);
    }

    @Override
    public void onClick(int x, int y, Hero hero, Dungeon d) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(d);
        int realW = fenetre.getRealWidth();
        int realH = fenetre.getRealHeight();

        int bw = realW / 4;
        int bh = realH / 12;
        int bx = (realW - bw) / 2;

        // --- Button 1: Play ---
        int by1 = realH / 3;
        if (inButton(x, y, bx, by1, bw, bh)) {
            fenetre.setScreen(new EcranPartie(fenetre, hero, new ArrayList<>()));
            return;
        }

        // --- Button 2: Rules ---
        int by2 = by1 + bh + 40;
        if (inButton(x, y, bx, by2, bw, bh)) {
            fenetre.setScreen(new EcranRegles(fenetre));
            return;
        }

        // --- Button 3: Quit ---
        int by3 = by2 + bh + 40;
        if (inButton(x, y, bx, by3, bw, bh)) {
            System.exit(0);
        }
    }

    private boolean inButton(int x, int y, int bx, int by, int bw, int bh) {
        return x >= bx && x <= bx + bw && y >= by && y <= by + bh;
    }
}
