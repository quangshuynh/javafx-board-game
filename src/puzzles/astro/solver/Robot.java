package puzzles.astro.solver;

import java.util.Objects;

/**
 * A single treasure that exists in the dungeon.
 *
 * @author RIT CS
 */
public class Robot implements Comparable<Robot> {
    /** robot symbol */
    private final char symbol;
    /** robot row */
    private final int row;
    /** robot column */
    private final int col;

    /**
     * Create a new robot
     *
     * @param symbol char symbol of robot
     * @param row row coordinate
     * @param column column coordinate
     */
    public Robot(char symbol, int row, int column) {
        this.symbol = symbol;
        this.row = row;
        this.col = column;
    }


    /**
     * Get the symbol char
     *
     * @return symbol
     */
    public char getSymbol() {
        return this.symbol;
    }

    /**
     * Get the robot row
     *
     * @return robot row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Get the robot column
     *
     * @return robot col
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Two robots are equal iff they have the same symbol, row & col
     *
     * @param o the other object
     * @return whether they are equal or not
     */
    @Override
    public boolean equals(Object o) {
        // this method was generated by IntelliJ
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Robot robot2 = (Robot) o;
        return symbol == robot2.symbol && row == robot2.row && col == robot2.col;
    }

    /**
     * Uses all the fields of the robot and hash them together.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(symbol, row, col);
    }

    /**
     * Returns a string with the robot symbol and row and col
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.symbol + String.valueOf(row) + col;
    }

    /**
     * Robots naturally compare themselves alphabetically by name.
     *
     * @param other the object to be compared.
     * @return a value less than, equal, or greater to zero when comparing
     * this treasure to the other treasure
     */
    @Override
    public int compareTo(Robot other) {
        return this.symbol - other.symbol;
    }
}