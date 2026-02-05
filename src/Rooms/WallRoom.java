package Rooms;

/**
 * Represents a Wall Room, which is essentially a room that blocks movement or interaction.
 * The hero cannot enter or interact meaningfully with this room.
 */
public record WallRoom() implements RoomType {

    /**
     * Compact constructor.
     */
    public WallRoom {
        // No additional initialization needed
    }

    /**
     * Returns the type name of this room.
     */
    @Override
    public String getTypeName() {
        return "WALL";
    }

//    public void enter(Hero hero) {
//        // No action needed: wall block.
//    }
}
