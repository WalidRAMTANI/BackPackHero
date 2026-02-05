package ui;

/**
 * Interface for components that support drag operations.
 */
public interface DragSupport extends GameInterface {
    /**
     * Called when the component is dragged to a new position.
     *
     * @param x the new x-coordinate during dragging
     * @param y the new y-coordinate during dragging
     */
    void onDrag(int x, int y);
}
