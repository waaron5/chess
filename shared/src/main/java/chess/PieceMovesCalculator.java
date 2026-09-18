package chess;

public class PieceMovesCalculator {

    protected boolean isOnBoard(int row, int column) {

        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }

    protected boolean isEmptyOrFoe(ChessBoard board, ChessPiece piece, ChessPosition end) {

        ChessPiece otherPiece = board.getPiece(end);

        return otherPiece == null || otherPiece.getTeamColor() != piece.getTeamColor();
    }
}
