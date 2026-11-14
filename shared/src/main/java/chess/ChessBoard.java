package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public static final int BOARD_SIZE = 8;

    private ChessPiece[][] squares = new ChessPiece[BOARD_SIZE][BOARD_SIZE];

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[BOARD_SIZE][BOARD_SIZE];

        ChessPiece.PieceType[] backRow = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };

        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;
        ChessPiece.PieceType pawn = ChessPiece.PieceType.PAWN;
        for (int column = 1; column <= BOARD_SIZE; column++) {
            ChessPiece.PieceType backRowType = backRow[column - 1];
            addPiece(new ChessPosition(1, column), new ChessPiece(white, backRowType));
            addPiece(new ChessPosition(2, column), new ChessPiece(white, pawn));
            addPiece(new ChessPosition(7, column), new ChessPiece(black, pawn));
            addPiece(new ChessPosition(8, column), new ChessPiece(black, backRowType));
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ChessBoard otherBoard)) {
            return false;
        }
        return Arrays.deepEquals(squares, otherBoard.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
