package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start) {

        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece king = board.getPiece(start);

        addKingMove(moves, board, start, king, 1, 0); // up
        addKingMove(moves, board, start, king, -1, 0); // down
        addKingMove(moves, board, start, king, 0, 1); // right
        addKingMove(moves, board, start, king, 0, -1); // left

        addKingMove(moves, board, start, king, 1, 1); // up-right
        addKingMove(moves, board, start, king, 1, -1); // up-left
        addKingMove(moves, board, start, king, -1, 1); // down-right
        addKingMove(moves, board, start, king, -1, -1); // down-left

        return moves;
    }

    private void addKingMove(Collection<ChessMove> moves, ChessBoard board, ChessPosition start, ChessPiece king, int rowChange, int columnChange) {

        int endRow = start.getRow() + rowChange;
        int endColumn = start.getColumn() + columnChange;

        if (!isOnBoard(endRow, endColumn)) {
            return;
        }

        ChessPosition end = new ChessPosition(endRow, endColumn);

        if (!isEmptyOrFoe(board, king, end)) {
            return;
        }

        moves.add(new ChessMove(start, end, null));
    }
}