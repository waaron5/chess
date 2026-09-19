package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        // Remove everything on the board
        squares = new ChessPiece[8][8];

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

        for (int column = 1; column <= 8; column++) {

            // White pieces
            addPiece(
                    new ChessPosition(1, column),
                    new ChessPiece(ChessGame.TeamColor.WHITE, backRow[column - 1])
            );

            addPiece(
                    new ChessPosition(2, column),
                    new ChessPiece(
                            ChessGame.TeamColor.WHITE,
                            ChessPiece.PieceType.PAWN
                    )
            );

            // Black pieces
            addPiece(
                    new ChessPosition(7, column),
                    new ChessPiece(
                            ChessGame.TeamColor.BLACK,
                            ChessPiece.PieceType.PAWN
                    )
            );

            addPiece(
                    new ChessPosition(8, column),
                    new ChessPiece(ChessGame.TeamColor.BLACK, backRow[column - 1])
            );
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof ChessBoard)) {
            return false;
        }

        ChessBoard otherBoard = (ChessBoard) object;

        return Arrays.deepEquals(squares, otherBoard.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
