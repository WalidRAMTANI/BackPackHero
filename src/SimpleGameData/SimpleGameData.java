package SimpleGameData;

import model.Dungeon;
import model.Hero;
import ui.EcranMenuPrincipal;
import ui.ZenGameView;

public class SimpleGameData {
    public static void main(String[] args) {

        // Create the main game window / view manager
        ZenGameView fenetre = new ZenGameView();

        // Initialize the hero character with name "loki" and starting level 0
        Hero hero = new Hero("loki", 0);

        Dungeon dungeon = new Dungeon();
        // Set the initial screen to the main menu
        fenetre.setScreen(new EcranMenuPrincipal(fenetre));

        // Start the game loop with the hero and the floor
        fenetre.start(hero, dungeon);
    }
}
