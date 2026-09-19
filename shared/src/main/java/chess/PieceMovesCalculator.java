package chess;

import java.util.Collection;

public class PieceMovesCalculator {

    protected boolean isOnBoard(int row, int column) {

        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }

    protected boolean isEmptyOrFoe(ChessBoard board, ChessPiece piece, ChessPosition end) {

        ChessPiece otherPiece = board.getPiece(end);

        return otherPiece == null || otherPiece.getTeamColor() != piece.getTeamColor();
    }

    // shared bishop, rook, and queen logic
    protected void addMovesInDirection(Collection<ChessMove> moves, ChessBoard board, ChessPosition start, ChessPiece movingPiece, int rowChange, int colChange) {

        int endRow = start.getRow() + rowChange;
        int endColumn = start.getColumn() + colChange;

        while (isOnBoard(endRow, endColumn)) {
            ChessPosition end = new ChessPosition(endRow, endColumn);

            if (!isEmptyOrFoe(board, movingPiece, end)) {
                return;
            }

            moves.add(new ChessMove(start, end, null));

            if (board.getPiece(end) != null) {
                return;
            }

            endRow += rowChange;
            endColumn += colChange;
        }
    }
}
