package Rooms;

/**
 * Interface representing different types of rooms in the game.
 * Each room type must provide a name identifying its type.
 */
public interface RoomType {
    /**
     * Returns the name/type of this room.
     * @return String representing the room type name.
     */
    String getTypeName();
    // void enter(Hero hero);
}
