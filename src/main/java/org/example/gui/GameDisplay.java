package org.example.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.StageStyle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

public class GameDisplay {
    private static final int CELL_SIZE = 75;
    private final ShadowEffect shadowEffect = new ShadowEffect();
    public final Effect shadowEffectSilver = shadowEffect.effect(Color.GREY, 4, 3.0f, 2.0f, 2.0f);
    public final Effect shadowEffectGold = shadowEffect.effect(Color.web("#AD8E27"), 4, 3.0f, 2.0f, 2.0f);
    public final Effect shadowEffectButton = shadowEffect.effect(Color.web("#FFB7B7"), 5, 3.0f, 2.0f, 2.0f);

    private static final Function<Node, Effect> HIGHLIGHT_EFFECT = node -> new Glow(0.5);
    public final ImageView m_gold = new ImageView("camel-gold.png");
    public final ImageView e_gold = new ImageView("elephant-gold.png");
    public final ImageView h1_gold = new ImageView("horse-gold.png");
    public final ImageView h2_gold = new ImageView("horse-gold.png");
    public final ImageView d1_gold = new ImageView("dog-gold.png");
    public final ImageView d2_gold = new ImageView("dog-gold.png");
    public final ImageView c1_gold = new ImageView("cat-gold.png");
    public final ImageView c2_gold = new ImageView("cat-gold.png");
    public final ImageView r1_gold = new ImageView("rabbit-gold.png");
    public final ImageView r2_gold = new ImageView("rabbit-gold.png");
    public final ImageView r3_gold = new ImageView("rabbit-gold.png");
    public final ImageView r4_gold = new ImageView("rabbit-gold.png");
    public final ImageView r5_gold = new ImageView("rabbit-gold.png");
    public final ImageView r6_gold = new ImageView("rabbit-gold.png");
    public final ImageView r7_gold = new ImageView("rabbit-gold.png");
    public final ImageView r8_gold = new ImageView("rabbit-gold.png");
    public final ImageView m_silver = new ImageView("camel-silver.png");
    public final ImageView e_silver = new ImageView("elephant-silver.png");
    public final ImageView h1_silver = new ImageView("horse-silver.png");
    public final ImageView h2_silver = new ImageView("horse-silver.png");
    public final ImageView d1_silver = new ImageView("dog-silver.png");
    public final ImageView d2_silver = new ImageView("dog-silver.png");
    public final ImageView c1_silver = new ImageView("cat-silver.png");
    public final ImageView c2_silver = new ImageView("cat-silver.png");
    public final ImageView r1_silver = new ImageView("rabbit-silver.png");
    public final ImageView r2_silver = new ImageView("rabbit-silver.png");
    public final ImageView r3_silver = new ImageView("rabbit-silver.png");
    public final ImageView r4_silver = new ImageView("rabbit-silver.png");
    public final ImageView r5_silver = new ImageView("rabbit-silver.png");
    public final ImageView r6_silver = new ImageView("rabbit-silver.png");
    public final ImageView r7_silver = new ImageView("rabbit-silver.png");
    public final ImageView r8_silver = new ImageView("rabbit-silver.png");

    public final ImageView[] goldImages = {m_gold, e_gold, h1_gold, d1_gold, c1_gold, h2_gold, d2_gold,
            c2_gold, r1_gold, r2_gold, r3_gold, r4_gold, r5_gold, r6_gold, r7_gold, r8_gold};
    public final ImageView[] silverImages = {m_silver, e_silver, h1_silver, d1_silver, c1_silver, h2_silver,
            d2_silver, c2_silver, r1_silver, r2_silver, r3_silver, r4_silver, r5_silver, r6_silver, r7_silver, r8_silver};
    public final ImageView[] images = {m_gold, e_gold, h1_gold, d1_gold, c1_gold, h2_gold, d2_gold, c2_gold, r1_gold,
            r2_gold, r3_gold, r4_gold, r5_gold, r6_gold, r7_gold, r8_gold, m_silver, e_silver, h1_silver, d1_silver,
            c1_silver, h2_silver, d2_silver, c2_silver, r1_silver, r2_silver, r3_silver, r4_silver, r5_silver, r6_silver, r7_silver, r8_silver};
    public final Rectangle[][] rectangles = new Rectangle[8][8];


    public final Button pull_button = new Button("PULL");
    public final Button push_button = new Button("PUSH");
    public final Button finish_button = new Button("FINISH MOVE");
    public final Button resign_button = new Button("RESIGN");
    public final Button defaultPlacementSilver = new Button("DEFAULT PLACEMENT");
    public final Button defaultPlacementGold = new Button("DEFAULT PLACEMENT");
    public Label time = new Label("TIME");
    private final BorderPane pane;
    private final GridPane table;
    private final FlowPane silver_pane;
    private final FlowPane gold_pane;
    public final StackPane[][] stackPanes;

    public GameDisplay() {
        pane = new BorderPane();
        stackPanes = new StackPane[8][8];
        table = drawBoard();
        silver_pane = addFlowPane(m_silver, e_silver, h1_silver, h2_silver, d1_silver, d2_silver, c1_silver,
                c2_silver, r1_silver, r2_silver, r3_silver, r4_silver, r5_silver, r6_silver, r7_silver, r8_silver);
        gold_pane = addFlowPane(m_gold, e_gold, h1_gold, h2_gold, d1_gold, d2_gold, c1_gold, c2_gold,
                r1_gold, r2_gold, r3_gold, r4_gold, r5_gold, r6_gold, r7_gold, r8_gold);
        for (ImageView im : silverImages){
            im.setEffect(shadowEffectSilver);
        }
        for (ImageView im : goldImages){
            im.setEffect(shadowEffectGold);
        }
    }

    public BorderPane display(){
        pane.setPrefSize(650, 800);
        pane.setTop(customiseTime());
        pane.setRight(placeFiguresPane(gold_pane, defaultPlacementGold));
        pane.setLeft(placeFiguresPane(silver_pane, defaultPlacementSilver));
        pane.setCenter(tablePane());
        pane.setBottom(buttons());
        return pane;
    }


    /**
     * Places the pieces and the placement button in a VBox.
     *
     * @param figures the flow pane containing the pieces
     * @param placementButton the button for default placement
     * @return the VBox containing the pieces and the button
     */
    private VBox placeFiguresPane(FlowPane figures, Button placementButton){
        customiseButtons(placementButton);
        VBox vBox = new VBox();
        vBox.getChildren().addAll(figures, placementButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(0, 20, 30, 20));
        return vBox;
    }

    /**
     * Creates a FlowPane containing the specified ImageViews.
     *
     * @param m the camel image
     * @param e the elephant image
     * @param h1 the first horse image
     * @param h2 the second horse image
     * @param d1 the first dog image
     * @param d2 the second dog image
     * @param c1 the first cat image
     * @param c2 the second cat image
     * @param r1 the first rabbit image
     * @param r2 the second rabbit image
     * @param r3 the third rabbit image
     * @param r4 the fourth rabbit image
     * @param r5 the fifth rabbit image
     * @param r6 the sixth rabbit image
     * @param r7 the seventh rabbit image
     * @param r8 the eighth rabbit image
     * @return the FlowPane containing the images
     */
    private FlowPane addFlowPane(ImageView m, ImageView e, ImageView h1, ImageView h2, ImageView d1, ImageView d2,
                                 ImageView c1, ImageView c2, ImageView r1, ImageView r2, ImageView r3, ImageView r4,
                                 ImageView r5, ImageView r6, ImageView r7, ImageView r8) {
        FlowPane flow = new FlowPane();
        flow.setVgap(4);
        flow.setHgap(4);
        flow.setMaxSize(132, 512);
        flow.getChildren().addAll(m, e, h1, h2, d1, d2, c1, c2, r1, r2, r3, r4, r5, r6, r7, r8);
        return flow;
    }


    private BorderPane tablePane(){
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(35, 10, 0, 0));
        vBox.setSpacing(60);
        for(int i = 8; i > 0; i--){
            Text text = new Text();
            text.setFont(newFont("/MontereyFLF.ttf", 15));
            text.setTextAlignment(TextAlignment.CENTER);
            text.setText(String.valueOf(i));
            vBox.getChildren().add(8-i, text);
        }
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(0, 0, 10, 52));
        hBox.setSpacing(68);
        for(int i = 0; i < 8; i++){
            Text text = new Text();
            text.setFont(newFont("/MontereyFLF.ttf", 15));
            text.setTextAlignment(TextAlignment.CENTER);
            text.setText(String.valueOf((char)('A'+i)));
            hBox.getChildren().add(i, text);
        }
        BorderPane tablePane = new BorderPane();
        tablePane.setCenter(table);
        tablePane.setLeft(vBox);
        tablePane.setBottom(hBox);
        return tablePane;
    }

    /**
     * Creates and returns a GridPane representing the game board.
     *
     * @return the GridPane representing the game board
     */
    private GridPane drawBoard(){
        GridPane table = new GridPane();
        table.setMaxSize(640,640);
        for(int k = 0; k < 8; k++){
            for(int l = 0; l < 8; l++){
                rectangles[k][l] = new Rectangle(CELL_SIZE, CELL_SIZE);
                if((k == 5 || k == 2) && (l == 2 || l == 5)){
                    Color c = Color.web("#FFD5D5");
                    rectangles[k][l].setFill(c);
                }else if (l < 2){
                    Color c = Color.web("#FFF8E0");
                    rectangles[k][l].setFill(c);
                }else if (l > 5){
                    Color c = Color.web("#EFEFEF");
                    rectangles[k][l].setFill(c);
                }else{
                    rectangles[k][l].setFill(Color.WHITE);
                }
                rectangles[k][l].setStroke(Color.GREY);
                stackPanes[k][l] = new StackPane(rectangles[k][l]);
                table.add(stackPanes[k][l], k, 7-l);
            }
        }
        Color color = Color.web("#b1b1b1");
        table.setEffect(shadowEffect.effect(color, 7, 5.0f, 4.0f, 4.0f));
        return table;
    }
    public FlowPane getGoldenPane(){
        return this.gold_pane;
    }

    public FlowPane getSilverPane(){
        return this.silver_pane;
    }

    private void customiseButtons(Button button){
        button.setPrefSize(200, 40);
        button.setStyle("-fx-background-color: #FFD5D5");
        button.setFont(newFont("/MontereyFLF.ttf", 15));
        button.setEffect(shadowEffectButton);
    }

    /**
     * Creates and returns a HBox containing the game control buttons.
     *
     * @return the HBox containing the game control buttons
     */
    private HBox buttons(){
        customiseButtons(push_button);
        customiseButtons(pull_button);
        customiseButtons(finish_button);
        customiseButtons(resign_button);
        HBox hBoxButtons = new HBox();
        hBoxButtons.setSpacing(20);
        hBoxButtons.setPadding(new Insets(10, 0, 20, 130));
        hBoxButtons.getChildren().addAll(push_button, pull_button, finish_button, resign_button);
        return hBoxButtons;
    }

    /**
     * Creates and returns a StackPane containing the game time display.
     *
     * @return the StackPane containing the game time display
     */
    private StackPane customiseTime(){
        time.setFont(newFont("/MontereyFLF-Bold.ttf", 15));
        Rectangle timeRect = new Rectangle();
        StackPane timePane = new StackPane(timeRect, time);
        timePane.setPadding(new Insets(15, 20, 15, 40));
        time.setPadding(new Insets(5));
        time.setStyle("-fx-border-color: grey; -fx-border-width: 2; -fx-border-style: solid");
        time.setEffect(shadowEffect.effect(Color.web("#b1b1b1"), 5, 3.0f, 2.0f, 2.0f));
        return timePane;
    }

    public BorderPane getPane() {
        return pane;
    }

    /**
     * Displays a pop-up window with the specified text.
     *
     * @param text the text to display in the pop-up window
     */
    public void popUpWindow(String text){
        Label label = new Label(text);
        label.setPadding(new Insets(10, 10, 5, 10));
        label.setMinWidth(300);
        label.setMinHeight(30);
        label.setAlignment(Pos.TOP_LEFT);
        label.setFont(newFont("/MontereyFLF.ttf", 20));
        Alert a = new Alert(Alert.AlertType.NONE);
        DialogPane dialogPane = new DialogPane();
        dialogPane.setContent(label);
        dialogPane.setStyle("-fx-background-color: transparent");
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(okButtonType);
        a.setDialogPane(dialogPane);
        a.initStyle(StageStyle.UNDECORATED);
        Button okButton = ((Button) a.getDialogPane().lookupButton(okButtonType));
        customisePopUpButton(okButton);
        a.show();
    }

    private Font newFont(String name, int size){
        return Font.loadFont(getClass().getResourceAsStream(name), size);
    }

    public void setGlowEffect(Node node){
        node.setEffect(new Blend(BlendMode.ADD, node.getEffect(), HIGHLIGHT_EFFECT.apply(node)));
        node.setOnMouseExited(null);
        node.setOnMouseEntered(null);
    }
    public void setHighlightEffect(Node node, Effect effect){
        node.setEffect(effect);
        node.setOnMouseExited(new HighlightEventHandler(node, false, effect));
        node.setOnMouseEntered(new HighlightEventHandler(node, true, effect));
    }
    public void removeEffect(Node node, Effect effect){
        node.setEffect(effect);
        node.setOnMouseEntered(null);
        node.setOnMouseExited(null);
    }

    /**
     * Gets the position of the specified rectangle in the grid.
     *
     * @param selectedRect the rectangle to find the position of
     * @return an array containing the row and column indices of the rectangle
     */
    public int[] getSelectedPosition(Rectangle selectedRect){
        int[] position = new int[2];
        for (int k = 0; k < 8; k++) {
            for (int l = 0; l < 8; l++) {
                if (rectangles[k][l] == selectedRect) {
                    position[0] = k;
                    position[1] = l;
                    break;
                }
            }
        }
        return position;
    }

    /**
     * Gets the index of the specified image in the images array.
     *
     * @param selectedImage the image to find the index of
     * @return the index of the image, or -1 if the image is not found
     */
    public int getImageIndex(ImageView selectedImage){
        for(int i = 0; i < images.length; i++){
            if(images[i] == selectedImage){
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks if both the gold and silver panes are empty.
     *
     * @return true if both panes are empty, false otherwise
     */
    public boolean panesEmptyCheck(){
        return gold_pane.getChildren().isEmpty() && silver_pane.getChildren().isEmpty();
    }

    /**
     * Displays a dialog indicating that the game has ended and provides an option to save the moves.
     *
     * @param goldWon whether the gold player won the game
     * @param moves the list of moves made during the game
     */
    public void gameEnded(boolean goldWon, ArrayList<String> moves, ArrayList<String[][]> movesImages, String winningCause){
        Dialog<Object> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.UTILITY);
        DialogPane dialogPane = new DialogPane();
        dialogPane.setMinSize(400, 200);
        dialogPane.setContent(gameEndedDisplay(goldWon, moves, movesImages, winningCause));
        dialogPane.setStyle("-fx-border-color: black; -fx-background-color: #FFF8E0");
        dialog.setDialogPane(dialogPane);
        dialog.show();
    }


    /**
     * Creates a VBox layout to display the game end screen, including a message, an image, and a button to save moves.
     *
     * @param goldWon a boolean indicating whether the gold player won the game
     * @param moves an ArrayList of Strings containing the moves made during the game
     * @return a VBox containing the game end display elements
     */
    private VBox gameEndedDisplay(boolean goldWon, ArrayList<String> moves, ArrayList<String[][]> movesImages, String winningCause){
        VBox pane = new VBox();
        Label label = new Label("CONGRATS! YOU WON!\n" + winningCause);
        label.setFont(Font.loadFont(getClass().getResourceAsStream("/MontereyFLF-Bold.ttf"), 20));
        label.setAlignment(Pos.TOP_CENTER);
        label.setPadding(new Insets(30, 0, 0, 0));
        ImageView crownImageView;
        if(goldWon){
            crownImageView = new ImageView("crown_gold.png");
            crownImageView.setEffect(shadowEffect.effect(Color.web("#AD8E27"), 4, 3.0f, 2.0f, 2.0f));
        }
        else{
            crownImageView = new ImageView("crown_silver.png");
            crownImageView.setEffect(shadowEffect.effect(Color.GREY, 4, 3.0f, 2.0f, 2.0f));
        }
        Button getMovesButton = new Button("get moves and exit");
        customisePopUpButton(getMovesButton);
        getMovesButton.setOnMouseClicked(mouseEvent -> {
            String mainFileName = "moves";
            try (BufferedWriter buffWrite = new BufferedWriter(new FileWriter(mainFileName + ".txt")))  {
                String result = "";
                if(goldWon) result+="1-0";
                else result+="0-1";
                buffWrite.write("Event: Casual Game\n");
                buffWrite.write("Site: Prague, Czech Republic\n");
                buffWrite.write("Round: ?\n");
                buffWrite.write("White: Gold player\n");
                buffWrite.write("Black: Silver player\n");
                buffWrite.write("Result: " +result+ "\n");
                for(String move : moves) {
                    buffWrite.write(move + "\n");
                }
                buffWrite.write("\n");
                for (int i = 0; i < movesImages.size(); i++) {
                    buffWrite.write(moves.get(i)+"\n");
                    for (String[] strings : movesImages.get(i)) {
                        for (String string : strings) {
                            buffWrite.write(string);
                        }
                        buffWrite.write("\n");
                    }
                    buffWrite.write("\n");
                }

            } catch (IOException ex) {
                System.out.println("There is an error with the" + mainFileName);
            }
            Platform.exit();
        });
        pane.setAlignment(Pos.CENTER);
        pane.setSpacing(4);
        pane.setMinSize(400, 200);
        pane.getChildren().addAll(label, crownImageView, getMovesButton);
        pane.setStyle("-fx-background-color: transparent");
        return pane;
    }

    /**
     * Customizes the appearance of a pop-up button.
     *
     * @param button the button to customize
     */
    private void customisePopUpButton(Button button){
        button.setMinSize(100, 40);
        button.setStyle("-fx-background-color: #EFEFEF; -fx-background-radius: 0; -fx-border-color: black");
        button.setFont(Font.loadFont(getClass().getResourceAsStream("/MontereyFLF.ttf"), 20));
        Effect buttonShadow = shadowEffect.effect(Color.GREY, 5, 3.0f, 2.0f, 2.0f);
        button.setEffect(buttonShadow);
        button.setOnMouseEntered(mouseEvent -> button.setEffect(new Glow(0.2)));
        button.setOnMouseExited(mouseEvent -> button.setEffect(buttonShadow));
    }

}
