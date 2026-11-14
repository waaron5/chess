package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();

        addMovesInDirection(moves, board, start, 1, 0); // up
        addMovesInDirection(moves, board, start, -1, 0); // down
        addMovesInDirection(moves, board, start, 0, 1); // right
        addMovesInDirection(moves, board, start, 0, -1); // left

        return moves;
    }
}
