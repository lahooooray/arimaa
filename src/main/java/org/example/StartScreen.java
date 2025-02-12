package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.example.engine.GameController;
import org.example.gui.ShadowEffect;

import java.util.logging.Level;
import java.util.logging.Logger;


public class StartScreen {
    private static final Logger logger = Logger.getLogger(GameController.class.getName());
    private final ShadowEffect shadowEffect = new ShadowEffect();
    private final Font font = Font.loadFont(getClass().getResourceAsStream("/MontereyFLF-Bold.ttf"), 15);
    private final Font font2 = Font.loadFont(getClass().getResourceAsStream("/MontereyFLF.ttf"), 12);

    private VBox multiplayerButton;
    private VBox onePlayerButton;

    public StartScreen() {}

    /**
     * Creates the start display layout for the start screen, including buttons for selecting game modes.
     *
     * @return a VBox containing the start display elements
     */
    public VBox startDisplay() {
        Label chosePlayerLabel = new Label("CHOOSE GAME MODE");
        chosePlayerLabel.setFont(font);
        chosePlayerLabel.setPrefWidth(410);
        chosePlayerLabel.setAlignment(Pos.TOP_CENTER);
        multiplayerButton = customiseButton("MULTIPLAYER", "PLAY WITH A FRIEND(or not...)");
        onePlayerButton = customiseButton("ONE PLAYER", "COMPUTER WILL BE YOUR OPPONENT");
        HBox hBxButtons = new HBox(multiplayerButton, onePlayerButton);
        hBxButtons.setAlignment(Pos.CENTER);
        hBxButtons.setSpacing(10);
        VBox vBox = new VBox(chosePlayerLabel, hBxButtons);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.setStyle("-fx-border-color: black; -fx-background-color: #FFF8E0");
        vBox.setPrefSize(430, 120);
        logger.log(Level.FINEST, "Start screen displayed");
        return vBox;
    }

    /**
     * Customizes a button with specified text and applies styling and effects.
     *
     * @param str1 the main text for the button
     * @param str2 the subtext for the button
     * @return a VBox containing the customized button elements
     */
    private VBox customiseButton(String str1, String str2){
        Text tex1 = new Text(str1);
        tex1.setWrappingWidth(200);
        tex1.setFont(font);
        tex1.setTextAlignment(TextAlignment.CENTER);
        Text tex2 = new Text(str2);
        tex2.setFont(font2);
        tex2.setWrappingWidth(200);
        tex2.setTextAlignment(TextAlignment.CENTER);
        VBox vBoxTexts = new VBox(tex1, tex2);
        vBoxTexts.setSpacing(4);
        vBoxTexts.setPadding(new Insets(5, 0, 5, 0));
        vBoxTexts.setStyle("-fx-border-color: black; -fx-background-color: #EFEFEF");
        vBoxTexts.setEffect(shadowEffect.effect(Color.GREY, 5, 3.0f, 2.0f, 2.0f));
        vBoxTexts.setOnMouseEntered(mouseEvent -> vBoxTexts.setEffect(new Glow(0.5)));
        vBoxTexts.setOnMouseExited(mouseEvent -> vBoxTexts.setEffect(shadowEffect.effect(Color.GREY, 5, 3.0f, 2.0f, 2.0f)));
        return vBoxTexts;
    }

    public VBox getMultiplayerButton() {
        return multiplayerButton;
    }

    public VBox getOnePlayerButton() {
        return onePlayerButton;
    }
}
