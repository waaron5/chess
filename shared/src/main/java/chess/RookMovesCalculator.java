package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start) {

        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece rook = board.getPiece(start);

        addMovesInDirection(moves, board, start, rook, 1, 0); // up
        addMovesInDirection(moves, board, start, rook, -1, 0); // down
        addMovesInDirection(moves, board, start, rook, 0, 1); // right
        addMovesInDirection(moves, board, start, rook, 0, -1); // left

        return moves;
    }
}
