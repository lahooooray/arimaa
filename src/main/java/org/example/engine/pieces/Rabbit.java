package org.example.engine.pieces;

import org.example.engine.Cell;

public class Rabbit extends Piece{
    public Rabbit(boolean isWhite) {
        super(isWhite);
        setCost(1);
        setName("r");
    }

    @Override
    public boolean legalMove(int x, int y, Cell cell) {
        boolean can_not_move_back = (!isGold() && getCurrentPositionY() - y != -1) || (isGold() && y - getCurrentPositionY() != -1);
        return super.legalMove(x, y, cell) && can_not_move_back;
    }
}
