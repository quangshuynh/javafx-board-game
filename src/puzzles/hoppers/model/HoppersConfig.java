package puzzles.hoppers.model;

import puzzles.common.Direction;
import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

/**
 * The configuration class for the board game Hoppers, used to work with common BFS solver
 *
 * @author Kai Fan
 */
public class HoppersConfig implements Configuration{
    private char[][] board; //the board of the game
    public static int row; //the number of row start from 1
    public static int col; //the number of col start from 1
    private Integer id; //the id of the configuration

    /**
     * the main constructor of the HoppersConfig
     * @param filename - the file used
     * @throws IOException - IOE
     */
    public HoppersConfig(String filename) throws IOException {
        id = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String[] dims = reader.readLine().split("\\s+");
            row = Integer.parseInt(dims[0]);
            col = Integer.parseInt(dims[1]);
            board = new char[row][col];
            //writing into the 2D array
            for(int i = 0; i < row; i++){
                String[] aRow = reader.readLine().split("\\s+");
                for(int j = 0; j < col; j++){
                    board[i][j] = aRow[j].charAt(0);
                }
            }
        }
    }

    /**
     * The copy constructor used to easily create another config
     *
     * @param other - the other HoppersConfig passed into here to make copy constructors
     */
    private HoppersConfig(HoppersConfig other, char[][]board){
        this.id = other.id ++;
        this.board = board;
    }

    /**
     * It is solution when there's only red frog left
     *
     * @return true if only red frog is left
     */
    @Override
    public boolean isSolution() {
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                if(board[i][j] == 'G'){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * the get neighbors methods used to generate new configurations for the BFS solver
     *
     * @return a collection of configurations
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        HashSet<Configuration> result = new HashSet<>();

        //go through the frogs and move them, each move generate a new config
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                if(i % 2 == 0 && board[i][j] == 'G'){ //if even row and is a green frog
                    //doing vertical and horizontal moves
                    if(isMoveValid(i - 4, j)){
                        result.add(new HoppersConfig(this, move(i, j, i - 4, j, true)));
                    }else if(isMoveValid(i + 4, j)){
                        result.add(new HoppersConfig(this, move(i, j, i + 4, j, true)));
                    }else if(isMoveValid(i, j - 4)){
                        result.add(new HoppersConfig(this, move(i, j, i, j - 4, true)));
                    }else if(isMoveValid(i, j + 4)){
                        result.add(new HoppersConfig(this, move(i, j, i, j + 4, true)));
                    }
                    //doing diagonal moves
                }else if(board[i][j] == 'G'){
                    if(isMoveValid(i - 2, j - 2)){
                        result.add(new HoppersConfig(this, move(i, j, i - 2, j - 2, false)));
                    }else if(isMoveValid(i - 2, j + 2)){
                        result.add(new HoppersConfig(this, move(i, j, i - 2, j + 2, false)));
                    }else if(isMoveValid(i + 2, j - 2)){
                        result.add(new HoppersConfig(this, move(i, j, i + 2, j - 2, false)));
                    }else if(isMoveValid(i + 2, j + 2)){
                        result.add(new HoppersConfig(this, move(i, j, i + 2, j + 2, false)));
                    }
                }
            }
        }
        return result;
    }

    private char[][] move(int originalRow, int originalCol, int newRow, int newCol, boolean longJump){
        char[][] copyBoard = new char[row][col]; //creating a copy of the board from this config to move frogs
        System.arraycopy(board, 0, copyBoard, 0, row);
        copyBoard[newRow][newCol] = 'G'; //moving the green frog

        //process deleting
        int deleteFrogRow = 0;
        int deleteFrogCol = 0;
        if(longJump){ //deleting even row
            if(newRow > originalRow && newCol == originalCol){
                deleteFrogRow = originalRow + 2;
                deleteFrogCol = originalCol;
            }else if(newRow < originalRow && newCol == originalCol){
                deleteFrogRow = originalRow - 2;
                deleteFrogCol = originalCol;
            }else if(newCol > originalCol && newRow == originalRow){
                deleteFrogCol = originalCol + 2;
                deleteFrogRow = originalRow;
            }else if(newCol < originalCol && newRow == originalRow){
                deleteFrogCol = originalCol - 2;
                deleteFrogRow = originalRow;
            }
        }else{
            if(newRow > originalRow && newCol > originalCol){
                deleteFrogRow = originalRow + 1;
                deleteFrogCol = originalCol + 1;
            }else if(newRow > originalRow && newCol < originalCol){
                deleteFrogRow = originalRow + 1;
                deleteFrogCol = originalCol - 1;
            }else if(newRow < originalRow && newCol > originalCol){
                deleteFrogRow = originalRow - 1;
                deleteFrogCol = originalCol + 1;
            }else if(newRow < originalRow && newCol < originalCol){
                deleteFrogRow = originalRow - 1;
                deleteFrogCol = originalCol - 1;
            }
        }
        if(copyBoard[deleteFrogRow][deleteFrogCol] == 'G' || copyBoard[deleteFrogRow][deleteFrogCol] == 'R'
        || copyBoard[deleteFrogRow][deleteFrogCol] == '.'){
            copyBoard[deleteFrogRow][deleteFrogCol] = '.';
        }else{
            copyBoard[deleteFrogRow][deleteFrogCol] = '*';
        }
        return copyBoard;
    }

    /**
     * A method used in get neighbors to see if a move is valid
     *
     * @param newRow - the target row to move to
     * @param newCol - the target col to move to
     * @return if that targeted row, col is valid
     */
    private boolean isMoveValid(int newRow, int newCol){
        boolean result = true;
        if(newRow > row - 1){
            result = false;
        }else if(newRow < 0){
            result = false;
        }else if(newCol > col - 1){
            result = false;
        }else if(newCol < 0){
            result = false;
        }else if(board[newRow][newCol] == 'G' || board[newRow][newCol] == 'R'){
            result = false;
        }
        return result;
    }

    /**
     * the equal method compares the id of each config
     *
     * @param other - the HoppersConfig that needs to be compared
     * @return true if two config are the same
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if(other instanceof HoppersConfig){
            HoppersConfig otherConfig = (HoppersConfig) other;
            result = id.equals(otherConfig.id);
        }
        return result;
    }

    /**
     * returns the hashcode of the id
     *
     * @return hash code of config id
     */
    @Override
    public int hashCode() { return id.hashCode(); }

    /**
     * the to String method for the Hoppers configuration. It prints out the board
     *
     * @return the string representation of the board
     */
    @Override
    public String toString() {
        String result = "";
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                result += board[i][j] + " ";
            }
            result = result.substring(0, result.length() - 1);
            result += "\n";
        }
        result = result.substring(0, result.length() - 1);
        return result;
    }
}
