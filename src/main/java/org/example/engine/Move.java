package org.example.engine;

public class Move {
    public static final int ORDINARY_MOVE_INDEX = -1;
    public static final int PUSH_MOVE_INDEX = 0;
    public static final int PULL_MOVE_INDEX = 1;
    public int moveType;
    public int pieceIndex;
    public int x;
    public int y;
    public int pushPullToX;
    public int pushPullToY;

    /**
     * Constructs a move with the specified parameters.
     *
     * @param moveType          the type of move
     * @param pieceIndex        the index of the piece making the move
     * @param x                 the target row index
     * @param y                 the target column index
     */
    public Move(int moveType, int pieceIndex, int x, int y) {
        this.moveType = moveType;
        this.pieceIndex = pieceIndex;
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a move with the specified parameters, including push/pull target coordinates.
     *
     * @param moveType          the type of move
     * @param pieceIndex        the index of the piece making the move
     * @param x                 the target row index
     * @param y                 the target column index
     * @param pushPullToX       the row index to push/pull to
     * @param pushPullToY       the column index to push/pull to
     */
    public Move(int moveType, int pieceIndex, int x, int y, int pushPullToX, int pushPullToY) {
        this.moveType = moveType;
        this.pieceIndex = pieceIndex;
        this.x = x;
        this.y = y;
        this.pushPullToX = pushPullToX;
        this.pushPullToY = pushPullToY;
    }
}
