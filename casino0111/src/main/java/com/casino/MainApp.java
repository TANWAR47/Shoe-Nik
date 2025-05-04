package com.casino;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainApp extends Application {
    private final String[] symbols = { "!", "@", "#", "$", "&" };
    private final Random random = new Random();

    private final Map<String, Image> symbolImages = new HashMap<>();

    {
        symbolImages.put("!", new Image(getClass().getResourceAsStream("/airforce_black.png")));
        symbolImages.put("@", new Image(getClass().getResourceAsStream("/airforce_white.png")));
        symbolImages.put("#", new Image(getClass().getResourceAsStream("/goat.png")));
        symbolImages.put("$", new Image(getClass().getResourceAsStream("/jordan.png")));
        symbolImages.put("&", new Image(getClass().getResourceAsStream("/sports.png")));
    }

    private final ImageView[][] reels = new ImageView[3][5];
    private int credit = 1000;
    private int bet = 10;
    private Label creditLabel = new Label("Credit: $1000");
    private Label resultLabel = new Label("");

    private AudioClip spinSound;
    private AudioClip winSound;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        spinSound = new AudioClip(getClass().getResource("/spin.wav").toString());
        winSound = new AudioClip(getClass().getResource("/win.wav").toString());

        primaryStage.setTitle("Slot Machine");


        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 20;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                ImageView imageView = new ImageView(symbolImages.get("!"));
                imageView.setFitWidth(120);
                imageView.setFitHeight(120);
                reels[row][col] = imageView;
                grid.add(imageView, col, row);
            }
        }

        // New components
        TextField betInput = new TextField();
        betInput.setPromptText("Enter Bet");
        betInput.setPrefWidth(100);

        Button setBetButton = new Button("Set Bet");
        setBetButton.setOnAction(e -> {
            try {
                int newBet = Integer.parseInt(betInput.getText());
                if (newBet > 0) {
                    bet = newBet;
                    resultLabel.setText("Bet set to $" + bet);
                } else {
                    resultLabel.setText("Bet must be positive!");
                }
            } catch (NumberFormatException ex) {
                resultLabel.setText("Invalid bet amount!");
            }
        });

        Button spinButton = new Button("Spin ");
        spinButton.setOnAction(e -> spinReels());

        Button attendantButton = new Button("Attendant ➕");
        attendantButton.setOnAction(e -> {
            credit += 100;
            creditLabel.setText("Credit: $" + credit);
        });

        creditLabel.setFont(Font.font(18));
        creditLabel.setStyle("-fx-text-fill: lightgreen;");
        resultLabel.setFont(Font.font(16));
        resultLabel.setStyle("-fx-text-fill: yellow;");

        Label headingLabel = new Label("SHOE NIK");
        headingLabel.setFont(Font.font("Arial", 36));
        headingLabel.setStyle("-fx-text-fill: white;");
        headingLabel.setAlignment(Pos.CENTER);

        Label disclaimerLabel = new Label("MINIMUM THREE SIMILAR SYMBOLS MAKES WIN UPTO FIVE. WIN IS AWARDED TO A PLAYER AS OF NUMBER OF SIMILAR SYMBOLS MULTIPLIES BET. " +
                "ALL WINS ARE FOR COMBINATIONS OF A KIND. ALL WINS ARE FOR ADJACENT COMBINATIONS FROM LEFT TO RIGHT OR RIGHT TO LEFT STARTING WITH THE LEFTMOST / " +
                "RIGHTMOST REEL. HIGHEST WIN ONLY PER PLAYED LINE. THE GAME IS PLAYED WITH FIXED 5 LINES. ALL SYMBOLS HAVE SAME VALUE. MALFUNCTION VOID ALL PAYS AND PLAYS. " );


        disclaimerLabel.setFont(Font.font(14));
        disclaimerLabel.setStyle("-fx-text-fill: white;");
        disclaimerLabel.setAlignment(Pos.CENTER_RIGHT);

        // Button layout
        HBox buttonRow = new HBox(10, betInput, setBetButton, spinButton, attendantButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);


  //      root.getChildren().addAll(headingLabel, grid, creditLabel, buttonRow, resultLabel, disclaimerLabel);


        // Load and configure winlines image
        Image winlinesImage = new Image(getClass().getResourceAsStream("/winlines.png"));
        ImageView winlinesImageView = new ImageView(winlinesImage);
        winlinesImageView.setFitWidth(500);
        winlinesImageView.setPreserveRatio(true);

// Add label above the image
        Label winLinesLabel = new Label("\t\t\t\t Win Lines");
        winLinesLabel.setFont(Font.font(24));
        winLinesLabel.setStyle("-fx-text-fill: white;");

// VBox for label + image
        VBox imageBox = new VBox(5, winLinesLabel, winlinesImageView);
        imageBox.setAlignment(Pos.TOP_LEFT);

// Wrap disclaimer in a VBox for alignment
        VBox disclaimerBox = new VBox(disclaimerLabel);
        disclaimerBox.setAlignment(Pos.CENTER_RIGHT);
        disclaimerBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(disclaimerBox, Priority.ALWAYS);

// Bottom section: image on left, disclaimer on right
        HBox bottomSection = new HBox(20, imageBox, disclaimerBox);
        bottomSection.setAlignment(Pos.CENTER_LEFT);
        bottomSection.setPrefWidth(Double.MAX_VALUE);

// Add all to root
        root.getChildren().addAll(headingLabel, grid, creditLabel, buttonRow, resultLabel, bottomSection);

        disclaimerLabel.setWrapText(true);


        Scene scene01 = new Scene(root, 900, 700);
        primaryStage.setScene(scene01);
        primaryStage.setMaximized(true); // Maximize the window
        primaryStage.show();
    }

    private void spinReels() {
        if (credit < bet) {
            resultLabel.setText("Not enough credit!");
            return;
        }

        spinSound.play();

        String[][] result = new String[3][5];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                String symbol = symbols[random.nextInt(symbols.length)];
                result[row][col] = symbol;
                reels[row][col].setImage(symbolImages.get(symbol));
            }
        }

        int win = calculateWin(result);
        credit -= bet;
        credit += win;
        creditLabel.setText("Credit: $" + credit);

        if (win > 0) {
            winSound.play();
            resultLabel.setText("You won $" + win + "! 🎉");
        } else {
            resultLabel.setText("No win. Try again!");
        }
    }

    private int calculateWin(String[][] result) {
        int winAmount = 0;

        // Horizontal wins
        for (int row = 0; row < 3; row++) {
            String symbol = result[row][0];
            int count = 1;
            for (int col = 1; col < 5; col++) {
                if (result[row][col].equals(symbol)) {
                    count++;
                } else {
                    break;
                }
            }
            if (count >= 3) winAmount += bet * count;
        }

        // Diagonal wins
        if (result[0][0].equals(result[1][1]) && result[1][1].equals(result[2][2])) {
            winAmount += bet * 3;
        }
        if (result[0][4].equals(result[1][3]) && result[1][3].equals(result[2][2])) {
            winAmount += bet * 3;
        }

        return winAmount;
    }
}