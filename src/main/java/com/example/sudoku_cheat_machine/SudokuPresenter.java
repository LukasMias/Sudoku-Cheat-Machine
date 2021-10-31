package com.example.sudoku_cheat_machine;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class SudokuPresenter extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        //textFields is where the user inputs their sudoku numbers
        //hintLabels contain the hints that can be deduced from the entered numbers
        //ghostLabels contain the unique number that must be entered into the field, if such a number exists
        HBox root = new HBox();

        TextField[][] textFields = new TextField[9][9];
        Label[][] hintLabels = new Label[9][9];
        Label[][] ghostLabels = new Label[9][9];
        GridPane sudokuGrid = new GridPane();
        Button resetButton = new Button("Reset");

        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                resetPresentation(textFields);
            }
        });

        /*Label infoLabel = new Label(
                "Welcome to the Sudoku Cheat Machine! Enter a Sudoku into the grid on the left."+
                        "\nThe grey numbers on top of the fields indicate what numbers are possible " +
                        "\nin the field, given the current information. Once enough numbers have been" +
                        "\nentered, the program may begin to fill in uniquely determined numbers in " +
                        "\ngreen. Contradictions, meaning forbidden double numbers or configurations " +
                        "\n that lead to a field being unfillable, will be indicated in red. Have fun!");*/

        GridPane.setValignment(resetButton, VPos.BOTTOM);

        initializeAndFormatLabels(textFields, hintLabels, ghostLabels);
        restrictTextFormatting(textFields);
        addListeners(textFields, hintLabels, ghostLabels);

        addToRoot(sudokuGrid, textFields);
        addToRoot(sudokuGrid, ghostLabels);
        addToRoot(sudokuGrid, hintLabels);

        root.getChildren().add(sudokuGrid);
        root.getChildren().add(resetButton);

        resetButton.setPrefSize(200,100);
        HBox.setHgrow(sudokuGrid, Priority.ALWAYS);

        Scene scene = new Scene(root);
        stage.setResizable(false);
        scene.getStylesheets().add(SudokuPresenter.class.getResource("sudoku.css").toExternalForm());
        stage.setTitle("Sudoku Cheat Machine");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    public static void initializeAndFormatLabels(TextField[][] textFields,
                                                 Label[][] hintLabels,
                                                 Label[][] ghostLabels) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                //initialize labels
                hintLabels[i][j] = new Label("");
                textFields[i][j] = new TextField("");
                ghostLabels[i][j] = new Label("");
                textFields[i][j].setPrefSize(100,100);
                ghostLabels[i][j].setPrefSize(100,100);

                //be able to click through ghost labels & hint labels
                hintLabels[i][j].setMouseTransparent(true);
                ghostLabels[i][j].setMouseTransparent(true);

                // allow text wrapping
                hintLabels[i][j].setWrapText(true);

                //place row & column alignment within the grid
                GridPane.setConstraints(hintLabels[i][j], i, j);
                GridPane.setConstraints(textFields[i][j], i, j);
                GridPane.setConstraints(ghostLabels[i][j], i, j);

                //position hints within a grid field
                GridPane.setValignment(hintLabels[i][j], VPos.TOP);
                GridPane.setHalignment(hintLabels[i][j], HPos.CENTER);
                GridPane.setValignment(ghostLabels[i][j], VPos.CENTER);
                GridPane.setHalignment(ghostLabels[i][j], HPos.CENTER);

                //pseudoclasses for CSS
                hintLabels[i][j].pseudoClassStateChanged(PseudoClass.getPseudoClass("hintLabel"), true);
                ghostLabels[i][j].pseudoClassStateChanged(PseudoClass.getPseudoClass("ghostLabel"), true);

                //every third border line should be thicker -- mark these using pseudoclasses
                if(i % 3 == 2 && j % 3 == 2) {
                    textFields[i][j].pseudoClassStateChanged(PseudoClass.getPseudoClass("bottom-right"), true);
                }  else if(i % 3 == 2) {
                    textFields[i][j].pseudoClassStateChanged(PseudoClass.getPseudoClass("right"), true);
                }  else if(j % 3 == 2) {
                    textFields[i][j].pseudoClassStateChanged(PseudoClass.getPseudoClass("bottom"), true);
                }
            }
        }
    }

    public static void resetPresentation(TextField[][] textFields) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                textFields[i][j].setText("");
            }
        }
    }

    public static void restrictTextFormatting(TextField[][] textFields) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                textFields[i][j].setTextFormatter(
                        new TextFormatter<String>(
                                (TextFormatter.Change change) -> {
                                    String newText = change.getControlNewText();
                                    if (newText.matches("|[1-9]")) {
                                        return change;
                                    } else {
                                        return null;
                                    }
                                })
                );
            }
        }
    }

    public static void addToRoot(GridPane root, Node[][] nodes) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                root.getChildren().add(nodes[i][j]);
            }
        }
    }

    public static void addListeners(TextField[][] textFields,
                                    Label[][] hintLabels,
                                    Label[][] ghostLabels) {
        Sudoku sudoku = new Sudoku();

        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                final int current_i = i;
                final int current_j = j;
                textFields[i][j].textProperty().addListener(new ChangeListener<String>() {
                    Sudoku ghost = new Sudoku();
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldVal, String newVal) {
                        sudoku.setEntry(newVal.isEmpty() ? 0 : Integer.parseInt(newVal), current_i, current_j);
                        sudoku.generateHints();
                        ghost = sudoku.clone();
                        generateEntriesFromHints(ghost, textFields, hintLabels, ghostLabels);
                        checkContradictions(ghost, textFields);
                        ghost.reset();
                    }
                });
            }
        }
    }


    public static void generateEntriesFromHints(Sudoku ghost,
                                                TextField[][] textFields,
                                                Label[][] hintLabels,
                                                Label[][] ghostLabels) {
        while(true) {
            Sudoku ghostCopy = ghost.clone();

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    ghost.generateHints();

                    //remove hints from nonempty text boxes
                    hintLabels[i][j].setText(
                            textFields[i][j].getText().isEmpty() ? ghost.getHints(i, j) : ""
                    );

                    //if only a single hint, fill it into the ghost label
                    if (hintLabels[i][j].getText().length() == 1) {
                        ghostLabels[i][j].setText(hintLabels[i][j].getText());
                        ghost.setEntry(Integer.parseInt(hintLabels[i][j].getText()), i, j);
                    } else {
                        ghostLabels[i][j].setText("");
                    }
                }
            }
            if(ghost.equals(ghostCopy)) {
                break;
            }
        }
    }

    public static void checkContradictions(Sudoku ghost,
                                           TextField[][] textFields) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                textFields[i][j].pseudoClassStateChanged(PseudoClass.getPseudoClass("contradiction"),
                        ghost.getHints(i, j).isEmpty() |  ghost.isContradictory(ghost.getEntry(i,j), i, j));
            }
        }
    }
}