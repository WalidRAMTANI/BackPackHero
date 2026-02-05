package ui;

/**
 * Represents a rectangular button with position and size.
 *
 * @param x the x-coordinate of the button's top-left corner
 * @param y the y-coordinate of the button's top-left corner
 * @param w the width of the button
 * @param h the height of the button
 */
public record Button(int x, int y, int w, int h) {
	public boolean contains(int mx, int my) {
	    return mx >= x && mx <= x + w &&
	           my >= y && my <= y + h;
	}

}
