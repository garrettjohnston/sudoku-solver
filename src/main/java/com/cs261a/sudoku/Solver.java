package com.cs261a.sudoku;

import com.cs261a.sudoku.techniques.Technique;
import com.cs261a.sudoku.techniques.TechniqueFactory;
import com.cs261a.sudoku.techniques.TechniqueType;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by Garrett on 5/25/2015.
 */
public class Solver {

    public void solvePuzzle(int[][] init) throws InsufficientTechniquesException, Technique.BadTechniqueException {
        Grid grid = new Grid(init);
        ArrayList<Technique> steps = new ArrayList<Technique>();
        steps.ensureCapacity(64);

        while(!grid.isComplete()) {
            TechniqueType techniqueType = TechniqueType.getFirst();
            Technique technique = TechniqueFactory.getTechnique(techniqueType);

            // Go through techniques in order until we find one we can apply to our current game state.
            // Throw exception if no techniques can be applied.
            while(!technique.isApplicable(grid)) {
                techniqueType = techniqueType.next();
                if (techniqueType == null) {
                    throw new InsufficientTechniquesException(grid);
                } else {
                    technique = TechniqueFactory.getTechnique(techniqueType);
                }
            }

            // Apply the first applicable technique to the game board, adding it to our steps list.
            technique.apply(grid);
            steps.add(technique);
        }
        System.out.println("Puzzle solved!");
        grid.printCurrentState();

        for (int i = 0; i < steps.size(); i++) {
            Technique technique = steps.get(i);
            System.out.println("step " + i + " technique " + technique.getClass().getSimpleName()
                                + " " + technique.getNumApplications());
        }
    }

    public class InsufficientTechniquesException extends Exception {
        public InsufficientTechniquesException(Grid grid) {
            System.err.println("Could not apply any techniques to this game state");
            grid.printVerboseState(System.err);
        }
    }

    public static void main(String args[]) {

        /*Grid grid = new Grid(easyBoardNearlySolved);
        grid.printVerboseState();
        System.out.println("grid is valid:" + grid.isValid());
        grid.determineCell(grid.getRow(7)[4], 4);
        System.out.println("grid is valid:" + grid.isValid());
        grid.printVerboseState();*/

        Solver solver = new Solver();
        try {
            solver.solvePuzzle(hard2);
        } catch (InsufficientTechniquesException | Technique.BadTechniqueException e) {
            e.printStackTrace();
        }
    }

    static int[][] hardBoard = {
            {0,4,0,2,0,8,0,0,6},
            {0,0,0,0,1,0,0,9,0},
            {0,1,0,7,5,0,0,0,4},
            {0,7,9,1,0,2,0,0,0},
            {0,0,0,0,7,0,0,0,5},
            {0,0,0,8,0,0,0,2,0},
            {0,8,2,0,0,0,0,0,7},
            {0,0,0,0,0,0,0,6,3},
            {4,0,0,0,0,0,0,0,0}
    };

    static int[][] easyBoard = {
            {4,3,0,5,0,0,0,0,7},
            {0,0,0,0,0,4,2,5,9},
            {0,5,0,8,6,0,0,4,3},
            {3,0,7,0,0,9,0,0,0},
            {0,0,6,4,0,1,7,0,0},
            {0,0,0,2,0,0,6,0,4},
            {5,1,0,0,4,8,0,2,0},
            {8,9,2,3,0,0,0,0,0},
            {7,0,0,0,0,5,0,8,1}
    };

    static int[][] easyBoardNearlySolved = {
            {4,3,0,5,0,0,8,6,7},
            {0,0,8,0,0,4,2,5,9},
            {0,5,0,8,6,0,1,4,3},
            {3,4,7,6,8,9,5,1,0},
            {0,2,6,4,0,1,7,0,8},
            {0,8,0,2,0,0,6,0,4},
            {5,1,3,7,4,8,0,2,6},
            {8,9,2,3,0,0,4,0,0},
            {7,0,4,9,2,5,0,8,1}
    };

    static int[][] mediumBoard = {
            {0,2,8,0,0,3,0,0,6},
            {1,3,0,0,0,0,0,0,0},
            {0,0,9,0,0,8,0,7,0},
            {0,0,6,5,0,7,0,0,0},
            {5,0,0,0,6,0,0,0,7},
            {0,0,0,3,0,9,2,0,0},
            {0,7,0,4,0,0,5,0,0},
            {0,0,0,0,0,0,0,4,1},
            {9,0,0,1,0,0,7,8,0}
    };

    static int[][] easy17 = {
            {0,0,0,0,4,1,0,0,0},
            {0,6,0,0,0,0,2,0,0},
            {0,0,0,0,0,0,0,0,0},
            {3,2,0,6,0,0,0,0,0},
            {0,0,0,0,5,0,0,4,1},
            {7,0,0,0,0,0,0,0,0},
            {0,0,0,2,0,0,3,0,0},
            {0,4,8,0,0,0,0,0,0},
            {5,0,1,0,0,0,0,0,0}
    };

    static int[][] medium2 = {
            {0,7,1,0,0,0,9,4,0},
            {4,2,0,0,0,0,0,5,1},
            {0,0,0,0,0,0,0,0,0},
            {1,0,0,5,0,9,0,0,2},
            {5,0,0,1,3,7,0,0,6},
            {7,0,0,6,0,4,0,0,5},
            {0,0,0,0,0,0,0,0,0},
            {2,6,0,0,0,0,0,1,7},
            {0,5,9,0,0,0,6,8,0}
    };

    static int[][] medium3 = {
            {0,4,5,0,0,0,0,0,0},
            {0,0,0,3,0,0,0,0,2},
            {0,0,2,9,0,5,3,0,4},
            {0,0,7,6,0,4,9,5,0},
            {0,0,0,0,0,0,0,0,0},
            {0,9,8,2,0,1,4,0,0},
            {5,0,6,4,0,7,2,0,0},
            {8,0,0,0,0,2,0,0,0},
            {0,0,0,0,0,0,7,6,0}
    };

    static int[][] hard2 = {
            {0,0,0,8,0,0,6,0,1},
            {1,0,6,0,7,9,0,0,0},
            {0,0,0,5,1,0,0,0,9},
            {6,0,5,2,0,0,0,1,0},
            {0,1,4,0,0,0,9,8,0},
            {0,2,0,0,0,1,4,0,5},
            {3,0,0,0,2,5,0,0,0},
            {0,0,0,3,4,0,0,0,0},
            {5,0,7,0,0,8,0,0,0}
    };
}
