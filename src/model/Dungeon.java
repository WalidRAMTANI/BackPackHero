package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents the entire dungeon containing multiple floors.
 */
public class Dungeon {
	private ArrayList<Floor> floors;
	private Point heroPosition;
	private int currentFloor;

	/**
	 * @return Current floor index
	 */
	public int getCurrentFloorIndex() {
		return currentFloor;
	}

	/**
	 * @return true if current floor is the last one
	 */
	public boolean isLastFloor() {
		return currentFloor == floors.size() - 1;
	}

	/**
	 * @return Current hero position in the dungeon
	 */
	public Point getHeroPosition() {
		return heroPosition;
	}

	/**
	 * @param p New hero position
	 */
	public void setHeroPosition(Point p) {
		Objects.requireNonNull(p);
		this.heroPosition = p;
	}

	/**
	 * @return List of all floors
	 */
	public ArrayList<Floor> getFloors() {
		return floors;
	}

	/**
	 * Constructor for Dungeon.
	 */
	public Dungeon() {
		this.floors = generateFloors();
		this.currentFloor = 0;
		this.heroPosition = floors.get(currentFloor).getEntrance();
	}

	/**
	 * @return Generated list of floors
	 */
	private static ArrayList<Floor> generateFloors() {
		var f = new ArrayList<Floor>();
		f.add(new Floor());
		f.add(new Floor());
		f.add(new Floor());
		return f;
	}

	/**
	 * Advances to the next floor if available.
	 */
	public void goNextFloor() {
		if (currentFloor < floors.size() - 1) {
			this.currentFloor++;
			this.heroPosition = floors.get(currentFloor).getEntrance();
		}
	}

	/**
	 * @return The current floor object
	 */
	public Floor getCurrentFloor() {
		return floors.get(currentFloor);
	}
	
	public boolean isDungeonCompleted() {
	    return currentFloor == floors.size() - 1;
	}

	
}
