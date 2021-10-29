module com.example.sudoku_cheat_machine {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sudoku_cheat_machine to javafx.fxml;
    exports com.example.sudoku_cheat_machine;
}