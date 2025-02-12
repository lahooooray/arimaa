package org.example.gui;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;

import java.util.function.Function;

public class HighlightEventHandler implements EventHandler<MouseEvent> {
    public static final Function<Node, Effect> HIGHLIGHT_EFFECT = node -> new Glow(0.5);
    private final Node node;
    private final boolean enter;
    private final Effect effect;
    public HighlightEventHandler(final Node node, final boolean enter, Effect effect){
        this.node = node;
        this.enter = enter;
        this.effect = effect;
    }

    @Override
    public void handle(MouseEvent mouseEvent){
        if (enter){

            node.setEffect(new Blend(BlendMode.ADD, node.getEffect(), HIGHLIGHT_EFFECT.apply(node)));
        }
        else{
            node.setEffect(effect);
        }
    }
}
