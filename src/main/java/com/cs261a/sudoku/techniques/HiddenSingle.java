package com.cs261a.sudoku.techniques;

import com.cs261a.sudoku.Cell;
import com.cs261a.sudoku.Grid;
import com.cs261a.sudoku.Region;

import java.util.BitSet;

/**
 * Created by Garrett on 5/25/2015.
 */
public class HiddenSingle extends Technique {

    // The undetermined cell that is a HiddenSingle
    private Cell undeterminedCell;

    // The HiddenSingle value
    private int hiddenValue;

    // The region type in which we are first able to apply HiddenSingle
    private Region firstFoundRegion;

    // The number of the region (i.e. 3rd row) in which we find our first application of the technique
    private int firstFoundRegionNum;

    @Override
    public boolean isApplicable(Grid grid) {
        // Find the first application of this technique. If there is none, return false.
        if(!findFirstApplication(grid)) {
            return false;
        }

        // Resume the search where we left off, to count the rest of the applications of this technique
        int searchRegionNum = firstFoundRegionNum+1;

        for (int i = firstFoundRegion.ordinal(); i < 3; i++) {
            Region region = Region.values()[i];
            for (int j = searchRegionNum; j < 9; j++) {
                BitSet hiddenSingles = findHiddenSingle(grid.getRegion(region, j));
                numApplications += hiddenSingles.cardinality();
            }
            searchRegionNum = 0;
        }
        return true;
    }

    /**
     * Searches for the first possible application of technique.
     * Sets attributes so we can continue searching, in order to count the total number of applications
     * @return True if we find an application of technique, false if not.
     */
    private boolean findFirstApplication(Grid grid) {
        for (Region region : Region.values()) {
            for (int regionNum = 0; regionNum < 9; regionNum++) {
                BitSet hiddenSingles = findHiddenSingle(grid.getRegion(region, regionNum));

                if(!hiddenSingles.isEmpty()) {
                    hiddenValue = hiddenSingles.nextSetBit(1);
                    numApplications += hiddenSingles.cardinality();
                    firstFoundRegion = region;
                    firstFoundRegionNum = regionNum;
                    return true;
                }
            }
        }
        // If no fitting cells are found, return false
        return false;
    }

    private BitSet findHiddenSingle(Cell[] region) {
        BitSet foundOnce  = Grid.getDeterminedValsInRegion(region);
        BitSet foundTwice = Grid.getDeterminedValsInRegion(region);

        // Look for hidden single within region
        for (Cell cell : region) {
            if(!cell.isDetermined()) {
                BitSet cloneFoundOnce = (BitSet) foundOnce.clone();
                foundOnce.or(cell.getBits());

                cloneFoundOnce.and(cell.getBits());
                foundTwice.or(cloneFoundOnce);
            }
        }

        foundTwice.flip(1,10);
        return foundTwice;
    }

    @Override
    public void applyMe(Grid grid) {
        for(Cell cell : grid.getRegion(firstFoundRegion, firstFoundRegionNum)) {
            if(cell.isPotentialValue(hiddenValue)) {
                undeterminedCell = cell;
                grid.determineCell(undeterminedCell, hiddenValue);
                return;
            }
        }
        throw new RuntimeException("ERROR: Could not find cell in region " + firstFoundRegion + " "
                + firstFoundRegionNum + " to apply HiddenSingle technique with value= " + hiddenValue);

    }

    @Override
    public String applyResult() {
        StringBuilder str = new StringBuilder();
        str.append(getClass().getSimpleName()).append(": Determining cell [").append(undeterminedCell.toString());
        str.append("] with val=").append(hiddenValue).append(" in ").append(firstFoundRegion).append(" ").append(firstFoundRegionNum);
        return str.toString();
    }
}
