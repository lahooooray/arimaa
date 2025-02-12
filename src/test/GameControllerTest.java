import org.example.engine.GameController;
import org.example.engine.pieces.Piece;
import org.example.engine.pieces.Rabbit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {
    private GameController gameController;

    @BeforeEach
    public void setUp() {
        gameController = new GameController();
    }

    @Test
    public void testChangeTurn() {
        gameController.change_turn();
        assertFalse(gameController.getGoldPlayer().isMyTurn());
        assertTrue(gameController.getSilverPlayer().isMyTurn());

        gameController.change_turn();
        assertTrue(gameController.getGoldPlayer().isMyTurn());
        assertFalse(gameController.getSilverPlayer().isMyTurn());
    }

    @Test
    public void testGetSelectedPiece() {
        Piece piece = gameController.getPiece(3);
        piece.setCurrentPosition(0, 1);
        int pieceIndex = gameController.getSelectedPiece(0, 1);
        assertNotEquals(-1, pieceIndex);
        piece = gameController.getPiece(pieceIndex);
        assertEquals(0, piece.getCurrentPositionX());
        assertEquals(1, piece.getCurrentPositionY());
    }

    @Test
    public void testAddToList() {
        LinkedList<Piece> pieces = gameController.getPieces();
        assertEquals(32, pieces.size());
    }

    @Test
    public void testFreezeCheck() {
        // Setup board state where a piece should be frozen
        Piece piece = gameController.getPiece(5);
        piece.setCurrentPosition(0, 0);
        Piece strongerOpponentPiece = gameController.getPiece(16);
        strongerOpponentPiece.setCurrentPosition(0, 1);
        gameController.freezeCheck();
        assertTrue(piece.isFrozen());

        // Move the opponent piece away and recheck
        strongerOpponentPiece.setCurrentPosition(1, 1);
        gameController.freezeCheck();
        assertFalse(piece.isFrozen());
    }

    @Test
    public void testKillCheck() {
        // Setup board state where a piece should be killed on a trap
        Piece strongerOpponentPiece = gameController.getPiece(16);
        strongerOpponentPiece.setCurrentPosition(2, 1);
        Piece piece = gameController.getPiece(15);
        piece.setCurrentPosition(2, 2);
        gameController.killCheck();
        assertTrue(piece.isKilled());

        // Move the piece away from the trap and recheck
        piece = gameController.getPiece(14);
        piece.setCurrentPosition(2, 3);
        gameController.killCheck();
        assertFalse(piece.isKilled());
    }

    @Test
    public void testRabbitReachedEnd() {
        // Set up a rabbit reaching the end of the board
        Rabbit rabbit = (Rabbit) gameController.getPiece(15); // Assuming piece 24 is a rabbit
        rabbit.setCurrentPosition(7, 7);
        assertTrue(gameController.rabbitReachedEnd());
        assertEquals("One of your rabbits reach the end!", gameController.loosingWinningCause);

        // Reset and check with a silver rabbit
        rabbit = (Rabbit) gameController.getPiece(25); // Assuming piece 25 is a rabbit
        rabbit.setCurrentPosition(0, 0);
        assertTrue(gameController.rabbitReachedEnd());
        assertEquals("One of your rabbits reach the end!", gameController.loosingWinningCause);
    }

    @Test
    public void testNoRabbitsLeft() {
        // Setup all rabbits being killed for gold player
        for (int i = 8; i < 16; i++) {
            Rabbit rabbit = (Rabbit) gameController.getPiece(i);
            rabbit.setKilled(true);
        }
        assertTrue(gameController.noRabbitsLeft());
        assertEquals("The opponent lost all of their rabbits!", gameController.loosingWinningCause);

        // Reset and check with all silver rabbits killed
        for (int i = 24; i < 32; i++) {
            Rabbit rabbit = (Rabbit) gameController.getPiece(i);
            rabbit.setKilled(true);
        }
        assertTrue(gameController.noRabbitsLeft());
        assertEquals("The opponent lost all of their rabbits!", gameController.loosingWinningCause);
    }

    @Test
    public void testPlayerWonCheck() {
        gameController.getGoldPlayer().setWon();
        assertTrue(gameController.playerWonCheck());
        gameController.getGoldPlayer().setWon();
        gameController.getSilverPlayer().setWon();
        assertTrue(gameController.playerWonCheck());
    }

    @Test
    public void testWriteMove() {
        Piece piece = gameController.getPiece(0);
        piece.setCurrentPosition(1, 1);
        String move = gameController.writeMove(piece, 2, 2);
        assertNotNull(move);
    }

    @Test
    public void testBoardToImage() {
        gameController.boardToImage();
        ArrayList<String[][]> movesImages = gameController.getMovesImages();
        assertEquals(1, movesImages.size());
        String[][] image = movesImages.get(0);
        assertNotNull(image);
        assertEquals(11, image.length);
        assertEquals(20, image[0].length);
    }

    @Test
    public void testStopTimer() {
        gameController.stopTimer();
        assertNotNull(gameController.timer);
    }

    @Test
    public void testLegalMovesEmptyCheck() {
        gameController.calculateLegalMoves();
        gameController.getLegalMovesSilver().clear();
        assertTrue(gameController.legalMovesEmptyCheck());
        assertEquals("The opponent does not have any legal moves to make!", gameController.loosingWinningCause);

        gameController.calculateLegalMoves();
        gameController.getLegalMovesGold().clear();
        assertTrue(gameController.legalMovesEmptyCheck());
        assertEquals("The opponent does not have any legal moves to make!", gameController.loosingWinningCause);
    }
}