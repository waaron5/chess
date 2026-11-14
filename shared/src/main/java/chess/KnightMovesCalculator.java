package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();

        addSingleMove(moves, board, start, 2, -1); // up-left
        addSingleMove(moves, board, start, 2, 1); // up-right
        addSingleMove(moves, board, start, 1, 2); // right-up
        addSingleMove(moves, board, start, 1, -2); // left-up

        addSingleMove(moves, board, start, -2, -1); // down-left
        addSingleMove(moves, board, start, -2, 1); // down-right
        addSingleMove(moves, board, start, -1, -2); // left-down
        addSingleMove(moves, board, start, -1, 2); // right-down

        return moves;
    }
}
