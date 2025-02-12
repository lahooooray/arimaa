package org.example.engine;

public class Cell{
    private boolean isOccupied;
    private final boolean isTrap;
    private String name = "";

    public Cell(final int x, final int y){
        this.name = String.valueOf((char)('a'+x)) + String.valueOf(y+1);
        this.isOccupied = false;
        this.isTrap = (x == 2 && y == 2) || (x == 2 && y == 5) || (x == 5 && y == 2) || (x == 5 && y == 5);
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public boolean isTrap() {
        return isTrap;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
