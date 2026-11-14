package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMovesCalculator {
    private static final int WHITE_START_ROW = 2;
    private static final int BLACK_START_ROW = 7;

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();

        addForwardMoves(moves, board, start);
        addCaptureMove(moves, board, start, -1);
        addCaptureMove(moves, board, start, 1);

        return moves;
    }

    private boolean isWhite(ChessPiece pawn) {
        return pawn.getTeamColor() == ChessGame.TeamColor.WHITE;
    }

    private int getDirection(ChessPiece pawn) {
        return isWhite(pawn) ? 1 : -1;
    }

    private void addForwardMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition start) {
        ChessPiece pawn = board.getPiece(start);
        int direction = getDirection(pawn);
        int nextRow = start.getRow() + direction;
        int column = start.getColumn();
        if (!isOnBoard(nextRow, column)) {
            return;
        }

        // Pawns cannot move forward onto another piece
        ChessPosition oneForward = new ChessPosition(nextRow, column);
        if (board.getPiece(oneForward) != null) {
            return;
        }
        addPawnMove(moves, start, oneForward, pawn);

        // Only pawns on the starting row can move twice
        if (!isStartingRow(start, pawn)) {
            return;
        }
        ChessPosition twoForward = new ChessPosition(start.getRow() + 2 * direction, column);
        if (board.getPiece(twoForward) == null) {
            addPawnMove(moves, start, twoForward, pawn);
        }
    }

    private void addCaptureMove(Collection<ChessMove> moves, ChessBoard board, ChessPosition start, int columnChange) {
        ChessPiece pawn = board.getPiece(start);
        int endRow = start.getRow() + getDirection(pawn);
        int endColumn = start.getColumn() + columnChange;
        if (!isOnBoard(endRow, endColumn)) {
            return;
        }

        // There must be an enemy piece to capture
        ChessPosition end = new ChessPosition(endRow, endColumn);
        ChessPiece otherPiece = board.getPiece(end);
        if (otherPiece != null && otherPiece.getTeamColor() != pawn.getTeamColor()) {
            addPawnMove(moves, start, end, pawn);
        }
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
        return position.getRow() == (isWhite(pawn) ? WHITE_START_ROW : BLACK_START_ROW);
    }

    private boolean isPromotionRow(ChessPosition position, ChessPiece pawn) {
        return position.getRow() == (isWhite(pawn) ? ChessBoard.BOARD_SIZE : 1);
    }
}
