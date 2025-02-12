package org.example.engine.pieces;

public class Dog extends Piece{
    public Dog(boolean isWhite) {
        super(isWhite);
        setCost(3);
        setName("d");
    }
}
