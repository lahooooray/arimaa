package org.example;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Rectangle;
import org.example.engine.GameController;
import org.example.engine.Move;
import org.example.engine.pieces.Piece;
import org.example.gui.GameDisplay;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code Game} class represents the main game logic and controls the flow of the game.
 */
public class Game implements Runnable {
    private Thread gameThread;
    private boolean isHuman;
    private static final int PUSH_BUTTON_INDEX = 0;
    private static final int PULL_BUTTON_INDEX = 1;
    private static final int FINISH_BUTTON_INDEX = 2;
    private static final int RESIGN_BUTTON_INDEX = 3;
    private static final int NO_BUTTON = -1;
    private static final int MAX_MOVES = 4;
    private static final int PUSH_PULL_MOVE_COST = 2;
    private int button = NO_BUTTON;
    private boolean imageClicked = false;
    private ImageView selectedImage;
    private ImageView selectedImagePullPush;
    private Piece selectedPushPullPiece;
    private Piece selectedPiece;
    private boolean cellClicked = false;
    private Rectangle selectedRect;
    private final GameDisplay gameDisplay;
    private final GameController gameController;
    private int stepCounter = 0;
    private int moveCounter = 0;
    private String move = "1g ";
    private static final Logger logger = Logger.getLogger(Game.class.getName());
    public String popUpMessage = "";
    private boolean defaultPlacementClicked = false;

    public Game() {
        this.gameDisplay = new GameDisplay();
        this.gameController = new GameController();
    }

    public void setHuman(boolean human) {
        isHuman = human;
    }

    public GameDisplay getGameDisplay() {
        return gameDisplay;
    }

    public GameController getGameController() {
        return gameController;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        logger.log(Level.FINEST, "Game thread started");
    }

    /**
     * Sets the effect for an image and handles image click events.
     *
     * @param image   the image view to set the effect on
     * @param images  the array of image views
     * @param effect  the effect to apply
     */
    private void imageSet(ImageView image, ImageView[] images, Effect effect) {
        Piece piece = gameController.getPiece(gameDisplay.getImageIndex(image));
        if ((!piece.isFrozen() && (button == NO_BUTTON || button == FINISH_BUTTON_INDEX)) ||
                (button == PUSH_BUTTON_INDEX || button == PULL_BUTTON_INDEX)) {
            if (image.getEffect() == effect) {
                gameDisplay.setHighlightEffect(image, effect);
            }
            image.setOnMouseClicked(mouseEvent -> {
                logger.log(Level.INFO, "ImageView clicked");
                if (button == NO_BUTTON) {
                    if (imageClicked) {
                        image.setEffect(effect);
                        imageClicked = false;
                    } else {
                        if (image.getOnMouseEntered() != null) {
                            imageClicked = true;
                            selectedImage = image;
                            gameDisplay.setGlowEffect(image);
                            for (ImageView im : images) {
                                if (im != image) {
                                    gameDisplay.setHighlightEffect(im, effect);
                                } else {
                                    imageClicked = false;
                                }
                            }
                        } else {
                            gameDisplay.setHighlightEffect(image, effect);
                            imageClicked = false;
                        }
                    }
                    if (selectedImage != null)
                        selectedPiece = gameController.getPiece(gameDisplay.getImageIndex(selectedImage));
                } else {
                    if (image.getOnMouseEntered() != null) {
                        if (imageClicked) {
                            selectedImagePullPush = image;
                            imageClicked = false;
                            selectedPushPullPiece = gameController.getPiece(gameDisplay.getImageIndex(selectedImagePullPush));
                        } else {
                            imageClicked = true;
                            selectedImage = image;
                            selectedPiece = gameController.getPiece(gameDisplay.getImageIndex(selectedImage));
                        }
                        gameDisplay.setGlowEffect(image);
                        for (ImageView im : gameDisplay.silverImages) {
                            if (im != image && im != selectedImage) {
                                gameDisplay.setHighlightEffect(im, gameDisplay.shadowEffectSilver);
                            }
                        }
                        for (ImageView im : gameDisplay.goldImages) {
                            if (im != image && im != selectedImage) {
                                gameDisplay.setHighlightEffect(im, gameDisplay.shadowEffectGold);
                            }
                        }
                    } else {
                        gameDisplay.setHighlightEffect(image, effect);
                        imageClicked = false;
                    }
                }
            });
        } else {
            gameDisplay.removeEffect(image, effect);
        }
    }

    /**
     * Sets the effect for a rectangle and handles rectangle click events.
     *
     * @param rectangle the rectangle to set the effect on
     */
    private void rectangleSet(Rectangle rectangle) {
        int[] index = gameDisplay.getSelectedPosition(rectangle);
        if (!gameController.getBoard()[index[0]][index[1]].isOccupied()) {
            if (rectangle.getEffect() == null) {
                gameDisplay.setHighlightEffect(rectangle, null);
            }

            rectangle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    logger.log(Level.INFO, "Rectangle clicked");
                    if (cellClicked) {
                        rectangle.setEffect(null);
                        cellClicked = false;
                    } else {
                        if (rectangle.getOnMouseEntered() != null) {
                            cellClicked = true;
                            selectedRect = rectangle;
                            gameDisplay.setGlowEffect(rectangle);
                            for (int i = 0; i < 8; i++) {
                                for (int j = 0; j < 8; j++) {
                                    if (gameDisplay.rectangles[i][j] != rectangle) {
                                        gameDisplay.setHighlightEffect(gameDisplay.rectangles[i][j], null);
                                    } else {
                                        cellClicked = false;
                                    }
                                }
                            }
                        } else {
                            gameDisplay.setHighlightEffect(rectangle, null);
                            cellClicked = false;
                        }
                    }

                    if (!gameDisplay.panesEmptyCheck()) {
                        placeFigures(rectangle);
                    } else {
                        boolean playerIsGold;
                        playerIsGold = gameController.getGoldPlayer().isMyTurn();
                        switch (button) {
                            case NO_BUTTON:
                                if (selectedPiece == null) break;
                                if (!selectedPiece.legalMove(index[0], index[1], gameController.getBoard()[index[0]][index[1]])) {
                                    gameDisplay.popUpWindow("ILLEGAL MOVE:\ncannot move your figure '" + selectedPiece + "' to the tile " + gameController.getBoard()[index[0]][index[1]]);
                                    logger.log(Level.FINE, "Tried to make an illegal move: " + selectedPiece + " to cell " + gameController.getBoard()[index[0]][index[1]]);
                                    break;
                                }
                                moveFigure(rectangle, selectedPiece, selectedImage);
                                break;
                            case PUSH_BUTTON_INDEX:
                                button = NO_BUTTON;
                                if (stepCounter > PUSH_PULL_MOVE_COST) {
                                    gameDisplay.popUpWindow("ILLEGAL PUSH: not enough moves for push left");
                                    break;
                                }
                                if (selectedPushPullPiece == null || selectedPiece == null) {
                                    gameDisplay.popUpWindow("Select TWO pieces for push");
                                    break;
                                }
                                if (!selectedPiece.legalPush(selectedPushPullPiece, gameController.getBoard()[index[0]][index[1]], index[0], index[1], playerIsGold)) {
                                    gameDisplay.popUpWindow("ILLEGAL PUSH: cannot proceed with the push");
                                    break;
                                }
                                logger.log(Level.INFO, "PUSH move is being made");
                                if (selectedPiece.getCost() > selectedPushPullPiece.getCost())
                                    pushOrPull(selectedPushPullPiece, selectedImagePullPush, selectedPiece, selectedImage, rectangle);
                                else
                                    pushOrPull(selectedPiece, selectedImage, selectedPushPullPiece, selectedImagePullPush, rectangle);
                                break;
                            case PULL_BUTTON_INDEX:
                                button = NO_BUTTON;
                                if (stepCounter > PUSH_PULL_MOVE_COST) {
                                    gameDisplay.popUpWindow("ILLEGAL PULL: not enough moves for pull left");
                                    break;
                                }
                                if (selectedPushPullPiece == null || selectedPiece == null) {
                                    gameDisplay.popUpWindow("Select TWO pieces for pull");
                                    break;
                                }
                                if (!selectedPiece.legalPull(selectedPushPullPiece, gameController.getBoard()[index[0]][index[1]], index[0], index[1], playerIsGold)) {
                                    gameDisplay.popUpWindow("ILLEGAL PULL: cannot proceed with the pull");
                                    break;
                                }
                                logger.log(Level.INFO, "PULL move is being made");
                                if (selectedPiece.getCost() > selectedPushPullPiece.getCost())
                                    pushOrPull(selectedPiece, selectedImage, selectedPushPullPiece, selectedImagePullPush, rectangle);
                                else
                                    pushOrPull(selectedPushPullPiece, selectedImagePullPush, selectedPiece, selectedImage, rectangle);
                                break;
                        }

                    }
                    reset();
                }

                void placeFigures(Rectangle rectangle) {
                    if (checkSelection()) {
                        int[] pos = gameDisplay.getSelectedPosition(rectangle);
                        if (canPlace(pos[1]) && !gameController.getBoard()[pos[0]][pos[1]].isOccupied()) {
                            placeOnCell(pos[0], pos[1], selectedImage, selectedPiece);
                            if (selectedPiece.isGold())
                                gameDisplay.setHighlightEffect(selectedImage, gameDisplay.shadowEffectGold);
                            else gameDisplay.setHighlightEffect(selectedImage, gameDisplay.shadowEffectSilver);
                            gameDisplay.setHighlightEffect(selectedRect, null);
                            gameDisplay.getSilverPane().getChildren().remove(selectedImage);
                            gameDisplay.getSilverPane().getChildren().remove(selectedImage);
                            if (gameDisplay.getGoldenPane().getChildren().isEmpty() && moveCounter == 0) {
                                stepCounter = MAX_MOVES;
                            } else if (gameDisplay.getSilverPane().getChildren().isEmpty() && moveCounter == 1) {
                                stepCounter = MAX_MOVES;
                            }
                        } else {
                            gameDisplay.popUpWindow("can't place there");
                        }
                    }
                }

                boolean checkSelection() {
                    return !cellClicked && !imageClicked && selectedImage != null && selectedRect != null;
                }

                boolean canPlace(int j) {
                    int selectedImageIndex = gameDisplay.getImageIndex(selectedImage);
                    return (j < 2 && gameDisplay.goldImages[selectedImageIndex].equals(selectedImage) && gameController.getGoldPlayer().isMyTurn()) || (gameController.getSilverPlayer().isMyTurn() && j > 5 && gameDisplay.silverImages[selectedImageIndex - 16].equals(selectedImage));
                }

                void reset() {
                    if (selectedImage != null) {
                        if (selectedPiece.isGold())
                            gameDisplay.setHighlightEffect(selectedImage, gameDisplay.shadowEffectGold);
                        else gameDisplay.setHighlightEffect(selectedImage, gameDisplay.shadowEffectSilver);
                    }
                    if (selectedImagePullPush != null) {
                        if (selectedPushPullPiece.isGold())
                            gameDisplay.setHighlightEffect(selectedImagePullPush, gameDisplay.shadowEffectGold);
                        else gameDisplay.setHighlightEffect(selectedImagePullPush, gameDisplay.shadowEffectSilver);
                    }
                    selectedPiece = null;
                    selectedPushPullPiece = null;
                    selectedImage = null;
                    selectedImagePullPush = null;
                    popUpMessage = null;
                }
            });
        } else {
            gameDisplay.removeEffect(rectangle, null);
        }
    }

    /**
     * Places a piece on a cell.
     *
     * @param i             the row index
     * @param j             the column index
     * @param selectedImage the selected image
     * @param selectedPiece the selected piece
     */
    private void placeOnCell(int i, int j, ImageView selectedImage, Piece selectedPiece) {
        String killMove = gameController.killCheck();
        move += killMove +gameController.writeMove(selectedPiece, i, j);
        if (gameDisplay.stackPanes[i][j].getChildren().size() == 2) {
            gameDisplay.stackPanes[i][j].getChildren().remove(1);
        }
        gameDisplay.stackPanes[i][j].getChildren().add(selectedImage);
        selectedPiece.moveTo(i, j, gameController.getBoard());
        logger.log(Level.INFO, "Piece " + selectedPiece + " was moved to " + gameController.getBoard()[i][j]);
        if (gameController.rabbitReachedEnd() || gameController.noRabbitsLeft() || legalMovesCheck()) {
            endGame();
        }
    }

    /**
     * Moves a piece to a specific cell.
     *
     * @param rectangle      the target rectangle
     * @param selectedPiece  the selected piece
     * @param selectedImage  the selected image
     */
    private void moveFigure(Rectangle rectangle, Piece selectedPiece, ImageView selectedImage) {
        int[] pos = gameDisplay.getSelectedPosition(rectangle);
        if (selectedPiece != null) {
            placeOnCell(pos[0], pos[1], selectedImage, selectedPiece);
            if (selectedPiece.isGold()) {
                gameDisplay.setHighlightEffect(selectedImage, gameDisplay.shadowEffectGold);
            }
            else{
                gameDisplay.setHighlightEffect(selectedImage, gameDisplay.shadowEffectSilver);
            }
            gameDisplay.setHighlightEffect(rectangle, null);
            stepCounter++;
        }
    }

    /**
     * Handles push or pull move actions.
     *
     * @param p1        the first piece
     * @param im1       the first image
     * @param p2        the second piece
     * @param im2       the second image
     * @param rectangle the target rectangle
     */
    private void pushOrPull(Piece p1, ImageView im1, Piece p2, ImageView im2, Rectangle rectangle) {
        int opponentX = p1.getCurrentPositionX();
        int opponentY = p1.getCurrentPositionY();
        Rectangle opponentRectangle = gameDisplay.rectangles[opponentX][opponentY];
        moveFigure(rectangle, p1, im1);
        moveFigure(opponentRectangle, p2, im2);
    }

    /**
     * Checks if there are any legal moves available on the board.
     *
     * @return {@code true} if there are no legal moves left, {@code false} otherwise.
     */
    private boolean legalMovesCheck(){
        if(gameDisplay.panesEmptyCheck()) return  moveCounter > 3 && gameController.legalMovesEmptyCheck();
        else return false;
    }

    /**
     * It updates the game status, records the final move, and displays the game result.
     * It also stops the game timer.
     */
    private void endGame(){
        logger.log(Level.FINE, "Game ended");
        String lastMove = String.valueOf(moveCounter/2+1);
        if(gameController.getGoldPlayer().isMyTurn()) lastMove+="g ";
        else lastMove+="s ";
        if(!move.equals(lastMove)){
            gameController.getMoves().add(move);
            gameController.boardToImage();
        }else if(gameController.rabbitReachedEnd()){
            move+="lost";
            gameController.getMoves().add(move);
        }
        boolean won = gameController.getGoldPlayer().isWon();
        gameDisplay.gameEnded(won, gameController.getMoves(), gameController.getMovesImages(), gameController.loosingWinningCause);
        gameController.stopTimer();
    }

    /**
     * Handles button click events.
     *
     * @param b            the button
     * @param buttonIndex  the button index
     */
    private void onButtonClick(Button b, int buttonIndex) {

        b.setOnMouseClicked(mouseEvent -> {
            if(buttonIndex == FINISH_BUTTON_INDEX) {
                if (stepCounter > 0) stepCounter = MAX_MOVES;
                else{
                    popUpMessage = "No move was made! At least one required!";
                    gameDisplay.popUpWindow(popUpMessage);
                }
            } else if(buttonIndex == RESIGN_BUTTON_INDEX) {
                if(gameController.getGoldPlayer().isMyTurn()){
                    gameController.getSilverPlayer().setWon();
                }else gameController.getGoldPlayer().setWon();
                gameController.loosingWinningCause = "Opponent resigned!";
                move+="resign";
                endGame();
            }else{
                button = buttonIndex;
            }
            b.setEffect(new Glow(0.4));
            logger.log(Level.INFO, "Button "+ b.getText() + " was clicked");
        });
        b.setOnMouseExited(mouseEvent -> b.setEffect(gameDisplay.shadowEffectButton));
    }

    /**
     * Handles the default placement button click event.
     *
     * @param b         the button
     * @param isMyTurn  whether it is the player's turn
     */
    private void onDefaultPlacementClick(Button b, boolean isMyTurn) {
        if(isMyTurn) {
            b.setOnMouseClicked(mouseEvent -> {
                b.setVisible(false);
                defaultPlacementClicked = true;
            logger.log(Level.INFO, "Button "+ b.getText() + " was clicked");
            });
        }
    }


    @Override
    public void run() {
        double updateInterval = (double) 1000000000 / 60;
        double nextUpdateTime = System.nanoTime() + updateInterval;
        while (gameThread != null) {
            if (gameController.playerWonCheck()) {
                break;
            }
            gameController.calculateLegalMoves();
//            this means end of the move: steps are recorded, turn changes,
//            stepCounter and move reset
            if (stepCounter == MAX_MOVES) {
                gameController.boardToImage();
                moveCounter++;
                gameController.change_turn();
                stepCounter = 0;
                resetMove();
            }
//          panesEmptyCheck indicates that all the pieces are on the board,
//          therefore all the checks are processed and the buttons become clickable
            if (gameDisplay.panesEmptyCheck()) {
                gameController.killCheck();
                gameController.freezeCheck();
                onButtonClick(gameDisplay.push_button, PUSH_BUTTON_INDEX);
                onButtonClick(gameDisplay.pull_button, PULL_BUTTON_INDEX);
                onButtonClick(gameDisplay.finish_button, FINISH_BUTTON_INDEX);
                onButtonClick(gameDisplay.resign_button, RESIGN_BUTTON_INDEX);
            } else {
                onDefaultPlacementClick(gameDisplay.defaultPlacementGold, gameController.getGoldPlayer().isMyTurn());
                onDefaultPlacementClick(gameDisplay.defaultPlacementSilver, gameController.getSilverPlayer().isMyTurn());
            }

            if (gameController.getGoldPlayer().isMyTurn()) {
                placePiecesOnTheBoard(gameDisplay.getGoldenPane(), 0, 0, 2, gameDisplay.defaultPlacementGold);
                setEffects(gameDisplay.goldImages, gameDisplay.silverImages, gameDisplay.shadowEffectGold, gameDisplay.shadowEffectSilver);
            } else {
                if (this.isHuman) {
                    placePiecesOnTheBoard(gameDisplay.getSilverPane(), 16, 6, 8, gameDisplay.defaultPlacementSilver);
                    setEffects(gameDisplay.silverImages, gameDisplay.goldImages, gameDisplay.shadowEffectSilver, gameDisplay.shadowEffectGold);
                } else {
                    gameDisplay.defaultPlacementSilver.setVisible(false);
                    if (!gameDisplay.panesEmptyCheck()) {
                        Platform.runLater(() -> defaultPlacePieces(16, 6, 8));
                        stepCounter = MAX_MOVES;
                    }
                    if (stepCounter != MAX_MOVES) {
                        Platform.runLater(this::computerMakesMove);
                    }
                    for (ImageView im : gameDisplay.goldImages) {
                        im.setOnMouseClicked(null);
                        im.setEffect(gameDisplay.shadowEffectGold);
                    }
                }
            }
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    rectangleSet(gameDisplay.rectangles[i][j]);
                }
            }
            for (int i = 0; i < 32; i++) {
                Piece p = gameController.getPiece(i);
                ImageView imageView = gameDisplay.images[i];
                int x = p.getCurrentPositionX();
                int y = p.getCurrentPositionY();
                if (p.isKilled() && x > -1 && y > -1) {
                    Platform.runLater(() -> gameDisplay.stackPanes[x][y].getChildren().remove(imageView));
                    gameController.getBoard()[x][y].setOccupied(false);
                    logger.log(Level.FINE, "Killed piece's image was removed from the board");
                }
            }

            try {
                double remainingTime = nextUpdateTime - System.nanoTime();
                remainingTime /= 1000000;
                if (remainingTime < 0) {
                    remainingTime = 0;
                }
                Thread.sleep((long) remainingTime);
                nextUpdateTime += updateInterval;
            } catch (InterruptedException e) {
                gameThread.interrupt();
                logger.log(Level.FINEST, "Error in thread");
            }
        }
    }
    private void resetMove() {
        gameController.getMoves().add(move);
        move = String.valueOf(moveCounter / 2 + 1);
        if (gameController.getGoldPlayer().isMyTurn()) {
            move += "g ";
        } else {
            move += "s ";
        }
    }

    /**
     * Sets the effects for images based on the current button state and whose turn it is.
     *
     * @param images1 the first set of images
     * @param images2 the second set of images
     * @param effect1 the first effect
     * @param effect2 the second effect
     */
    private void setEffects(ImageView[] images1, ImageView[] images2, Effect effect1, Effect effect2) {
        if (button == NO_BUTTON) {
//            only for one player(who's turn it is) it will be possible to highlight pieces and click on the
            for (ImageView im : images1) {
                imageSet(im, images1, effect1);
            }
//            the same player cannot control opponent's figures, unless it's pull/push move
            for (ImageView im : images2) {
                im.setOnMouseClicked(null);
                im.setEffect(effect2);
            }
//            pull/push move - figures will get highlighted or will glow on click regardless who's turn it is
        } else if (button == PUSH_BUTTON_INDEX || button == PULL_BUTTON_INDEX) {
            for (ImageView im : gameDisplay.silverImages) {
                imageSet(im, gameDisplay.images, gameDisplay.shadowEffectSilver);
            }
            for (ImageView im : gameDisplay.goldImages) {
                imageSet(im, gameDisplay.images, gameDisplay.shadowEffectGold);
            }
        }
    }

    /**
     * If button was clicked, places pieces on the game board in their default positions based on the given parameters.
     *
     * @param flowPane              the flowPane containing images to be placed
     * @param counter               the counter for iterating over the pieces
     * @param j_start               the starting column index
     * @param j_end                 the ending column index
     * @param defaultPlacement      the button which was used to place pieces on the board
     */
    private void placePiecesOnTheBoard(FlowPane flowPane, int counter, int j_start, int j_end, Button defaultPlacement){
        if (!flowPane.getChildren().isEmpty() && defaultPlacementClicked) {
            Platform.runLater(() -> defaultPlacePieces(counter, j_start, j_end));
            defaultPlacementClicked = false;
            stepCounter = MAX_MOVES;
        }else if(flowPane.getChildren().isEmpty()){
            defaultPlacement.setVisible(false);
        }
    }

    /**
     * Places pieces on the game board in their default positions based on the given parameters.
     *
     * @param counter               the counter for iterating over the pieces
     * @param j_start               the starting column index
     * @param j_end                 the ending column index
     */
    private void defaultPlacePieces(int counter, int j_start, int j_end) {
        int counterForSeparateImages = 0;
        int j;
        for (int i = 0; i < 8; i++) {
            j = j_start;
            while (j < j_end) {
                Piece piece = gameController.getPiece(counter);
                if (!gameController.getBoard()[i][j].isOccupied()) {
                    if (gameDisplay.getGoldenPane().getChildren().contains(gameDisplay.images[counter]) ||
                            gameDisplay.getSilverPane().getChildren().contains(gameDisplay.images[counter])) {
                        placeOnCell(i, j, gameDisplay.images[counter], piece);
                        gameDisplay.getGoldenPane().getChildren().remove(gameDisplay.images[counterForSeparateImages]);
                        gameDisplay.getSilverPane().getChildren().remove(gameDisplay.images[counterForSeparateImages]);
                        j++;
                    }
                    counter++;
                    counterForSeparateImages++;
                }else {
                    j++;
                }
            }
        }
    }

    /**
     * Allows the computer player to make a move by selecting a random legal move from the available options.
     * The number of moves made by the computer is determined by a random counter.
     */
    private void computerMakesMove() {
        Random rand = new Random();
        int randomMoveCounter = rand.nextInt(5);
        for (int i = 0; i < randomMoveCounter; i++) {
            if (!gameController.getLegalMovesSilver().isEmpty()) {
                int randomMove = rand.nextInt(gameController.getLegalMovesSilver().size());
                Move m = gameController.getLegalMovesSilver().get(randomMove);
                Piece piece;
                if (stepCounter == MAX_MOVES) {
                    break;
                } else {
                    switch (m.moveType) {
                        case Move.ORDINARY_MOVE_INDEX:
                            piece = gameController.getPiece(m.pieceIndex);
                            moveFigure(gameDisplay.rectangles[m.x][m.y], piece, gameDisplay.images[m.pieceIndex]);
                            break;
                        case Move.PUSH_MOVE_INDEX:
                            if (randomMoveCounter - i == 2) {
                                piece = gameController.getPiece(gameController.getSelectedPiece(m.pushPullToX, m.pushPullToY));
                                Piece piece2 = gameController.getPiece(m.pieceIndex);
                                pushOrPull(piece, gameDisplay.images[gameController.getSelectedPiece(m.pushPullToX, m.pushPullToY)],
                                        piece2, gameDisplay.images[m.pieceIndex], gameDisplay.rectangles[m.pushPullToX][m.pushPullToY]);
                            }
                            break;
                        case Move.PULL_MOVE_INDEX:
                            if (randomMoveCounter - i == 2) {
                                piece = gameController.getPiece(m.pieceIndex);
                                Piece piece2 = gameController.getPiece(gameController.getSelectedPiece(m.pushPullToX, m.pushPullToY));
                                pushOrPull(piece2, gameDisplay.images[m.pieceIndex], piece, gameDisplay.images[gameController.getSelectedPiece(m.pushPullToX, m.pushPullToY)],
                                        gameDisplay.rectangles[m.pushPullToX][m.pushPullToY]);
                            }
                            break;
                    }
                }
            }
        }
    }
}