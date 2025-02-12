package org.example.engine;

import javafx.application.Platform;
import org.example.engine.pieces.*;
import org.example.gui.GameDisplay;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameController {
    private static final Logger logger = Logger.getLogger(GameController.class.getName());
    private final Player goldPlayer;
    private final Player silverPlayer;
    private final LinkedList<Piece> pieces = new LinkedList<>();
    private final LinkedList<Rabbit> rabbits = new LinkedList<>();
    private LinkedList<Move> legalMovesGold = new LinkedList<>();
    private LinkedList<Move> legalMovesSilver = new LinkedList<>();
    private final ArrayList<String> moves = new ArrayList<>();
    private final ArrayList<String[][]> movesImages = new ArrayList<>();
    private final Cell[][] board = new Cell[8][8];
    public final Timer timer = new Timer();
    public TimerTask timerTask;
    private int seconds = 0;
    public String loosingWinningCause = "";

    private static final String RABBIT_REACH_END = "One of your rabbits reach the end!";
    private static final String NO_RABBITS_LEFT = "The opponent lost all of their rabbits!";
    private static final String NO_LEGAL_MOVES_LEFT = "The opponent does not have any legal moves to make!";

    /**
     * Controls the game logic, including piece movements, turn changes, and game state checks.
     */
    public GameController() {
        createBoard();
        goldPlayer = new Player(true);
        goldPlayer.setMyTurn(true);
        silverPlayer = new Player(false);
        silverPlayer.setMyTurn(false);
        addToList(true);
        addToList(false);
        logger.log(Level.FINE, "GameController created");
    }

    public ArrayList<String> getMoves() {
        return moves;
    }
    public ArrayList<String[][]> getMovesImages() {
        return movesImages;
    }

    /**
     * Changes the turn between gold and silver players.
     */
    public void change_turn(){
        if(goldPlayer.isMyTurn()){
            goldPlayer.setMyTurn(false);
            silverPlayer.setMyTurn(true);
            logger.log(Level.INFO, "Silver player's turn");
        }else{
            goldPlayer.setMyTurn(true);
            silverPlayer.setMyTurn(false);
            logger.log(Level.INFO, "Gold player's turn");
        }
    }

    /**
     * Gets the index of the piece at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the index of the piece, or -1 if no piece is found
     */
    public int getSelectedPiece(int x, int y){
        for(int i = 0; i < pieces.size(); i++){
            if(pieces.get(i).getCurrentPositionX() == x && pieces.get(i).getCurrentPositionY() == y){
                return i;
            }
        }
        return -1;
    }

    public Piece getPiece(int i){
        return pieces.get(i);
    }

    /**
     * Adds pieces to the list based on the player's color.
     *
     * @param isGold true if the pieces belong to the gold player, false if they belong to the silver player
     */
    private void addToList(boolean isGold){
        pieces.add(new Camel(isGold));
        pieces.add(new Elephant(isGold));
        for(int i = 0; i < 2; i++){
            pieces.add(new Horse(isGold));
            pieces.add(new Dog(isGold));
            pieces.add(new Cat(isGold));
        }
        for(int i = 0; i < 8; i++){
            Rabbit r = new Rabbit(isGold);
            pieces.add(r);
            rabbits.add(r);
        }
    }
    public LinkedList<Piece> getPieces(){
        return this.pieces;
    }
    private void createBoard(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Cell cell = new Cell(i, j);
                board[i][j] = cell;
            }
        }
    }

    public Cell[][] getBoard(){
        return this.board;
    }

    /**
     * Checks if any pieces are frozen based on their surrounding pieces.
     */
    public void freezeCheck(){
        for(Piece piece : this.pieces) {
            Piece p1;
            int x1 = piece.getCurrentPositionX() - 1;
            int y1 = piece.getCurrentPositionY();
            int x2 = piece.getCurrentPositionX() + 1;
            int y2 = piece.getCurrentPositionY();
            int x3 = piece.getCurrentPositionX();
            int y3 = piece.getCurrentPositionY() - 1;
            int x4 = piece.getCurrentPositionX();
            int y4 = piece.getCurrentPositionY() + 1;
            int index1 = getSelectedPiece(x1, y1);
            int index2 = getSelectedPiece(x2, y2);
            int index3 = getSelectedPiece(x3, y3);
            int index4 = getSelectedPiece(x4, y4);
            if(index1 != -1){
                p1 = getPieces().get(index1);
                if(p1.isGold() == piece.isGold() && p1.getCost() > piece.getCost()
                        ||(p1.isGold() != piece.isGold() && p1.getCost() <= piece.getCost())){
                    piece.setFrozen(false);
                    continue;
                } else if (p1.isGold() != piece.isGold() && p1.getCost() > piece.getCost()) {
                    piece.setFrozen(true);
                }
            }
            if (index2 != -1){
                p1 = getPieces().get(index2);
                if(p1.isGold() == piece.isGold() && p1.getCost() > piece.getCost()
                        ||(p1.isGold() != piece.isGold() && p1.getCost() <= piece.getCost())){
                    piece.setFrozen(false);
                    continue;
                } else if (p1.isGold() != piece.isGold() && p1.getCost() > piece.getCost()) {
                    piece.setFrozen(true);
                }
            }
            if(index3 != -1){
                p1 = getPieces().get(index3);
                if(p1.isGold() == piece.isGold() && p1.getCost() > piece.getCost()
                        ||(p1.isGold() != piece.isGold() && p1.getCost() <= piece.getCost())){
                    piece.setFrozen(false);
                    continue;
                } else if (p1.isGold() != piece.isGold() && p1.getCost() > piece.getCost()) {
                    piece.setFrozen(true);
                }
            }
            if (index4 != -1){
                p1 = getPieces().get(index4);
                if(p1.isGold() == piece.isGold() && p1.getCost() > piece.getCost()
                        ||(p1.isGold() != piece.isGold() && p1.getCost() <= piece.getCost())){
                    piece.setFrozen(false);
                    continue;
                } else if (p1.isGold() != piece.isGold() && p1.getCost() > piece.getCost()) {
                    piece.setFrozen(true);
                }
            }

            if(index1 == -1 && index2 == -1 && index3 == -1 && index4 == -1){
                piece.setFrozen(false);
            }
        }
    }

    /**
     * Checks if any pieces are killed based on their surrounding pieces and if they are on a trap cell.
     */
    public String killCheck(){
        String move = "";
        for(Piece piece : this.pieces) {
            if(!piece.isKilled()) {
                Piece p1;
                int x_left = piece.getCurrentPositionX() - 1;
                int y_left = piece.getCurrentPositionY();
                int x_right = piece.getCurrentPositionX() + 1;
                int y_right = piece.getCurrentPositionY();
                int x_down = piece.getCurrentPositionX();
                int y_down = piece.getCurrentPositionY() - 1;
                int x_up = piece.getCurrentPositionX();
                int y_up = piece.getCurrentPositionY() + 1;
                int index_left = getSelectedPiece(x_left, y_left);
                int index_right = getSelectedPiece(x_right, y_right);
                int index_down = getSelectedPiece(x_down, y_down);
                int index_up = getSelectedPiece(x_up, y_up);

                int currentX = piece.getCurrentPositionX();
                int currentY = piece.getCurrentPositionY();
                if (index_left != -1 && getBoard()[currentX][currentY].isTrap()) {
                    p1 = getPieces().get(index_left);
                    if (p1.isGold() == piece.isGold()) {
                        piece.setKilled(false);
                        continue;
                    } else {
                        piece.setKilled(true);
                    }
                }
                if (index_right != -1 && getBoard()[currentX][currentY].isTrap()) {
                    p1 = getPieces().get(index_right);
                    if (p1.isGold() == piece.isGold()) {
                        piece.setKilled(false);
                        continue;
                    } else {
                        piece.setKilled(true);
                    }
                }
                if (index_down != -1 && getBoard()[currentX][currentY].isTrap()) {
                    p1 = getPieces().get(index_down);
                    if (p1.isGold() == piece.isGold()) {
                        piece.setKilled(false);
                        continue;
                    } else {
                        piece.setKilled(true);
                    }
                }
                if (index_up != -1 && getBoard()[currentX][currentY].isTrap()) {
                    p1 = getPieces().get(index_up);
                    if (p1.isGold() == piece.isGold()) {
                        piece.setKilled(false);
                        continue;
                    } else {
                        piece.setKilled(true);
                    }
                }
                if (piece.getCurrentPositionX() > -1 && piece.getCurrentPositionY() > -1 &&
                        index_left == -1 && index_right == -1 && index_up == -1 &&
                        index_down == -1 && getBoard()[currentX][currentY].isTrap()) {
                    piece.setKilled(true);
                }
                if(piece.isKilled()){
                    move+=piece.isKilledToString(board[piece.getCurrentPositionX()][piece.getCurrentPositionY()]);
                }
            }
        }
        return move;
    }

    /**
     * Checks if any rabbit has reached the end of the board, indicating a win.
     *
     * @return true if a rabbit has reached the end, false otherwise
     */
    public boolean rabbitReachedEnd(){
        for(Rabbit rabbit : rabbits){
            if (rabbit.isGold() && rabbit.getCurrentPositionY() == 7) {
                loosingWinningCause = RABBIT_REACH_END;
                goldPlayer.setWon();
                return true;
            } else if (!rabbit.isGold() && rabbit.getCurrentPositionY() == 0) {
                loosingWinningCause = RABBIT_REACH_END;
                silverPlayer.setWon();
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all rabbits of a player are killed, indicating a win for the opponent.
     *
     * @return true if all rabbits of a player are killed, false otherwise
     */
    public boolean noRabbitsLeft(){
        int counterForSilver = 0;
        int counterForGold = 0;
        for(Rabbit rabbit : rabbits){
            if (rabbit.isGold() && rabbit.isKilled()) {
                counterForGold++;
            } else if (!rabbit.isGold() && rabbit.isKilled()) {
                counterForSilver++;
            }
        }
        if(counterForSilver == 8){
            loosingWinningCause = NO_RABBITS_LEFT;
            goldPlayer.setWon();
            return true;
        }
        if(counterForGold == 8){
            loosingWinningCause = NO_RABBITS_LEFT;
            silverPlayer.setWon();
            return true;
        }
        return false;
    }

    /**
     * Checks if either player has won the game.
     *
     * @return true if a player has won, false otherwise
     */
    public boolean playerWonCheck(){
        return goldPlayer.isWon() || silverPlayer.isWon();
    }

    public Player getGoldPlayer(){
        return this.goldPlayer;
    }
    public Player getSilverPlayer(){
        return this.silverPlayer;
    }

    /**
     * Writes a move to the move list.
     *
     * @param piece the piece to move
     * @param x the target x-coordinate
     * @param y the target y-coordinate
     * @return the move as a string
     */
    public String writeMove(Piece piece, int x, int y){
        int currentX = piece.getCurrentPositionX();
        int currentY = piece.getCurrentPositionY();
        if(currentX >= 0 && currentY >= 0){
            return piece.moveToString(board[piece.getCurrentPositionX()][piece.getCurrentPositionY()], x, y);
        }else{
            return piece.placeMoveToString(board[x][y]);
        }
    }

    public void boardToImage(){
        String[][] image = new String[11][20];
        for(int i = 0; i < 11; i++){
            for(int j = 0; j < 20; j++){
                image[i][j]=" ";
            }
        }
        image[3][7] = "x";
        image[3][13] = "x";
        image[6][7] = "x";
        image[6][13] = "x";
        image[9][1] = "+";
        image[0][1] = "+";
        image[0][19] = "+";
        image[9][19] = "+";
        for(int i = 1; i < 9; i++){
            image[i][0] = String.valueOf(9-i);
            image[10][(i-1)*2+3] = String.valueOf((char)('a' + (i-1)));
            image[i][1] = "|";
            image[i][19] = "|";
            for(int j = 2; j < 19; j+=2) {
                image[i][j] = " ";
            }
        }
        for(int i = 2; i < 18; i++){
            image[0][i] = "-";
            image[9][i] = "-";
        }
        for(Piece piece : pieces){
            int i = piece.getCurrentPositionX();
            int j = piece.getCurrentPositionY();
            if(i > -1 && j > -1 && !piece.isKilled()) image[8-j][i*2+3] = piece.toString();
        }
        movesImages.add(image);
    }

    /**
     * Restarts the game timer and updates the display.
     *
     * @param gameDisplay the game display to update
     */
    public void restartTimer(GameDisplay gameDisplay){
        if (this.timerTask != null) {
            this.timerTask.cancel();
            logger.log(Level.FINE, "Timer was not started");
        }
        logger.log(Level.FINE, "Timer start");
        this.timerTask = new TimerTask(){
            @Override
            public void run(){
                seconds++;
                Platform.runLater(() -> gameDisplay.time.setText(LocalTime.MIN.plusSeconds(seconds).format(DateTimeFormatter.ISO_LOCAL_TIME)));
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000L);
    }

    public void stopTimer(){
        logger.log(Level.FINE, "Timer stopped");
        this.timer.cancel();
    }

    public LinkedList<Move> getLegalMovesSilver() {
        return legalMovesSilver;
    }

    public boolean legalMovesEmptyCheck(){
        if(legalMovesSilver.isEmpty()) {
            loosingWinningCause = NO_LEGAL_MOVES_LEFT;
            goldPlayer.setWon();
            return true;
        }else if(legalMovesGold.isEmpty()){
            loosingWinningCause = NO_LEGAL_MOVES_LEFT;
            silverPlayer.setWon();
            return true;
        }
        return false;
    }

    public void calculateLegalMoves(){
        legalMovesGold = legalMoves(0, 16);
        legalMovesSilver = legalMoves(16, 32);
    }

    private LinkedList<Move> legalMoves(int rangeStart, int rangeEnd) {
        LinkedList<Move> legalMoves = new LinkedList<>();
        for (int k = rangeStart; k < rangeEnd; k++) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Piece p = getPiece(k);
                    if (p.legalMove(i, j, getBoard()[i][j])) {
                        legalMoves.add(new Move(Move.ORDINARY_MOVE_INDEX, k, i, j));
                    }
                    if (getBoard()[i][j].isOccupied() && getSelectedPiece(i, j) != k) {
                        Piece pieceToMove = getPiece(getSelectedPiece(i, j));
                        if (p.legalPush(pieceToMove, getBoard()[i][j], i - 1, j, false)) {
                            legalMoves.add(new Move(Move.PUSH_MOVE_INDEX, k, i, j, i - 1, j));
                        }
                        if (p.legalPush(pieceToMove, getBoard()[i][j], i, j - 1, false)) {
                            legalMoves.add(new Move(Move.PUSH_MOVE_INDEX, k, i, j, i, j - 1));
                        }
                        if (p.legalPush(pieceToMove, getBoard()[i][j], i + 1, j, false)) {
                            legalMoves.add(new Move(Move.PUSH_MOVE_INDEX, k, i, j, i + 1, j));
                        }
                        if (p.legalPush(pieceToMove, getBoard()[i][j], i, j + 1, false)) {
                            legalMoves.add(new Move(Move.PUSH_MOVE_INDEX, k, i, j, i, j + 1));
                        }
                        if (p.legalPull(pieceToMove, getBoard()[i][j], i - 1, j, false)) {
                            legalMoves.add(new Move(Move.PULL_MOVE_INDEX, k, i, j, i - 1, j));
                        }
                        if (p.legalPull(pieceToMove, getBoard()[i][j], i, j - 1, false)) {
                            legalMoves.add(new Move(Move.PULL_MOVE_INDEX, k, i, j, i, j - 1));
                        }
                        if (p.legalPull(pieceToMove, getBoard()[i][j], i + 1, j, false)) {
                            legalMoves.add(new Move(Move.PULL_MOVE_INDEX, k, i, j, i + 1, j));
                        }
                        if (p.legalPull(pieceToMove, getBoard()[i][j], i, j + 1, false)) {
                            legalMoves.add(new Move(Move.PULL_MOVE_INDEX, k, i, j, i, j + 1));
                        }
                    }
                }
            }
        }
        return legalMoves;
    }

    public LinkedList<Move> getLegalMovesGold() {
        return legalMovesGold;
    }
}
