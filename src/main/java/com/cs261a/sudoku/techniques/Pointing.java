package com.cs261a.sudoku.techniques;

import com.cs261a.sudoku.Cell;
import com.cs261a.sudoku.Grid;
import com.cs261a.sudoku.Region;

import java.util.BitSet;

/**
 * Created by Garrett on 6/6/2015.
 */
public class Pointing extends Technique {
    // The type of region (row or column) the pointing is happening in
    private Region regionType;

    // The region number for which the pointing is occuring
    private int regionNum;

    // The value we are pointing
    int pointValue;

    // The third of the region which contains the pointers (need to remove val from OTHER 2/3 of region)
    int thirdThatIsPointing;

    @Override
    public boolean isApplicable(Grid grid) {
        for(int blockNum = 0; blockNum < 9; blockNum++) {
            BitSet bitSet = grid.getDeterminedValsInRegion(Region.BLOCK, blockNum);
            Cell[] block = grid.getBlock(blockNum);
            bitSet.flip(1,10);
            int num = bitSet.nextSetBit(1);

            // Go through all undetermined vals in the block
            while(num != -1) {
                // Check each Row
                for (int rowNum = 0; rowNum < 3; rowNum++) {
                    if(valueConfinedToRowInBlock(block, rowNum, num)) {
                        if(valueExistsInRestOfRow(grid, blockNum, rowNum, num)) {
                            regionType = Region.ROW;
                            regionNum = getRowNum(blockNum, rowNum);
                            pointValue = num;
                            thirdThatIsPointing = blockNum % 3;
                            return true;
                        }
                    }
                }

                // Check each Column
                for (int colNum = 0; colNum < 3; colNum++) {
                    if(valueConfinedToColInBlock(block, colNum, num)) {
                        if(valueExistsInRestOfCol(grid, blockNum, colNum, num)) {
                            regionType = Region.COLUMN;
                            regionNum = getColNum(blockNum, colNum);
                            pointValue = num;
                            thirdThatIsPointing = blockNum / 3;
                            return true;
                        }
                    }
                }

                num = bitSet.nextSetBit(num + 1);
            }

        }
        return false;
    }

    /**
     * @param blockNum - the block in the grid (0 - 8)
     * @param innerRowNum - the row within the block (0, 1, or 2)
     * @return the actual row number corresponding to the inner row of the block
     */
    private int getRowNum(int blockNum, int innerRowNum) {
        return blockNum - blockNum%3 + innerRowNum;
    }

    private void removeValFromOtherTwoThirdsOfRow(Cell[] row, int thirdToKeep, int val) {
        for (int i = 0; i < 9; i++) {
            if(i/3 != thirdToKeep) {
                row[i].removePossibleVal(val);
            }
        }
    }

    private boolean valueExistsInRestOfRow(Grid grid, int blockNum, int rowNum, int val) {
        Cell[] row = grid.getRow(getRowNum(blockNum, rowNum));
        BitSet bitSet = new BitSet(10);
        for (int i = 0; i < 9; i++) {
            if(i/3 != blockNum%3) {
                bitSet.or(row[i].getBits());
            }
        }
        return bitSet.get(val);
    }

    private boolean valueConfinedToRowInBlock(Cell[] block, int rowNum, int val) {
        Cell c1 = block[rowNum*3];
        Cell c2 = block[rowNum*3 + 1];
        Cell c3 = block[rowNum*3 + 2];

        BitSet combo12 = (BitSet)c1.getBits().clone();
        combo12.and(c2.getBits());
        BitSet combo13 = (BitSet)c1.getBits().clone();
        combo13.and(c3.getBits());
        BitSet combo23 = (BitSet)c2.getBits().clone();
        combo23.and(c3.getBits());
        BitSet combo123 = (BitSet)c1.getBits().clone();
        combo123.and(c2.getBits());
        combo123.and(c3.getBits());

        // Make sure one combination of two or more cells contains the number
        if(! (combo123.get(val) || combo12.get(val) ||combo13.get(val) ||combo23.get(val)))
            return false;

        BitSet restOfBits = new BitSet(10);
        // We know val is in bitSet and row. Now check that it is NOT in other rows within the block.
        for(int i = 0; i < 3; i++) {
            if(i != rowNum) {
                restOfBits.or(block[i*3].getBits());
                restOfBits.or(block[i*3+1].getBits());
                restOfBits.or(block[i*3+2].getBits());
            }
        }

        return !restOfBits.get(val);
    }

    private boolean valueConfinedToColInBlock(Cell[] block, int colNum, int val) {
        Cell c1 = block[colNum];
        Cell c2 = block[colNum + 3];
        Cell c3 = block[colNum + 6];

        BitSet combo12 = (BitSet)c1.getBits().clone();
        combo12.and(c2.getBits());
        BitSet combo13 = (BitSet)c1.getBits().clone();
        combo13.and(c2.getBits());
        BitSet combo23 = (BitSet)c2.getBits().clone();
        combo23.and(c3.getBits());
        BitSet combo123 = (BitSet)c1.getBits().clone();
        combo123.and(c2.getBits());
        combo123.and(c3.getBits());

        // Make sure one combination of two or more cells contains the number
        if(! (combo123.get(val) || combo12.get(val) ||combo13.get(val) ||combo23.get(val)))
            return false;

        BitSet restOfBits = new BitSet(10);
        // We know val is in bitSet and col. Now check that it is NOT in other cols within the block.
        for(int i = 0; i < 3; i++) {
            if(i != colNum) {
                restOfBits.or(block[i].getBits());
                restOfBits.or(block[i + 3].getBits());
                restOfBits.or(block[i + 6].getBits());
            }
        }

        return !restOfBits.get(val);
    }

    private boolean valueExistsInRestOfCol(Grid grid, int blockNum, int colNum, int val) {
        Cell[] column = grid.getCol(getColNum(blockNum, colNum));
        BitSet bitSet = new BitSet(10);
        for (int i = 0; i < 9; i++) {
            if(i/3 != blockNum/3) {
                bitSet.or(column[i].getBits());
            }
        }
        return bitSet.get(val);
    }

    /**
     * @param blockNum - the block in the grid (0 - 8)
     * @param innerColNum - the col within the block (0, 1, or 2)
     * @return the actual column number corresponding to the inner column of the block
     */
    private int getColNum(int blockNum, int innerColNum) {
        return 3*(blockNum%3) + innerColNum;
    }

    @Override
    public void applyMe(Grid grid) {
        Cell[] region = grid.getRegion(this.regionType, this.regionNum);
        for (int i = 0; i < 9; i++) {
            if(i/3 != this.thirdThatIsPointing)
                region[i].removePossibleVal(this.pointValue);
        }
    }

    @Override
    public String applyResult() {
        StringBuilder str = new StringBuilder();
        str.append(getClass().getSimpleName()).append(": Removing val: ").append(pointValue);
        str.append(" from ").append((regionType == Region.COLUMN) ? "Column": "Row");
        str.append(" ").append(this.regionNum).append(" except part ");
        str.append(thirdThatIsPointing).append("/3\n");
        return str.toString();
    }
}
