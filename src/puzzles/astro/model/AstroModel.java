package puzzles.astro.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * The model for the Astro puzzle
 *
 * @author CS RIT
 * @author Quang Huynh (qth9368)
 */
public class AstroModel {
    private String filename;
    /** the collection of observers of this model */
    private final List<Observer<AstroModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private AstroConfig currentConfig;

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<AstroModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void notifyObservers(String msg) {
        for (var observer : observers) {
            observer.update(this, msg);
        }
    }

    /**
     * Creates a new game
     * @param filename name of Astro puzzle
     */
    public AstroModel(String filename) throws IOException {
        this.filename = filename;
        currentConfig = new AstroConfig(this.filename);
    }

    /**
     * Get rows
     *
     * @return rows of grid
     */
    public int getRow() {
        return currentConfig.rows;
    }

    /**
     * Get columns
     *
     * @return columns of grid
     */
    public int getCol() {
        return currentConfig.cols;
    }

    /**
     * returns current config
     *
     * @return current config
     */
    public AstroConfig getCurrentConfig() {
        return currentConfig;
    }

    /**
     * Loads a new puzzle configuration from a file.
     * @param filename The file name to load the puzzle from.
     */
    public void loadPuzzle(String filename) {
        try {
            this.filename = filename;
            this.currentConfig = new AstroConfig(filename);
            notifyObservers("Loaded: " + filename);
        } catch(IOException e) {
            notifyObservers("Failed to load puzzle: " + filename);
        }
    }

    public void getHint() {
        try {
            Solver solver = new Solver();
            List<Configuration> solution = solver.solve(currentConfig);
            // todo
        } catch(Exception e) {
            notifyObservers("Error getting hint");
        }
    }

    /**
     * Resets the puzzle to the initial configuration based on the current file.
     */
    public void resetPuzzle() {
        try {
            this.currentConfig = new AstroConfig(this.filename);
            notifyObservers("Puzzle reset");
        } catch(IOException e) {
            notifyObservers("Failed to reset puzzle");
        }
    }

    /**
     * Get content of row and column of a cell
     *
     * @param row row index
     * @param col col index
     * @return the cell value
     */
    public String getContent(int row, int col) {
        return currentConfig.getGrid()[row][col];
    }
}
