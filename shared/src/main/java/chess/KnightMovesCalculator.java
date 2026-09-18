package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start) {

        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece knight = board.getPiece(start);

        addKnightMove(moves, board, start, knight, 2, -1); // up-left
        addKnightMove(moves, board, start, knight, 2, 1); // up-right
        addKnightMove(moves, board, start, knight, 1, 2); // right-up
        addKnightMove(moves, board, start, knight, 1, -2); // left-up

        addKnightMove(moves, board, start, knight, -2, -1); // down-left
        addKnightMove(moves, board, start, knight, -2, 1); // down-right
        addKnightMove(moves, board, start, knight, -1, -2); // left-down
        addKnightMove(moves, board, start, knight, -1, 2); // right-down

        return moves;
    }

    public void addKnightMove(Collection<ChessMove> moves, ChessBoard board, ChessPosition start, ChessPiece knight, int rowChange, int colChange) {

        int endRow = start.getRow() + rowChange;
        int endColumn = start.getColumn() + colChange;

        if (!isOnBoard(endRow, endColumn)) {
            return;
        }

        ChessPosition end = new ChessPosition(endRow, endColumn);

        if (!isEmptyOrFoe(board, knight, end)) {
            return;
        }

        moves.add(new ChessMove(start, end, null));
    }
}
