package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board,
                                            ChessPosition start) {

        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece queen = board.getPiece(start);

        // Straight directions
        addMovesInDirection(moves, board, start, queen, 1, 0);   // up
        addMovesInDirection(moves, board, start, queen, -1, 0);  // down
        addMovesInDirection(moves, board, start, queen, 0, 1);   // right
        addMovesInDirection(moves, board, start, queen, 0, -1);  // left

        // Diagonal directions
        addMovesInDirection(moves, board, start, queen, 1, 1);    // up-right
        addMovesInDirection(moves, board, start, queen, 1, -1);   // up-left
        addMovesInDirection(moves, board, start, queen, -1, 1);   // down-right
        addMovesInDirection(moves, board, start, queen, -1, -1);  // down-left

        return moves;
    }
}