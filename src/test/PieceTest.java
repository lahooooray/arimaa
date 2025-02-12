import org.example.engine.Cell;
import org.example.engine.pieces.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PieceTest {
    private Piece piece;

    // A simple concrete class for testing abstract Piece class
    private static class TestPiece extends Piece {
        public TestPiece(boolean isGold) {
            super(isGold);
        }
    }

    @BeforeEach
    void setUp() {
        piece = new TestPiece(true); // Using a gold piece for testing
    }

    @Test
    void testIsGold() {
        assertTrue(piece.isGold());
    }

    @Test
    void testSetAndGetKilled() {
        assertFalse(piece.isKilled());
        piece.setKilled(true);
        assertTrue(piece.isKilled());
    }

    @Test
    void testSetAndGetCurrentPosition() {
        assertEquals(-1, piece.getCurrentPositionX());
        assertEquals(-1, piece.getCurrentPositionY());

        piece.setCurrentPosition(3, 5);
        assertEquals(3, piece.getCurrentPositionX());
        assertEquals(5, piece.getCurrentPositionY());
    }

    @Test
    void testSetAndIsFrozen() {
        assertFalse(piece.isFrozen());
        piece.setFrozen(true);
        assertTrue(piece.isFrozen());
    }

    @Test
    void testSetName() {
        piece.setName("Camel");
        assertEquals("CAMEL", piece.toString());

        Piece silverPiece = new TestPiece(false);
        silverPiece.setName("Camel");
        assertEquals("camel", silverPiece.toString());
    }

    @Test
    void testCalculateMoveDirection() {
        piece.setCurrentPosition(2, 2);
        assertEquals("e", piece.calculateMoveDirection(3, 2));
        assertEquals("w", piece.calculateMoveDirection(1, 2));
        assertEquals("n", piece.calculateMoveDirection(2, 3));
        assertEquals("s", piece.calculateMoveDirection(2, 1));
    }

    @Test
    void testMoveToString() {
        piece.setName("Camel");
        piece.setCurrentPosition(2, 2);
        Cell previousPosition = new Cell(2, 2);
        assertEquals("CAMELc3n ", piece.moveToString(previousPosition, 2, 3));
    }

    @Test
    void testPlaceMoveToString() {
        Cell cell = new Cell(4, 5);
        piece.setName("Cat");
        assertEquals("CATe6 ", piece.placeMoveToString(cell));
    }

    @Test
    void testIsKilledToString() {
        Cell trapCell = new Cell(2, 5);
        piece.setName("Dog");
        assertEquals("DOGc6x ", piece.isKilledToString(trapCell));
    }

    @Test
    void testLegalMove() {
        piece.setCurrentPosition(1, 1);
        Cell cell = new Cell(2, 1);
        Cell cell1 = new Cell(1, 2);
        Cell cell2 = new Cell(1, 0);
        Cell cell3 = new Cell(0, 1);

        assertTrue(piece.legalMove(2, 1, cell));
        assertTrue(piece.legalMove(1, 2, cell1));
        assertTrue(piece.legalMove(1, 0, cell2));
        assertTrue(piece.legalMove(0, 1, cell3));

        cell.setOccupied(true);
        assertFalse(piece.legalMove(2, 1, cell));
    }

    @Test
    void testMoveTo() {
        Cell[][] board = new Cell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Cell(i, j);
            }
        }

        piece.setCurrentPosition(3, 6);
        piece.moveTo(3, 7, board);

        assertFalse(board[3][6].isOccupied());
        assertTrue(board[3][7].isOccupied());
    }
}

