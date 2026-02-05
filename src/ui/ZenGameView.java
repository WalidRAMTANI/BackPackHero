package ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Objects;

import com.github.forax.zen.Application;
import com.github.forax.zen.Event;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;
import com.github.forax.zen.ScreenInfo;

import Items.Item;
import model.Dungeon;
import model.Hero;

public class ZenGameView {

    // Current screen shown in the game (implements Screen)
    private Screen currentScreen;

    // Current window width and height
    private int realW = 800;
    private int realH = 600;
    // Mapping between graphical item objects and their logical model item
    private HashMap<ItemGraphique, Item> items;

    // Map overlay
    private boolean showMap = false;
    private MapOverlay mapOverlay;

    public ZenGameView() {
        this.currentScreen = null;
        this.realW = 800;
        this.realH = 700;
        this.items = new HashMap<>();
    }

    // Set the graphical items map (called from screens)
    public void setItems(HashMap<ItemGraphique, Item> items) {
        Objects.requireNonNull(items);
        this.items = items;
    }

    // Get current items map
    public HashMap<ItemGraphique, Item> getItems() {
        Objects.requireNonNull(items);
        return items;
    }

    // Change the current screen
    public void setScreen(Screen screen) {
        Objects.requireNonNull(screen);
        this.currentScreen = screen;
        this.showMap = false; // Close map if we change screens
    }

    // Alternative setter with null check
    public void setCurrentScreen(Screen s) {
        Objects.requireNonNull(s);
        this.currentScreen = s;
    }

    public int getRealWidth() {
        return realW;
    }

    public int getRealHeight() {
        return realH;
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public void toggleMap() {
        showMap = !showMap;
    }

    public void closeMap() {
        showMap = false;
    }

    private void handlePointerEvent(PointerEvent pe, Hero hero, Dungeon d) {
        Objects.requireNonNull(pe);
        Objects.requireNonNull(hero);
        Objects.requireNonNull(d);
        int x = pe.location().x();
        int y = pe.location().y();

        switch (pe.action()) {
            case POINTER_DOWN -> {
                if (showMap) {
                    mapOverlay.onClick(x, y, hero, d);
                } else {
                    currentScreen.onClick(x, y, hero, d);
                }
            }
            case POINTER_UP -> {
                if (currentScreen instanceof EcranCombat ec) {
                    ec.onRelease(x, y, hero);
                } else {
                    currentScreen.onRelease(x, y);
                }
            }
            case POINTER_MOVE -> {
                if (currentScreen instanceof EcranCombat ec) {
                    ec.hover(x, y);
                }
                if (currentScreen instanceof DragSupport ds) {
                    ds.onDrag(x, y);
                }
            }
        }
    }

    private void handleKeyboardEvent(KeyboardEvent ke, Hero hero) {
        Objects.requireNonNull(ke);
        Objects.requireNonNull(hero);
        KeyboardEvent.Key key = ke.key();

        // Global Key: Toggle Map
        if (key == KeyboardEvent.Key.M) {
            if (showMap) {
                showMap = false;
            } else if (!(currentScreen instanceof EcranMenuPrincipal) && currentScreen.canLeaveScreen()) {
                showMap = true;
            }
            return;
        }

        // Screen-Specific Keys
        switch (currentScreen) { 	
            case EcranTreasure ep -> {
                if (key == KeyboardEvent.Key.O)
                    ep.openTreasure();
                if (key == KeyboardEvent.Key.K)
                    ep.onRotate();
            }
            case EcranMerchant ep -> {
                if (key == KeyboardEvent.Key.B)
                    ep.buySelectedItem();
                if (key == KeyboardEvent.Key.S)
                    ep.SellSelectedItem();
                if (key == KeyboardEvent.Key.K)
                    ep.onRotate();
            }
            case EcranCombat ep -> {
                if (key == KeyboardEvent.Key.K) {
                    ep.enemyTurn(hero);
                    ep.onRotate();
                }
                if (key == KeyboardEvent.Key.A) {
                	ep.expand();
                }
            }
            case EcranHealer ep -> {
                if (key == KeyboardEvent.Key.H)
                    ep.heal(hero);
            }
            case EcranPartie ep -> {
                if (key == KeyboardEvent.Key.K)
                    ep.onRotate();
            }
            default -> {
            }
        }
    }

    /**
     * Starts the game main loop.
     * Runs the Zen Application loop and listens for input events.
     *
     * @param hero The hero model object
     * @param f    The floor model object
     */
    public void start(Hero hero, Dungeon d) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(d);

        // Launch Zen application with black background
        Application.run(Color.BLACK, context -> {

            // Initialize screen dimensions
            ScreenInfo info = context.getScreenInfo();
            realW = info.width();
            realH = info.height();

            // Initialize map overlay
            this.mapOverlay = new MapOverlay(this, d);

            while (true) {
                // Poll or wait for the next event (max wait 10ms)
                Event e = context.pollOrWaitEvent(10);

                // ==== Event handling for different screens ====
                if (e instanceof KeyboardEvent ke && ke.action() == KeyboardEvent.Action.KEY_PRESSED) {
                    handleKeyboardEvent(ke, hero);
                } else if (e instanceof PointerEvent pe) {
                    handlePointerEvent(pe, hero, d);
                }
                
             // Victory: press P to go back to main menu
                if (e instanceof KeyboardEvent ke &&
                        ke.action() == KeyboardEvent.Action.KEY_PRESSED &&
                        ke.key() == KeyboardEvent.Key.P) {

                        System.out.println("KEY PRESSED: " + ke.key());
                        setScreen(new EcranMenuPrincipal(this));


                        //if (mapOverlay.isVictory()) {
                          //  setScreen(new EcranMenuPrincipal(this));
                        //}
                    }
                // ------------------------------------------------------------------------------------------------
                // ==== Render the current frame ====
                context.renderFrame(g -> {
                    if (currentScreen != null) {
                        currentScreen.render(g, realW, realH, hero);
                    }
                    if (showMap) {
                        mapOverlay.render(g, realW, realH, hero);
                    }
                });
            }
        });
    }
}
