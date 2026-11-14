package chess;

import java.util.Collection;

public class PieceMovesCalculator {

    protected boolean isOnBoard(int row, int column) {
        return row >= 1 && row <= ChessBoard.BOARD_SIZE && column >= 1 && column <= ChessBoard.BOARD_SIZE;
    }

    protected boolean isEmptyOrFoe(ChessBoard board, ChessPiece piece, ChessPosition end) {
        ChessPiece otherPiece = board.getPiece(end);
        return otherPiece == null || otherPiece.getTeamColor() != piece.getTeamColor();
    }

    /** Adds the one move (if legal) that is rowChange/colChange away, as kings and knights move. */
    protected void addSingleMove(Collection<ChessMove> moves, ChessBoard board, ChessPosition start,
                                 int rowChange, int colChange) {
        int endRow = start.getRow() + rowChange;
        int endColumn = start.getColumn() + colChange;
        if (!isOnBoard(endRow, endColumn)) {
            return;
        }

        ChessPosition end = new ChessPosition(endRow, endColumn);
        if (isEmptyOrFoe(board, board.getPiece(start), end)) {
            moves.add(new ChessMove(start, end, null));
        }
    }

    /** Adds moves along one direction until blocked, as bishops, rooks, and queens move. */
    protected void addMovesInDirection(Collection<ChessMove> moves, ChessBoard board, ChessPosition start,
                                       int rowChange, int colChange) {
        ChessPiece movingPiece = board.getPiece(start);
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
