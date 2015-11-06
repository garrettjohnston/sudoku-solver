package com.cs261a.sudoku.techniques;

/**
 * This class take a techniqueType and gives back an instance of that technique class.
 */
public class TechniqueFactory {

    public static Technique getTechnique(TechniqueType type) {
        if(type == null)
            return null;

        switch(type) {
            case FullHouse:
                return new FullHouse();
            case HiddenSingle:
                return new HiddenSingle();
            case NakedSingle:
                return new NakedSingle();
            case Pointing:
                return new Pointing();
            case NakedPair:
                return new NakedPair();
            case HiddenPair:
                return new HiddenPair();
            default:
                throw new EnumConstantNotPresentException(type.getClass(), "Should not reach this state");
        }
    }
}
