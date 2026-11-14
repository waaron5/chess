package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();

        addSingleMove(moves, board, start, 1, 0); // up
        addSingleMove(moves, board, start, -1, 0); // down
        addSingleMove(moves, board, start, 0, 1); // right
        addSingleMove(moves, board, start, 0, -1); // left

        addSingleMove(moves, board, start, 1, 1); // up-right
        addSingleMove(moves, board, start, 1, -1); // up-left
        addSingleMove(moves, board, start, -1, 1); // down-right
        addSingleMove(moves, board, start, -1, -1); // down-left

        return moves;
    }
}
