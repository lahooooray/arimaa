package org.example.engine.pieces;

import org.example.engine.Cell;

public abstract class Piece{
    private final boolean isGold;
    private boolean isFrozen;
    private boolean isKilled;
    private int x;
    private int y;
    private int cost;
    private String name;

    public Piece(boolean isGold) {
        this.isGold = isGold;
        this.isFrozen = false;
        this.isKilled = false;
        this.x = -1;
        this.y = -1;
    }
    public boolean isGold(){
        return this.isGold;
    }

    public boolean isKilled() {
        return this.isKilled;
    }
    public void setKilled(boolean isKilled) {
        this.isKilled = isKilled;
    }

    public int getCurrentPositionX() {
        return this.x;
    }
    public int getCurrentPositionY() {
        return this.y;
    }
    public void setCurrentPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean isFrozen() {
        return isFrozen;
    }
    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public int getCost() {
        return this.cost;
    }
    protected void setCost(int cost) {
        this.cost = cost;
    }

    public void setName(String name) {
        if (this.isGold){
            name = name.toUpperCase();
        }else name = name.toLowerCase();
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }

    /**
     * Calculates the direction of movement from the current position to a new position.
     *
     * @param new_x the new x-coordinate
     * @param new_y the new y-coordinate
     * @return a string representing the direction of the move: "n" for north, "s" for south, "e" for east, "w" for west
     */
    public String calculateMoveDirection(int new_x, int new_y){
        if(new_x > this.x) return "e";
        else if(new_x < this.x) return "w";
        else if(new_y > this.y) return "n";
        else return "s";
    }

    /**
     * Generates a string representation of a move from a previous position to a new position.
     *
     * @param previous_position the cell representing the previous position
     * @param new_x the new x-coordinate
     * @param new_y the new y-coordinate
     * @return a string representing the move
     */
    public String moveToString(Cell previous_position, int new_x, int new_y){
        return name+previous_position.toString()+calculateMoveDirection(new_x, new_y) + " ";
    }

    /**
     * Generates a string representation of placing the piece on a given cell.
     *
     * @param cell the cell on which the piece is placed
     * @return a string representing the placement move
     */
    public String placeMoveToString(Cell cell){
        return name+ cell + " ";
    }

    public String isKilledToString(Cell trapCell){
        return name+trapCell+"x ";
    }

    /**
     * Checks if a move to the specified coordinates is legal.
     *
     * @param x the target x-coordinate
     * @param y the target y-coordinate
     * @param cell the cell to move to
     * @return true if the move is legal, false otherwise
     */
    public boolean legalMove(int x, int y, Cell cell){
        if(!this.isFrozen() && !cell.isOccupied()) {
            return (Math.abs(this.x - x) == 1 && this.y == y) || (Math.abs(this.y - y) == 1 && this.x == x);
        }
        return false;
    }

    /**
     * Checks if a move is legal for pulling a piece.
     *
     * @param x the target x-coordinate
     * @param y the target y-coordinate
     * @param pieceToMoveX the x-coordinate of the piece to move
     * @param pieceToMoveY the y-coordinate of the piece to move
     * @return true if the move is legal for pulling, false otherwise
     */
    private boolean legalMoveForPull(int x, int y, int pieceToMoveX, int pieceToMoveY){
        return (Math.abs(this.x - x) == 1 && this.y == y) || (Math.abs(this.y - y) == 1 && this.x == x) && ((Math.abs(this.x - pieceToMoveX) == 1 && this.y == pieceToMoveY) || (Math.abs(this.y - pieceToMoveY) == 1 && this.x == pieceToMoveX) );
    }

    /**
     * Checks if a move is legal for pushing a piece.
     *
     * @param x the target x-coordinate
     * @param y the target y-coordinate
     * @param pieceToMoveX the x-coordinate of the piece to move
     * @param pieceToMoveY the y-coordinate of the piece to move
     * @return true if the move is legal for pushing, false otherwise
     */
    private boolean legalMoveForPush(int x, int y, int pieceToMoveX, int pieceToMoveY){
        return (Math.abs(this.x - pieceToMoveX) == 1 && this.y == pieceToMoveY) || (Math.abs(this.y - pieceToMoveY) == 1 && this.x == pieceToMoveX) && ((Math.abs(x - pieceToMoveX) == 1 && y == pieceToMoveY) || (Math.abs(y - pieceToMoveY) == 1 && x == pieceToMoveX) );
    }


    /**
     * Checks if a push move is legal.
     *
     * @param pieceToPush the piece to be pushed
     * @param cell the cell to push the piece to
     * @param pushToX the x-coordinate to push to
     * @param pushToY the y-coordinate to push to
     * @param playerIsGold the player color (gold or not)
     * @return true if the push move is legal, false otherwise
     */
    public boolean legalPush(Piece pieceToPush, Cell cell, int pushToX, int pushToY, boolean playerIsGold){
        if(playerIsGold == this.isGold) {
            return this.cost > pieceToPush.getCost() && !cell.isOccupied() && isGold != pieceToPush.isGold && this.legalMoveForPush(pushToX, pushToY, pieceToPush.getCurrentPositionX(), pieceToPush.getCurrentPositionY());
        }
        else if (playerIsGold == pieceToPush.isGold) {
            return this.cost < pieceToPush.getCost() && !cell.isOccupied() && this.isGold != pieceToPush.isGold && pieceToPush.legalMoveForPush(pushToX, pushToY, this.x, this.y);
        }
        return false;
    }

    /**
     * Checks if a pull move is legal.
     *
     * @param pieceToPull the piece to be pulled
     * @param cell the cell to pull the piece to
     * @param pullToX the x-coordinate to pull to
     * @param pullToY the y-coordinate to pull to
     * @param playerIsGold the player color (gold or not)
     * @return true if the pull move is legal, false otherwise
     */
    public boolean legalPull(Piece pieceToPull, Cell cell, int pullToX, int pullToY, boolean playerIsGold) {
        if (playerIsGold == this.isGold) {
            return this.cost > pieceToPull.getCost() && !cell.isOccupied() && isGold != pieceToPull.isGold && this.legalMoveForPull(pullToX, pullToY, pieceToPull.getCurrentPositionX(), pieceToPull.getCurrentPositionY());
        } else if (playerIsGold == pieceToPull.isGold()) {
            return this.cost < pieceToPull.getCost() && !cell.isOccupied() && isGold != pieceToPull.isGold && pieceToPull.legalMoveForPull(pullToX, pullToY, this.x, this.y);

        }
        return false;
    }

    /**
     * Moves the piece to the specified coordinates on the board.
     *
     * @param x the target x-coordinate
     * @param y the target y-coordinate
     * @param board the board represented as a 2D array of cells
     */
    public void moveTo(int x, int y, Cell[][] board){
        if(this.x >= 0 && this.y >= 0) {
            board[this.x][this.y].setOccupied(false);
        }
        this.setCurrentPosition(x, y);
        board[x][y].setOccupied(true);
    }
}
