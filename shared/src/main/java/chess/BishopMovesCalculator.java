package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();

        addMovesInDirection(moves, board, start, 1, 1); // up-right
        addMovesInDirection(moves, board, start, 1, -1); // up-left
        addMovesInDirection(moves, board, start, -1, 1); // down-right
        addMovesInDirection(moves, board, start, -1, -1); // down-left

        return moves;
    }
}
