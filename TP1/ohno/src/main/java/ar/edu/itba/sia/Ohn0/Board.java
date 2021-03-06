package ar.edu.itba.sia.Ohn0;


import java.util.Random;

import ar.edu.itba.sia.gps.api.State;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Board implements State {

    private Cell[][] cells;
    private Integer size;

    public Board(Cell[][] cells, Integer size) {
        this.cells = new Cell[size][size];
        for(int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.cells[i][j] = cells[i][j];
            }
        }
        this.size = size;
    }

    public Integer getSize() {
        return size;
    }

    public Cell getCell(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size){
            throw new IllegalArgumentException();
        }
        return cells[row][col];
    }

    @Override
    public String getRepresentation() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                builder.append(cells[i][j].toString()).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private int adjacentReds(int row, int col) {
        int redCount = 0;
        if (row - 1 < 0 || row - 1 >= size || getCell(row - 1, col).getColor() == Color.RED) redCount++;
        if (row + 1 < 0 || row + 1 >= size || getCell(row + 1, col).getColor() == Color.RED) redCount++;
        if (col - 1 < 0 || col - 1 >= size || getCell(row, col - 1).getColor() == Color.RED) redCount++;
        if (col + 1 < 0 || col + 1 >= size || getCell(row, col + 1).getColor() == Color.RED) redCount++;
        return redCount;
    }

    public Board switchColor(int row, int col, Color newColor) {
        if(row < 0 || row >= size || col < 0 || col >= size || cells[row][col].getFixed() ||
                newColor.equals(cells[row][col].getColor())) {
            return null;
        }
        if(newColor == Color.BLUE) {
            int redCounter = adjacentReds(row, col);
            if (redCounter == 4) return null;
        }
        Board newBoard = new Board(cells, size);
        newBoard.cells[row][col] = new Cell(false, 0, newColor);
        return newBoard;
    }

    public boolean isChangeValid(int row, int col) {
        boolean value;
        int directions[][] = new int[][]{
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };

        for (int i = 0; i < 4; i++) {
            for (int j = row + directions[i][0], k = col + directions[i][1]; j < getSize() && j >= 0 && k < getSize() && k >= 0
                    && !getCell(j, k).isBlank() && !getCell(j, k).getColor().equals(Color.RED); j += directions[i][0], k += directions[i][1]) {
                if (getCell(j, k).getValue() > 0) {
                    value = isNumberCorrect(j,k);
                    if (!value) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public int missingBlues(int row, int col) {
        int value = getCell(row, col).getValue();
        int directions[][] = new int[][]{
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };
        for (int i = 0; i < 4; i++) {
            for (int j = row + directions[i][0], k = col + directions[i][1]; j < getSize() && j >= 0 && k < getSize() && k >= 0
                    && getCell(j, k).getColor().equals(Color.BLUE); j += directions[i][0], k += directions[i][1]) {
                value -= 1;
            }
        }
        return value;
    }

    /**
     * Get missing blues inside the cell's range (its value)
     * @param row
     * @param col
     * @return
     */
    public int missingVisibleBlues(int row, int col) {
        int value = getCell(row, col).getValue();
        int distance = value;
        int directions[][] = new int[][]{
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };
        for (int i = 0; i < 4; i++) {
            for (int j = row + directions[i][0], k = col + directions[i][1]; j < getSize() && j >= 0 && k < getSize() && k >= 0
                    && !getCell(j, k).getColor().equals(Color.RED) && distance > 0; j += directions[i][0], k += directions[i][1], distance--) {
                if (getCell(j, k).getColor() == Color.BLUE) {
                    value -= 1;
                }
            }
            distance = getCell(row, col).getValue();
        }
        return value;
    }
    public boolean isNumberCorrectHeuristicReparation(int row, int col) {
        int value = getCell(row, col).getValue();
        int directions[][] = new int[][]{
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };

        for (int i = 0; i < 4; i++) {
            for (int j = row + directions[i][0], k = col + directions[i][1]; j < getSize() && j >= 0 && k < getSize() && k >= 0
                    && !getCell(j, k).getColor().equals(Color.RED); j += directions[i][0], k += directions[i][1]) {
                    value -= 1;
            }
        }
        return value == 0;
    }
    private boolean isNumberCorrect(int row, int col) {
        int value = getCell(row, col).getValue();
        int adjvalue = value;
        int blanksOnDir = 0;
        int blanks = 0;
        int directions[][] = new int[][]{
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };
        for (int i = 0; i < 4; i++) {
            for (int j = row + directions[i][0], k = col + directions[i][1]; j < getSize() && j >= 0 && k < getSize() && k >= 0
                     && !getCell(j, k).getColor().equals(Color.RED); j += directions[i][0], k += directions[i][1]) {
                if(getCell(j, k).isBlank()) {
                    blanks++;
                    blanksOnDir++;
                }
                else {
                    value -= 1;
                    if(blanksOnDir == 0) {
                        adjvalue -= 1;
                    }
                }
            }
            blanksOnDir = 0;
        }
        return adjvalue == 0 || (adjvalue > 0 && blanks > 0 && value <= blanks);
    }

    public Board fillRandomly() {
        Board newBoard = new Board(cells, size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (newBoard.getCell(i, j).isBlank()) {
                    Random r = new Random();
                    Color randomColor = r.nextBoolean() ? Color.RED : Color.BLUE;
                    newBoard.cells[i][j] = new Cell(false, 0, randomColor);
                }
            }
        }
        return newBoard;
    }

    public Board fillBlue() {
        Board newBoard = new Board(cells, size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (newBoard.getCell(i, j).isBlank()) {
                    Random r = new Random();
                    newBoard.cells[i][j] = new Cell(false, 0, Color.BLUE);
                }
            }
        }
        return newBoard;
    }

    public Board fillRed() {
        Board newBoard = new Board(cells, size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (newBoard.getCell(i, j).isBlank()) {
                    newBoard.cells[i][j] = new Cell(false, 0, Color.RED);
                }
            }
        }
        return newBoard;
    }

//    public Board bruteForceSolution() {
//        char position[][] = new char[size][size];
//        Board solution = null;
//
//        for(int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                if(cells[i][j].getColor() == Color.BLUE || cells[i][j].getColor() == Color.RED) {
//                    position[i][j] = 'X';
//                }
//                else {
//                    position[i][j] = '.';
//                }
//            }
//        }
//
//        for(int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                if(cells[i][j].isBlank()) {
//                    cells[i][j] = new Cell(false, 0, Color.BLUE);
//                    position[i][j] = 'X';
//                    solution = bruteForceSolution(i, j, position);
//                    if(solution != null) {
//                        solution.fillRed();
//                        return solution;
//                    }
//                    else {
//                        cells[i][j] = new Cell(false, 0, Color.WHITE);
//                        position[i][j] = '.';
//
//                    }
//                }
//            }
//        }
//
//        return null;
//    }

//    private Board bruteForceSolution(int row, int col, char position[][]) {
//        Board solution = null;
//        System.out.println("board[" + row + "]" + "[" + col + "]\n" + this + "\n");
//        if(!isChangeValid(row, col)) {
//            System.out.println("invalid Board1\n");
//
//            return null;
//        }
//        if(isBruteForceGoal()) {
//            System.out.println("solution found\n");
//            return this;
//        }
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                if(cells[i][j].isBlank() && position[i][j] != 'X') {
//                    cells[i][j] = new Cell(false, 0, Color.BLUE);
//                    position[i][j] = 'X';
//                    solution = bruteForceSolution(i, j, position);
//                    if(solution != null) {
//                        return solution;
//                    }
//                    else {
//
//                        cells[i][j] = new Cell(false, 0, Color.WHITE);
//
//                        System.out.println("repainting board\n" + this + "\n");
//                        position[i][j] = '.';
//                    }
//                }
//            }
//        }
//        System.out.println("invalid Board2\n");
//
//        return solution;
//
//    }
//
//    private boolean isBruteForceGoal() {
//        for(int i = 0; i < size; i++) {
//            for(int j = 0; j < size; j++) {
//                if(cells[i][j].getValue() > 0) {
//                    if(!isNumberCorrectBruteForce(i, j)) {
//                        return false;
//                    }
//                }
//            }
//        }
//        return true;
//    }
//
//    private boolean isNumberCorrectBruteForce(int row, int col) {
//        int value = getCell(row, col).getValue();
//        int directions[][] = new int[][]{
//                {-1, 0},
//                {1, 0},
//                {0, -1},
//                {0, 1}
//        };
//
//        for (int i = 0; i < 4; i++) {
//            for (int j = row + directions[i][0], k = col + directions[i][1]; j < getSize() && j >= 0 && k < getSize() && k >= 0
//                    && getCell(j, k).getColor().equals(Color.BLUE); j += directions[i][0], k += directions[i][1]) {
//                value -= 1;
//            }
//        }
//        return value == 0;
//    }

    @Override
    public String toString() {
        return  getRepresentation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o != null && o.getClass() != this.getClass()) return false;
        Board obj = (Board) o;
        if (obj.size != this.size) return false;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!obj.cells[i][j].equals(cells[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        //Objects.hashCode(cells);
        HashCodeBuilder hashBuilder = new HashCodeBuilder(17, 37);
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                hashBuilder.append(cells[i][j].getColor()).append(cells[i][j].getValue());
            }
        }
        //return getRepresentation().hashCode();
        return hashBuilder.toHashCode();
    }
}
