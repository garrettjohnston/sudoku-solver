package com.cs261a.sudoku.techniques;

import com.cs261a.sudoku.Cell;
import com.cs261a.sudoku.Grid;
import com.cs261a.sudoku.Region;

/**
 * Created by Garrett on 6/1/2015.
 */
public class FullHouse extends Technique {
    // The undetermined cell we find which has 8 determined siblings in either its row, column, or block.
    private Cell undeterminedCell;

    // The region in which we find our first cell to apply FullHouse to.
    private Region firstFoundRegion;

    // The number of the region (i.e. 3rd row) in which we find our first cell to apply FullHouse to.
    private int firstFoundRegionNum;


    /**
     * Finds all applications of this technique in the given grid
     * Returns true if technique applies at least once, false if technique does not apply.
     * Sets up 'undeterminedCell' attribute to be set in 'applyMe'
     */
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
                // Check if only 1 cell is undetermined
                if (grid.getNumUndeterminedInRegion(region, j) == 1)
                    numApplications++;
            }
            searchRegionNum = 0;
        }
        return true;
    }

    /**
     * Searches for the first possible application of FullHouse.
     * Sets attributes so we can continue searching, in order to count the total number of applications
     * @return True if we find an application of FullHouse, false if not.
     */
    private boolean findFirstApplication(Grid grid) {
        for (Region region : Region.values()) {
            for (int i = 0; i < 9; i++) {
                // Check if only 1 cell is undetermined in region[i]
                if (grid.getNumUndeterminedInRegion(region, i) == 1) {
                    // Set attributes when we find an applicable cell
                    numApplications++;
                    firstFoundRegion = region;
                    firstFoundRegionNum = i;
                    // get the array holding the one undetermined cell
                    Cell[] undeterminedCells = grid.undeterminedCellsInRegion(region, i);
                    undeterminedCell = undeterminedCells[0];
                    return true;
                }
            }
        }
        // If no fitting cells are found, return false
        return false;
    }

    @Override
    public void applyMe(Grid grid) {
        if(undeterminedCell == null)
            throw new RuntimeException("ERROR: FullHouse cell = null");

        if(undeterminedCell.getNumPossibleValues() != 1)
            throw new RuntimeException("ERROR: FullHouse cell: " + undeterminedCell.toString() + " more than 1 possible value");

        grid.determineCell(undeterminedCell, undeterminedCell.getOnlyPossibleValue());
    }

    public String applyResult() {
        StringBuilder str = new StringBuilder();
        str.append(getClass().getSimpleName()).append(": Determining cell [").append(undeterminedCell.toString());
        str.append("] with val=").append(undeterminedCell.getValue());
        str.append(" in ").append(firstFoundRegion).append(" ").append(firstFoundRegionNum);
        return str.toString();
    }
}
