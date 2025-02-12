package org.example;

import org.example.engine.Cell;
import org.example.engine.GameController;
import org.example.engine.Move;
import org.example.engine.Player;
import org.example.engine.pieces.Piece;
import org.example.gui.GameDisplay;
import org.example.gui.HighlightEventHandler;
import org.example.gui.ShadowEffect;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerController {

    private Class<?>[] classes = {Piece.class, Player.class, GameController.class, Move.class,
            Cell.class, GameDisplay.class, Game.class, HighlightEventHandler.class, ShadowEffect.class,
            StartScreen.class};

    public LoggerController(){}
    public void disableLoggingForClasses(boolean isDisabled) {
        if(isDisabled){
            for (Class<?> cls : classes) {
                Logger logger = Logger.getLogger(cls.getName());
                logger.setLevel(Level.OFF);
            }
        }
    }
}

