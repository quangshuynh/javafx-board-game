package puzzles.astro.model;

import puzzles.common.Coordinates;
import puzzles.common.Direction;
import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static puzzles.common.Direction.*;

/**
 * Represents a configuration for solving Astro puzzles
 *
 * @author RIT CS
 * @author Quang Huynh (qth9368)
 */

public class AstroConfig implements Configuration{
    private static final String GOAL_SYMBOL = "*";
    private static final String EMPTY_CELL_SYMBOL = ".";
    private static final String ASTRONAUT_SYMBOL = "A";

    public Piece[][] grid;  // grid game
    private Coordinates astroCoords;  // astronaut coordinates
    private Coordinates robotCoords; // robot coordinates
    private static Coordinates goalCoords;  // goal coordinates
    private Coordinates prevMove;
    public int rows;  // rows
    public int cols;  // columns
    private Set<Piece> totalPieces;  // a set of all the pieces
    private Piece astronaut;  // astronaut piece


    /**
     * Construct new AstroConfig
     *
     * @param filename name of Astro file
     * @throws IOException
     */
    public AstroConfig(String filename) throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            totalPieces = new HashSet<>();
            /** Read dimension info */
            String line = br.readLine();
            String dim[] = line.split("\\s+");
            rows = Integer.parseInt(dim[0]);  // first line, first int is row
            cols = Integer.parseInt(dim[1]); // first line, second int is col
            grid = new Piece[rows][cols];  // initialize grid array with specified dimensions

            /** Read goal info */
            line = br.readLine();
            String[] goalLine = line.split("\\s+");
            String goalSymbol = goalLine[0];
            int goalRow = Integer.parseInt(goalLine[1].split(",")[0]);
            int goalColumn = Integer.parseInt(goalLine[1].split(",")[1]);
            Piece goal = new Piece(goalSymbol, goalCoords);
            goalCoords = new Coordinates(goalRow, goalColumn);
            grid[goalRow][goalColumn] = goal;


            /** Read astronaut info */
            line = br.readLine();
            String[] astroLine = line.split("\\s+");
            String astroSymbol = astroLine[0];
            int astroRow = Integer.parseInt(astroLine[1].split(",")[0]);
            int astroColumn = Integer.parseInt(astroLine[1].split(",")[1]);
            astroCoords = new Coordinates(astroRow, astroColumn);
            astronaut = new Piece(astroSymbol, astroCoords);
            grid[astroRow][astroColumn] = astronaut;
            totalPieces.add(astronaut);

            /** Read number of robots and robot info */
            line = br.readLine();
            int numRobots = Integer.parseInt(line);
            for(int i = 0; i < numRobots; i++) {
                line = br.readLine();
                String[] robotInfo = line.split("\\s+");
                char robotChar = robotInfo[0].charAt(0);
                String robotSymbol = String.valueOf(robotChar);
                int robotRow = Integer.parseInt(robotInfo[1].split(",")[0]);
                int robotColumn = Integer.parseInt(robotInfo[1].split(",")[1]);
                robotCoords = new Coordinates(robotRow, robotColumn);
                Piece robot = new Piece(robotSymbol, robotCoords);
                grid[robotRow][robotColumn] = robot;
                totalPieces.add(robot);
            }

            /** Assign Grid with remaining cells*/
            for(int row = 0; row < rows; row++) {
                for(int col = 0; col < cols; col++) {
                    if(grid[row][col] == null) {
                        Coordinates coords = new Coordinates(row, col);
                        Piece empty = new Piece(EMPTY_CELL_SYMBOL, coords);
                        grid[row][col] = empty;
                    }
                }
            }
        }
    }

    /**
     * Gets adjacent piece in all 4 directions
     *
     * @param coords coordinates
     * @param dir cardinal direction (n, s, e, w)
     * @return coordinates of adjacent piece
     */
    public static Coordinates adjacent_piece(Coordinates coords, Direction dir) {
        return switch(dir) {
            case NORTH -> new Coordinates(coords.row() - 1, coords.col());
            case SOUTH -> new Coordinates(coords.row() + 1, coords.col());
            case EAST -> new Coordinates(coords.row(), coords.col() + 1);
            case WEST -> new Coordinates(coords.row(), coords.col() - 1);
        };
    }

    /**
     * Copy constructor to move piece
     *
     * @param other other AstroConfig.
     * @param current current piece
     * @param dir direction to move
     */
    public AstroConfig(AstroConfig other, Piece current, Direction dir) {
        this.grid = new Piece[other.grid.length][other.grid[0].length];
        for(int row= 0; row < this.grid.length; row++){
            for(int col= 0; col< this.grid[0].length; col++){
                if(other.grid[row][col] != null && other.grid[row][col].equals(current)){
                    this.grid[row][col]=new Piece(EMPTY_CELL_SYMBOL, new Coordinates(row, col));
                    continue;
                }
                this.grid[row][col] = other.grid[row][col];
            }
        }
        Coordinates newPos = current.coords();
        while(isValid(this.grid, newPos ,dir)){
            newPos = adjacent_piece(newPos,dir);
        }
        Piece otherPiece = new Piece(current.name(), newPos);
        grid[newPos.row()][newPos.col()] = otherPiece;
        Piece movedPiece = new Piece(current.name(), newPos);
        this.astronaut = other.astronaut.equals(current) ? movedPiece : other.astronaut;
        this.totalPieces = new HashSet<>(other.totalPieces);
        this.totalPieces.remove(current);
        this.totalPieces.add(movedPiece);
        this.prevMove = newPos;
    }

    public boolean isValidMove(Piece[][] grid, Coordinates coords, Direction dir){
        Coordinates coordinates = coords;
        while(isValid(this.grid, coordinates, dir)) {
            coordinates = adjacent_piece(coordinates,dir);
        }
        if(switch(dir) {
            case Direction.NORTH -> coordinates.row() == 0;
            case Direction.SOUTH-> coordinates.row() == this.grid.length-1;
            case Direction.EAST-> coordinates.col() == this.grid[0].length-1;
            case Direction.WEST-> coordinates.col() == 0;
        }){
            return false;}
        return isValid(grid, coordinates, dir);
    }

    /**
     * Checks if direction is valid
     *
     * @param grid game grid
     * @param coord piece coordinates
     * @param dir cardinal direction (n, s, e, w)
     * @return whether the piece can move in a direction
     */
    public static boolean isValid(Piece[][] grid, Coordinates coord, Direction dir) {
        try {
            if(!coord.equals(goalCoords) && !(Objects.equals(grid[coord.row()][coord.col()].name(), ASTRONAUT_SYMBOL))) {
                return switch(dir) {
                    case Direction.NORTH -> (Objects.equals(grid[coord.row() - 1][coord.col()].name(), EMPTY_CELL_SYMBOL))
                            || (Objects.equals(grid[coord.row() - 1][coord.col()].name(), GOAL_SYMBOL)
                            && Objects.equals(grid[coord.row() - 2][coord.col()].name(), EMPTY_CELL_SYMBOL));
                    case Direction.SOUTH -> Objects.equals(grid[coord.row() + 1][coord.col()].name(), EMPTY_CELL_SYMBOL)
                            || (Objects.equals(grid[coord.row() + 1][coord.col()].name(), GOAL_SYMBOL)
                            && Objects.equals(grid[coord.row() + 2][coord.col()].name(), EMPTY_CELL_SYMBOL));
                    case Direction.EAST -> (Objects.equals(grid[coord.row()][coord.col() + 1].name(), EMPTY_CELL_SYMBOL))
                            || (Objects.equals(grid[coord.row()][coord.col() + 1].name(), GOAL_SYMBOL)
                            && Objects.equals(grid[coord.row()][coord.col() + 2].name(), EMPTY_CELL_SYMBOL));
                    case Direction.WEST -> (Objects.equals(grid[coord.row()][coord.col() - 1].name(), EMPTY_CELL_SYMBOL))
                            || (Objects.equals(grid[coord.row()][coord.col() - 1].name(), GOAL_SYMBOL)
                            && Objects.equals(grid[coord.row()][coord.col() - 2].name(), EMPTY_CELL_SYMBOL));
                };
            } else if(!coord.equals(goalCoords) && Objects.equals(grid[coord.row()][coord.col()]
                    .name(), ASTRONAUT_SYMBOL)) {
                return switch(dir) {
                    case Direction.NORTH -> Objects.equals(grid[coord.row() - 1][coord.col()].name(), EMPTY_CELL_SYMBOL)
                        || Objects.equals(grid[coord.row() - 1][coord.col()].name(), GOAL_SYMBOL);
                    case Direction.SOUTH -> Objects.equals(grid[coord.row() + 1][coord.col()].name(), EMPTY_CELL_SYMBOL)
                            || Objects.equals(grid[coord.row() + 1][coord.col()].name(), GOAL_SYMBOL);
                    case Direction.EAST -> Objects.equals(grid[coord.row()][coord.col() + 1].name(), EMPTY_CELL_SYMBOL)
                            || Objects.equals(grid[coord.row()][coord.col() + 1].name(), GOAL_SYMBOL);
                    case Direction.WEST -> Objects.equals(grid[coord.row()][coord.col() - 1].name(), EMPTY_CELL_SYMBOL)
                            || Objects.equals(grid[coord.row()][coord.col() - 1].name(), GOAL_SYMBOL);
                };
            }
        } catch(IndexOutOfBoundsException index) {
            return false;
        }
        return false;
    }


    /**
     * Checks if the current config is a solution to Astro puzzle
     *
     * @return true if the config is solution, false otherwise.
     */
    @Override
    public boolean isSolution() {
        return Objects.equals(astroCoords, goalCoords);
    }

    /**
     * Retrieves the neighboring configs of the current config
     *
     * @return a collection of neighboring configurations.
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        List<Configuration> neighbors = new ArrayList<>();
        for(Direction dir : Direction.values()){
            for(Piece p : this.totalPieces) {
                if(isValidMove(this.grid, p.coords(), dir)) {
                    try {
                        neighbors.add(new AstroConfig(this, p, dir));
                    } catch(Exception ignored) {
                        //
                    }
                }
            }
        }
        return neighbors;
    }

    /**
     * Checks if another object's hours and current is equal to this config
     *
     * @param other The object to compare against.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if(other instanceof AstroConfig) {
            AstroConfig otherAstro = (AstroConfig) other;
            result = Arrays.deepEquals(grid, otherAstro.grid);
        }
        return result;
    }

    /**
     * Computes the hash code for this config
     *
     * @return The hash code of the clock config
     */
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(grid), astroCoords, goalCoords);
    }

    /**
     * Prints Astro grid
     *
     * @return Return each step of solution
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                sb.append(grid[row][col]).append(" ");
            }
            if(row < rows - 1) {  // removes space from last line
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Returns matrix
     *
     * @return return grid matrix of astro
     */
    public Piece[][] getGrid() {
        return grid;
    }
}