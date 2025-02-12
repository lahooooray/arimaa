import org.example.engine.Cell;
import org.example.engine.pieces.Rabbit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RabbitTest {
    private Rabbit silverRabbit;
    private Rabbit goldRabbit;

    @BeforeEach
    void setUp() {
        silverRabbit = new Rabbit(false); // Silver rabbit
        goldRabbit = new Rabbit(true); // Gold rabbit
    }

    @Test
    void testRabbitInitialSetup() {
        assertEquals(1, silverRabbit.getCost());
        assertEquals("r", silverRabbit.toString());

        assertEquals(1, goldRabbit.getCost());
        assertEquals("R", goldRabbit.toString());
    }

    @Test
    void testSilverRabbitLegalMove() {
        Cell cell = new Cell(3, 5);
        Cell cell1 = new Cell(5, 5);
        Cell cell2 = new Cell(4, 6);
        Cell cell3 = new Cell(4, 4);
        silverRabbit.setCurrentPosition(4, 5);

        assertTrue(silverRabbit.legalMove(4, 4, cell3)); // Forward move
        assertFalse(silverRabbit.legalMove(4, 6, cell2)); // Backward move
        assertTrue(silverRabbit.legalMove(5, 5, cell1)); // Side move
        assertTrue(silverRabbit.legalMove(3, 5, cell)); // Side move

        cell.setOccupied(true);
        assertFalse(silverRabbit.legalMove(3, 5, cell)); // Move to occupied cell
    }

    @Test
    void testGoldRabbitLegalMove() {
        Cell cell = new Cell(2, 1);
        Cell cell1 = new Cell(2, 3);
        Cell cell2 = new Cell(3, 1);
        Cell cell3 = new Cell(1, 2);
        goldRabbit.setCurrentPosition(2, 2);

        assertTrue(goldRabbit.legalMove(2, 3, cell1)); // Forward move
        assertFalse(goldRabbit.legalMove(2, 1, cell)); // Backward move
        assertTrue(goldRabbit.legalMove(3, 2, cell2)); // Side move
        assertTrue(goldRabbit.legalMove(1, 2, cell3)); // Side move

        cell.setOccupied(true);
        assertFalse(goldRabbit.legalMove(2, 1, cell)); // Move to occupied cell
    }

    @Test
    void testMoveTo() {
        Cell[][] board = new Cell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Cell(i, j);
            }
        }

        goldRabbit.setCurrentPosition(2, 2);
        goldRabbit.moveTo(3, 2, board);

        assertFalse(board[2][2].isOccupied());
        assertTrue(board[3][2].isOccupied());
    }
}

