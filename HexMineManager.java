import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Minesweeper class that manages the bookkeeping'
 * for HexMineGUI;
 * @author Jordan Newlander
 * CS251
 */
public class HexMineManager {
    static int rows;
    static int cols;
    static int mineNum;

    static final String UNCLICKED = "c";
    static final String EMPTY = "";
    static final String FLAG = "F";
    static final String MINE = "M";

    static String[][] board;
    static int[][] mines;

    /**
     * initiates HexMineManager object
     * initializes board
     * @param rowNum
     * @param colNum
     * @param mines
     */
    public HexMineManager(int rowNum, int colNum, int mines) {
        this.rows = rowNum;
        this.cols = colNum;
        this.mineNum = mines;
    }

    /**
     * initiates the gameboard,
     * calls initMines
     */
    public static void initGame() {
        board = new String[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++){
                board[r][c] = UNCLICKED;
            }
        }
        initMines();
    }

    /**
     * updates row, col and mine number after
     * button input form gui
     * reinitializes game
     * @param newR
     * @param newC
     * @param newM
     */
    public static void updateBoardSize(int newR, int newC, int newM) {
        rows = newR;
        cols = newC;
        mineNum = newM;
        initGame();
    }

    /**
     * flags mine at (row, col)
     * if unclicked
     * @param row
     * @param col
     */
    public static void flagMine(int row, int col) {
        if (board[row][col].equals(UNCLICKED)){
            board[row][col] = FLAG;
        }
    }

    /**
     * initializes mines using random object
     */
    public static void initMines() {
        int[] rMines = new int[mineNum];
        int[] cMines = new int[mineNum];

        for (int i = 0; i < mineNum; i++){
            int r, c;
            boolean duplicate;
            //check for duplicate mine before adding
            do {
                duplicate = false;
                r = (int)(Math.random() * rows);
                c = (int)(Math.random() * cols);
                for (int j = 0; j < i; j++) {
                    if (rMines[j] == r && cMines[j] == c){
                        duplicate = true;
                        break; //ends for loop
                    }
                }
            } while (duplicate);
            rMines[i] = r;
            cMines[i] = c;
        }

        mines = new int[][]{rMines, cMines};
    }

    /**
     * shows the mines after gameover
     * (mine has been clicked)
     */
    public static void showMines() {
        for (int m = 0; m < mineNum; m++) {
            for (int i = 0; i < rows; i++){
                for (int j = 0; j < cols; j++) {
                    if (mines[0][m] == i &&
                            mines[1][m] == j) board[i][j] = MINE;
                }
            }
        }
    }

    /**
     * sees if next move
     * and if move is legal
     * clears field if it is
     * shows mines if not
     * @param row
     * @param col
     * @return
     */
    public static boolean isLegal(int row, int col) {
        if (!hasNextMove() || !board[row][col].equals(UNCLICKED)) {
            return false;
        }

        for (int i = 0; i < mineNum; i++) {
            if (mines[0][i] == row && mines[1][i] == col) {
                showMines();
                return false;
            }
        }
        return true;
    }

    /**
     * sees if next move
     * @return
     */
    public static boolean hasNextMove() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].equals(UNCLICKED)) return true;
            }
        }
        return false;
    }

    /**
     * finds number of mines cell[row][col]
     * is adjacent to
     * @param row
     * @param col
     * @return how many mines cell is adjacent to
     */
    public static int findMineNum(int row, int col) {
        int mineNums = 0;
        int[][] evenNeighbors = {
                {0, -1}, {0, 1},
                {-1, -1}, {-1, 0},
                {1, -1}, {1, 0}
        };

        int[][] oddNeighbors = {
                {0, -1}, {0, 1},
                {-1, 0}, {-1, 1},
                {1, 0}, {1, 1}
        };

        int[][] neighbors = (row % 2 == 0) ? evenNeighbors : oddNeighbors;

        for (int m = 0; m < mineNum; m++) {
            for (int[] n : neighbors){
                int r = row + n[0];
                int c = col + n[1];

                if (r < 0 || r >= rows || c < 0 || c >= cols) {
                    continue;
                }

                if (mines[0][m] == r && mines[1][m] == c) mineNums++;
            }
        }
        return mineNums;
    }

    /**
     * creates an arraylist of points cleared by fieldclear
     * @param row
     * @param col
     * @return
     */
    public static List<Point> clearedList(int row, int col){
        List<Point> clearedList = new ArrayList<>();
        fieldClear(row, col, clearedList);
        return clearedList;
    }

    /**
     * clears the field while there are still
     * "empty" cells left
     * @param row
     * @param col
     */
    public static void fieldClear(int row, int col, List<Point> cleared) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return;
        }

        if (!board[row][col].equals(UNCLICKED)) return;

        for (int m = 0; m < mineNum; m++) {
            if (mines[0][m] == row && mines[1][m] == col) return;
        }

        int adjacent = findMineNum(row, col);

        if (adjacent > 0){
            board[row][col] = "" + adjacent;
            cleared.add(new Point(row, col));
            return;
        }

        int[][] evenNeighbors = {
                {0, -1}, {0, 1},
                {-1, -1}, {-1, 0},
                {1, -1}, {1, 0}
        };

        int[][] oddNeighbors = {
                {0, -1}, {0, 1},
                {-1, 0}, {-1, 1},
                {1, 0}, {1, 1}
        };

        int[][] neighbors = (row % 2 == 0) ? evenNeighbors : oddNeighbors;

        board[row][col] = EMPTY;
        cleared.add(new Point(row, col));

        for (int[] n : neighbors) {
            int r = row + n[0];
            int c = col + n[1];
            if (r < 0 || r >= rows || c < 0 || c >= cols) {
                continue;
            }
            fieldClear(r, c, cleared);
        }
    }

    /**
     * empty main method
     * (code would not work without??)
     * @param args
     */
    public static void main(String[] args){
    }
}