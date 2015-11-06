package com.cs261a.sudoku.techniques;

import com.cs261a.sudoku.Cell;
import com.cs261a.sudoku.Grid;
import com.cs261a.sudoku.Region;

import java.util.BitSet;

/**
 * Created by Garrett on 5/25/2015.
 */
public class NakedSingle extends Technique {

    // The undetermined cell that is a NakedSingle
    private Cell undeterminedCell;

    // The value of the NakedSingle
    private int nakedValue;

    @Override
    public boolean isApplicable(Grid grid) {
        boolean foundFirst = false;
        // Only need to cycle through rows, as columns and blocks would be redundant
        for(int rowNum = 0; rowNum < 9; rowNum++) {
            for(Cell cell : grid.getRow(rowNum)) {
                if(cell.getNumPossibleValues() == 1) {
                    numApplications++;
                    if(!foundFirst) {
                        foundFirst = true;
                        undeterminedCell = cell;
                        nakedValue = cell.getOnlyPossibleValue();
                    }
                }
            }
        }

        return foundFirst;
    }

    @Override
    public void applyMe(Grid grid) {
        if(undeterminedCell == null)
            throw new RuntimeException("ERROR: NakedSingle cell = null");

        grid.determineCell(undeterminedCell, nakedValue);
    }

    @Override
    public String applyResult() {
        StringBuilder str = new StringBuilder();
        str.append(getClass().getSimpleName()).append(": Determining cell [").append(undeterminedCell.toString());
        str.append("] with val=").append(nakedValue);
        return str.toString();
    }
}
