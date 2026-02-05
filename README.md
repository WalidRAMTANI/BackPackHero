# 🎒 Backpack Hero (Java Edition)

A strategic dungeon crawler game implemented in Java, inspired by the popular game *Backpack Hero*. Manage your inventory, explore mysterious dungeons, and defeat powerful enemies using your wits and spatial organization skills.

---

## 🎮 Game Overview

In **Backpack Hero**, your backpack is your strength. Unlike traditional RPGs where inventory is just storage, here, the **arrangement** of your items determines your power in battle.

### Key Features:
- **Spatial Inventory Management:** Rotate and place items in your grid-based backpack. Some items benefit from their position or adjacency to others.
- **Dynamic Dungeon Exploration:** Navigate through procedurally generated floors and rooms including corridors, treasure rooms, and merchant stalls.
- **Strategic Combat:** Face various enemies with unique attack patterns and effects (poisons, curses, burns).
- **Hero Development:** Gain XP and level up to increase your health and expand your backpack's capacity.
- **NPC Interactions:** Trade with merchants, heal at the healer, and discover rare artifacts.

---

## 🏗️ Project Structure

The project follows the **MVC (Model-View-Controller)** pattern for a clean separation of concerns:

- `src/model/`: Core game logic (Hero, Dungeon, Floor, Backpack, NPCs).
- `src/view/`: UI components and screen management (using the Zen library).
- `src/controller/`: Input handling and game loop logic.
- `src/Items/`: Inventory system with various item types (Weapons, Gold, Potions, Curses).
- `src/Enemies/`: Enemy definitions and AI behavior.
- `src/Rooms/`: Dungeon room types and generation logic.
- `src/effects/`: Status effects system (Burn, Poison, etc.).
- `lib/`: Contains the `zen-6.0.jar` library for rendering.

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK) 25** or higher.
- **Apache Ant** (for building and running).

### How to Build and Run

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd Ramtani_Zeineddine_BackpackHero
   ```

2. **Build the project:**
   ```bash
   ant compile
   ```

3. **Run the game:**
   The `build.xml` is configured to create a JAR. You can run the main class directly:
   ```bash
   java -cp "classes:lib/*" SimpleGameData.SimpleGameData
   ```
   *Note: On Windows, use `;` instead of `:` in the classpath.*

4. **Generate Documentation:**
   ```bash
   ant javadoc
   ```

---

## 🛠️ Controls

- **Mouse:** Drag and drop items into your backpack.
- **Keyboard:** Use specific keys for actions (e.g., 'K' for rotation in certain contexts, as seen in the source).
- **Menu:** Navigate through the starting screen to begin your adventure.

---

## 📘 Documentation
Detailed project documentation can be found in the `docs/` folder:
- `user.pdf`: User guide and gameplay instructions.
- `dev.pdf`: Technical documentation for developers.

---

## 👤 Author
Developed by **Zeineddine Ramtani** as part of a University project (L3 Info 2025-2026).

---

## 🙏 Credits
- Graphics and sounds provided in the project resources.
- UI powered by the **Zen** library.
