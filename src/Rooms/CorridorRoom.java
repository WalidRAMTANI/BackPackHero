package Rooms;

// Represents a corridor room, which can be an entry or exit but not both at the same time
public record CorridorRoom(boolean isEntry, boolean isExit, boolean closed) implements RoomType {

    public CorridorRoom {
        // Prevent room from being both entry and exit simultaneously
        if (isEntry && isExit) {
            isEntry = false;
            isExit = false;
            
        }
        
    }

    @Override
    public String getTypeName() {
        return "CORRIDOR";
    }
}
