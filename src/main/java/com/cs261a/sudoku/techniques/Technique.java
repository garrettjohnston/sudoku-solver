package com.cs261a.sudoku.techniques;

import com.cs261a.sudoku.Grid;

/**
 * Abstract technique class providing interface to main methods that must be performed by all techniques
 */
public abstract class Technique {

    int numApplications = 0;

    public int getNumApplications() {
        return numApplications;
    }

    /**
     * Returns true if this technique is applicable, false otherwise
     * If true, also finds all possible applications of technique and sets the attribute numApplications.
     */
    public abstract boolean isApplicable(Grid grid);

    /**
     * Each technique overrides this, applying their own technique to the grid.
     * NOTE: This method is hidden from other classes. Must call standard "apply" method.
     */
    protected abstract void applyMe(Grid grid);

    /**
     * Applies a technique to the grid.
     * Throws BadTechniqueException if the technique results in an invalid game board.
     * @throws BadTechniqueException
     */
    public final void apply(Grid grid) throws BadTechniqueException {
        applyMe(grid);
        System.out.println(applyResult());
        if(!grid.isValid())
            throw new BadTechniqueException(this, grid);
    }

    /**
     * Exception thrown if a technique results in an invalid game board.
     */
    public class BadTechniqueException extends Throwable {
        public BadTechniqueException(Technique technique, Grid grid) {
            System.err.println("Technique " + technique.getClass().getName() + " led to invalid game board");
            grid.printVerboseState(System.err);
        }
    }

    public abstract String applyResult();
}
