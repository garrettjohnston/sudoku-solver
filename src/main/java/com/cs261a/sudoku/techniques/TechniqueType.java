package com.cs261a.sudoku.techniques;

/**
 * Created by Garrett on 5/25/2015.
 * Enumeration of Technique types that will be used by TechniqueFactory
 */
public enum TechniqueType {

    FullHouse("Full House"),
    HiddenSingle("Hidden Single"),
    NakedSingle("Naked Single"),
    Pointing("Pointing"),
    NakedPair("Naked Pair"),
    HiddenPair("Hidden Pair") {
        @Override
        public TechniqueType next() { return null; };
    };

    private String name;

    TechniqueType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public TechniqueType next() {
        return values()[ordinal() + 1];
    }

    public static TechniqueType getFirst() {
        return FullHouse;
    }
}