package chess;

public class PieceMovesCalculator {

    protected boolean isOnBoard(int row, int column) {
        return row >= 1
                && row <= 8
                && column >= 1
                && column <= 8;
    }
}
