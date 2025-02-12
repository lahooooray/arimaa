package org.example.gui;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

public class ShadowEffect {
    public ShadowEffect() {
    }

    public Effect effect(Color color, double d1, double d2, double d3, double d4){
        return new DropShadow(BlurType.values()[0], color, d1, d2, d3, d4);
    }
}
