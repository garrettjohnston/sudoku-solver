package com.cs261a.sudoku;

import java.io.PrintStream;
import java.util.BitSet;

/**
 * Created by Garrett on 5/25/2015.
 */
public class Grid {
    private int numUndetermined = 0;
    private byte[][] numUndeterminedInRegion = new byte[3][9];
    private Cell [][] rows = new Cell[9][9];
    private Cell [][] cols = new Cell[9][9];
    private Cell [][] blks = new Cell[9][9];

    /**
     * Constructor takes a 2D array of integers and converts it into cells on a Sudoku board.
     * Makes 3 different representations of cells, by rows, columds, and blocks.
     * @param board - 9x9 array of numbers on a initial Sudoku puzzle
     */
    public Grid(int[][] board) {
        if(board.length != 9) { throw new IllegalArgumentException("Error: Incorrect board size"); }

        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                rows[i][j] = new Cell(i, j, board[i][j]);
                cols[j][i] = rows[i][j];

                int blockNum = i - i%3 + j/3;
                blks[blockNum][3*(i%3) + j%3] = rows[i][j];

                if(board[i][j] == 0) {
                    numUndetermined++;
                    numUndeterminedInRegion[Region.ROW.ordinal()][i]++;
                    numUndeterminedInRegion[Region.COLUMN.ordinal()][j]++;
                    numUndeterminedInRegion[Region.BLOCK.ordinal()][blockNum]++;
                }
            }
        }

        eliminateInitialPossibleValues();
    }

    public int getNumUndetermined() {
        return numUndetermined;
    }

    public int getNumDetermined() {
        return 81 - numUndetermined;
    }

    public int getNumUndeterminedInRegion(Region region, int regionNum) {
        return numUndeterminedInRegion[region.ordinal()][regionNum];
    }

    public Cell[] getRow(int i) {
        return rows[i];
    }

    public Cell[] getCol(int i) {
        return cols[i];
    }

    public Cell[] getBlock(int i) {
        return blks[i];
    }

    public Cell[] getRegion(Region region, int i) {
        switch (region) {
            case ROW:
                return rows[i];
            case COLUMN:
                return cols[i];
            case BLOCK:
                return blks[i];
            default: // Should never reach this, unless new region is secretly added!
                return null;
        }
    }

    public Cell getCell(Region region, int regionNum, int index) {
        return getRegion(region, regionNum)[index];
    }

    /**
     * Takes the initial grid after setup, and removes possible values causing
     */
    private void eliminateInitialPossibleValues() {
        removePossibleValsInSameRegionAsDeterminedVals(rows);
        removePossibleValsInSameRegionAsDeterminedVals(cols);
        removePossibleValsInSameRegionAsDeterminedVals(blks);
    }

    private void removePossibleValsInSameRegionAsDeterminedVals(Cell[][] regions) {
        for (int i = 0; i < regions.length; i++) {
            Cell[] region = regions[i];
            for (Cell cell : region) {
                if (cell.isDetermined())
                    clearValInRegion(regions, i, cell.getValue());
            }
        }
    }

    // For each cell in row[rowNum], remove possibleValue val
    public void clearValInRow(int rowNum, int val) {
        clearValInRegion(rows, rowNum, val);
    }

    // For each cell in cols[colNum], remove possibleValue val
    public void clearValInCol(int colNum, int val) {
        clearValInRegion(cols, colNum, val);
    }

    // For each cell in blks[blockNum], remove possibleValue val
    public void clearValInBlock(int blockNum, int val) {
        clearValInRegion(blks, blockNum, val);
    }

    /**
     * Remove a value from all possibleValue sets in a given region w/regionNumber
     * @param regions - either rows, cols, or blks
     * @param regionNum - which row, col, or block within the region
     * @param val - value to clear from all cells
     */
    private void clearValInRegion(Cell[][] regions, int regionNum, int val) {
        for(int i = 0; i < 9; i++) {
            Cell cell = regions[regionNum][i];
            cell.removePossibleVal(val);
        }
    }

    /**
     * Returns true if all cells determined. False otherwise
     */
    public boolean isDetermined() {
        return numUndetermined == 0;
    }

    /**
     * Returns true if the current sudoku grid is valid. False otherwise
     */
    public boolean isValid() {
        return allDifferent(rows) && allDifferent(cols) && allDifferent(blks);
    }
    /**
     * Returns true if the puzzle is determined and valid. False otherwise
     */
    public boolean isComplete() {
        return isDetermined() && isValid();
    }

    /**
     * Returns true if each array of cells holds distinct values. False if not.
     */
    private boolean allDifferent(Cell[][] regions) {
        BitSet bitset = new BitSet(10);
        for (Cell[] region : regions) {
            for(Cell cell : region) {
                if(cell.isDetermined() && bitset.get(cell.getValue()))
                    return false;
                else
                    bitset.set(cell.getValue());
            }
            bitset.clear();
        }
        return true;
    }

    public void determineCell(Cell cell, int val) {
        // Make sure cell is currently undetermined
        if(cell.getValue() != 0)
            throw new RuntimeException("ERROR: Determining cell: " + cell.toString() + " that is already determined");

        cell.setValue(val);
        cell.clearAllPossibleValues();

        // Update number of undetermined cells left
        this.numUndetermined--;
        this.numUndeterminedInRegion[Region.ROW.ordinal()][cell.getRowNum()]--;
        this.numUndeterminedInRegion[Region.COLUMN.ordinal()][cell.getColNum()]--;
        this.numUndeterminedInRegion[Region.BLOCK.ordinal()][cell.getBlockNum()]--;

        // Remove this value from cells in the same row, column, and block as this cell.
        clearValInRow(cell.getRowNum(), val);
        clearValInCol(cell.getColNum(), val);
        clearValInBlock(cell.getBlockNum(), val);
    }

    /**
     * Returns an array of the undetermined cells in a given region[regionNum]
     */
    public Cell[] undeterminedCellsInRegion(Region region, int regionNum){
        int numUndeterminedCells = this.numUndeterminedInRegion[region.ordinal()][regionNum];
        if(numUndeterminedCells == 0)
                return null;

        // Create appropriate sized array to put undetermined cells in
        Cell[] undeterminedCells = new Cell[numUndeterminedCells];
        int i = 0;
        for(Cell cell : getRegion(region, regionNum)) {
            if(!cell.isDetermined()) {
                undeterminedCells[i] = cell;
                i++;
            }
        }
        return undeterminedCells;
    }

    public BitSet getDeterminedValsInRegion(Region region, int regionNum) {
        return getDeterminedValsInRegion(getRegion(region, regionNum));
    }

    public static BitSet getDeterminedValsInRegion(Cell[] region) {
        BitSet bitSet = new BitSet(10);
        for(Cell cell : region) {
            if(cell.isDetermined())
                bitSet.set(cell.getValue());
        }
        return bitSet;
    }

    /**
     * Print the Sudoku board with only determined cells displayed
     */
    public void printCurrentState() {
        for(Cell [] row : rows) {
            for (Cell cell : row) {
                System.out.print(cell.getValue() + " ");
            }
            System.out.print("\n");
        }
    }

    /**
     * By default, print state to System.out
     */
    public void printVerboseState() {
        printVerboseState(System.out);
    }

    /**
     * Print the current Sudoku board to designated print stream, including all possible values for undetermined cells
     */
    public void printVerboseState(PrintStream printStream) {
        System.setOut(printStream);
        System.out.println("Undetermined cells: " + numUndetermined);
        for (int rowNum = 0; rowNum < rows.length; rowNum++) {
            Cell[] row = rows[rowNum];
            StringBuilder line1 = new StringBuilder("[");
            StringBuilder line2 = new StringBuilder("[");
            StringBuilder line3 = new StringBuilder("[");
            for (int colNum = 0; colNum < rows.length; colNum++) {
                Cell cell = row[colNum];
                for (int i = 1; i <= 3; i++) {
                    if (cell.isPotentialValue(i)) {
                        line1.append(i);
                    } else {
                        line1.append(" ");
                    }
                }
                for (int i = 4; i <= 6; i++) {
                    if (cell.isPotentialValue(i)) {
                        line2.append(i);
                    } else if(cell.isDetermined()) {
                        line2.append(" ").append(cell.getValue()).append(" ");
                        break;
                    } else {
                        line2.append(" ");
                    }
                }
                for (int i = 7; i <= 9; i++) {
                    if (cell.isPotentialValue(i)) {
                        line3.append(i);
                    } else {
                        line3.append(" ");
                    }
                }

                if (colNum == 2 || colNum == 5) {
                    line1.append("] [");
                    line2.append("] [");
                    line3.append("] [");
                } else if (colNum != 8) {
                    line1.append("|");
                    line2.append("|");
                    line3.append("|");
                } else if (colNum == 8) {
                    line1.append("]");
                    line2.append("]");
                    line3.append("]");
                }
            }

            System.out.println(line1.toString());
            System.out.println(line2.toString());
            System.out.println(line3.toString());
            if (rowNum == 2 || rowNum == 5) {
                System.out.println("=========================================");
            } else if (rowNum != 8) {
                System.out.println("-----------------------------------------");
            }

        }
    }

}
