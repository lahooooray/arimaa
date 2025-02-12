package org.example.engine.pieces;

public class Cat extends Piece{
    public Cat(boolean isWhite) {
        super(isWhite);
        setCost(2);
        setName("c");
    }
}
