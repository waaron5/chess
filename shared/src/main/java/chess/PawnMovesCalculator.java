package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start) {

        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece pawn = board.getPiece(start);
        int direction = getDirection(pawn);

        addForwardMoves(moves, board, start, pawn, direction);

        // Check the diagonal capture squares
        addCaptureMove(moves, board, start, pawn, direction, -1);
        addCaptureMove(moves, board, start, pawn, direction, 1);

        return moves;
    }

    private int getDirection(ChessPiece pawn) {
        if (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return 1;
        }

        return -1;
    }

    private void addForwardMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition start, ChessPiece pawn, int direction) {

        int nextRow = start.getRow() + direction;
        int column = start.getColumn();

        if (!isOnBoard(nextRow, column)) {
            return;
        }

        ChessPosition oneForward = new ChessPosition(nextRow, column);

        // Pawns cannot move forward onto another piece
        if (board.getPiece(oneForward) != null) {
            return;
        }

        addPawnMove(moves, start, oneForward, pawn);

        // Only pawns on the starting row can move twice
        if (!isStartingRow(start, pawn)) {
            return;
        }

        int twoForwardRow = start.getRow() + (2 * direction);

        ChessPosition twoForward = new ChessPosition(twoForwardRow, column);

        if (board.getPiece(twoForward) == null) {
            addPawnMove(moves, start, twoForward, pawn);
        }
    }

    private void addCaptureMove(Collection<ChessMove> moves, ChessBoard board, ChessPosition start, ChessPiece pawn, int direction, int columnChange) {

        int endRow = start.getRow() + direction;
        int endColumn = start.getColumn() + columnChange;

        if (!isOnBoard(endRow, endColumn)) {
            return;
        }

        ChessPosition end =
                new ChessPosition(endRow, endColumn);

        ChessPiece otherPiece = board.getPiece(end);

        // There must be a piece to capture
        if (otherPiece == null) {
            return;
        }

        // Pawns can't capture pieces of the same color
        if (otherPiece.getTeamColor() == pawn.getTeamColor()) {
            return;
        }

        addPawnMove(moves, start, end, pawn);
    }

    private void addPawnMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, ChessPiece pawn) {

        if (!isPromotionRow(end, pawn)) {
            moves.add(new ChessMove(start, end, null));
            return;
        }

        moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));

        moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));

        moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));

        moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
    }

    private boolean isStartingRow(ChessPosition position, ChessPiece pawn) {

        if (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return position.getRow() == 2;
        }

        return position.getRow() == 7;
    }

    private boolean isPromotionRow(ChessPosition position, ChessPiece pawn) {

        if (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return position.getRow() == 8;
        }

        return position.getRow() == 1;
    }
}