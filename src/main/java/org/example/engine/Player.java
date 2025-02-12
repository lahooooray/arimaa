package org.example.engine;

public class Player {
    protected final boolean isGold;
    protected boolean won = false;
    protected boolean isMyTurn;
    public Player(boolean isGold) {
        this.isGold = isGold;
    }

    public boolean isWon() {
        return won;
    }
    public void setWon() {
        this.won = true;
    }

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }
    public boolean isMyTurn() {
        return isMyTurn;
    }
}
