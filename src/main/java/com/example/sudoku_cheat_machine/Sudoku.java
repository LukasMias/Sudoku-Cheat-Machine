package com.example.sudoku_cheat_machine;

import java.util.*;

import static java.util.Arrays.deepEquals;

public class Sudoku {
    //-----fields
    int[][] entries = new int[9][9];
    int[][][] entryHints = new int[9][9][9];


    //----constructor
    public Sudoku() {
    }

    //----methods

    //checking for contradictions: does "num" appear twice in its associated box?
    public boolean isInBox(int num, int column, int row) {
        int minCol = column - (column % 3);
        int minRow = row - (row % 3);

        for(int i = minCol; i < minCol + 3; i++) {
            for(int j = minRow; j < minRow + 3; j++ ) {
                if(num == this.entries[i][j] & (column != i | row != j) ) {
                    return true;
                }
            }
        }
        return false;
    }

    //checking for contradictions: does "num" appear twice in its associated column?
    public boolean isInColumn(int num, int column, int row) {
        for (int j = 0; j < 9; j++) {
            if (num == this.entries[column][j] & j != row  ) {
                return true;
            }
        }
        return false;
    }

    //checking for contradictions: does "num" appear twice in its associated row?
    public boolean isInRow(int num, int column, int row) {
        for (int i = 0; i < 9; i++) {
            if (num == this.entries[i][row] & i != column) {
                return true;
            }
        }
        return false;
    }

    //checking for contradictions: for a nonzero entry, is there a contradiction?
    public boolean isContradictory(int num, int column, int row) {
        return  num != 0
                &(isInBox(num, column, row)
                |isInColumn(num, column, row)
                |isInRow(num, column, row));
    }

    //given entries, generate the hint
    public void generateHints() {
        for(int i = 0; i < 9; i ++) {
            for(int j = 0; j < 9; j++) {
                for(int k = 1; k <= 9; k++) {
                    if(isContradictory(k, i, j)) {
                        entryHints[i][j][k - 1] = 0;
                    } else {
                        entryHints[i][j][k - 1] = k;
                    }
                }
            }
        }
    }

    //set entries to zero
    public void reset() {
        entries = new int[9][9];
        entryHints = new int[9][9][9];
    }

    //---Getters & Setters
    public void setEntry(int num, int row, int col) {
        this.entries[row][col] = num;
    }

    public int getEntry(int row, int col) {
        return this.entries[row][col];
    }

    public String getHints(int row, int col) {
        ArrayList<Integer> list = new ArrayList<>();
        for(int num : this.entryHints[row][col]) {
            if (num != 0) {
                list.add(num);
            }
        }
        String resultString = Arrays.toString(list.toArray());
        resultString = resultString.substring(1,resultString.length() - 1);
        return resultString;
    }

    //----Equals & Clone (deep copy!) override
    @Override
    public boolean equals(Object object) {
        if(!(object instanceof Sudoku)) {
            return false;
        }

        Sudoku sudoku = (Sudoku) object;

        return deepEquals(this.entries, sudoku.entries);
    }

    //clones only the entries, not the hints
    @Override
    public Sudoku clone() {
        Sudoku copy = new Sudoku();
        copy.entries = Arrays.stream(this.entries).map(int[]::clone).toArray((int[][]::new));
        return copy;
    }


}
