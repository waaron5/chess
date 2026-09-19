package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start) {

        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece bishop = board.getPiece(start);

        addMovesInDirection(moves, board, start, bishop, 1, 1); // right-up
        addMovesInDirection(moves, board, start, bishop, 1, -1); // right-down
        addMovesInDirection(moves, board, start, bishop, -1, 1); // left-up
        addMovesInDirection(moves, board, start, bishop, -1, -1); // left-down

        return moves;
    }

    private void addMovesInDirection(Collection<ChessMove> moves, ChessBoard board, ChessPosition start, ChessPiece bishop, int rowChange, int columnChange) {

        int endRow = start.getRow() + rowChange;
        int endColumn = start.getColumn() + columnChange;

        while (isOnBoard(endRow, endColumn)) {

            ChessPosition end = new ChessPosition(endRow, endColumn);

            // A same team piece blocks the bishop
            if (!isEmptyOrFoe(board, bishop, end)) {
                return;
            }

            moves.add(new ChessMove(start, end, null));

            // A foe piece may be captured, but blocks squares behind it
            if (board.getPiece(end) != null) {
                return;
            }

            endRow += rowChange;
            endColumn += columnChange;
        }
    }
}