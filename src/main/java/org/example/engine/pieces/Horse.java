package org.example.engine.pieces;

public class Horse extends Piece{
    public Horse(boolean isWhite) {
        super(isWhite);
        setCost(4);
        setName("h");
    }
}
