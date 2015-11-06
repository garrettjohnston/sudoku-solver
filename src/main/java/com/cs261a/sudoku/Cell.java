package com.cs261a.sudoku;

import java.util.BitSet;

/**
 * @author Garrett Johnston
 * @date 05/25/15
 * Cell class holds the state of a single cell in a Sudoku grid.
 * The x, y coordinates in the grid, its value (if determined) and its possible values.
 */
public class Cell {
    private final int x;
    private final int y;
    private int value;
    private BitSet possibleValues = new BitSet(10);

    /**
     * Constructor creating a cell at given x, y position with given value.
     * If value = 0, then it is an empty cell, so we set the possible values all to true initially.
     * @param x - x coordinate in grid (0-8), starting in upper left
     * @param y - y coordinate in grid (0-8), starting in upper left
     * @param value - if 0, the cell is undecided. If between 1-9, it is decided.
     */
    public Cell(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;

        // If cell not determined, set all possible values (1-9) to true.
        if(value == 0) {
            possibleValues.set(1,10);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRowNum() { return x; }

    public int getColNum() { return y; }

    public int getBlockNum() { return x - x%3 + y/3; }

    public int getValue() {
        return value;
    }

    protected void setValue(int val) { this.value = val; }

    public BitSet getBits() {
        return possibleValues;
    }

    public void addPossibleValue(int val) {
        possibleValues.set(val);
    }

    public void removePossibleVal(int val) {
        possibleValues.clear(val);
    }

    public void clearAllPossibleValues() { possibleValues.clear(); }

    public int getNumPossibleValues() { return possibleValues.cardinality(); }

    public int getOnlyPossibleValue() { return possibleValues.nextSetBit(1); }

    /**
     * Checks if a certain number is a potential value in this cell
     */
    public boolean isPotentialValue(int val) {
        return possibleValues.get(val);
    }

    /**
     * Checks if a value is bound to this cell
     */
    public boolean isDetermined() {
        return value != 0;
    }


    @Override
    public String toString() {
        return String.format("x = %d, y = %d, value = %d, bitset = %s", x, y, value, possibleValues.toString());
    }

}
