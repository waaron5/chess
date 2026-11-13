package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardRenderer {
    private static final int BOARD_SIZE = 8;

    public static String render(ChessBoard board, ChessGame.TeamColor perspective) {
        boolean whiteView = perspective == ChessGame.TeamColor.WHITE;
        StringBuilder out = new StringBuilder();

        appendColumnLabels(out, whiteView);
        for (int i = 0; i < BOARD_SIZE; i++) {
            int row = whiteView ? BOARD_SIZE - i : i + 1;
            out.append(" ").append(row).append(" ");
            for (int j = 0; j < BOARD_SIZE; j++) {
                int col = whiteView ? j + 1 : BOARD_SIZE - j;
                out.append(squareColor(row, col));
                out.append(pieceText(board.getPiece(new ChessPosition(row, col))));
            }
            out.append(RESET_BG_COLOR).append(RESET_TEXT_COLOR);
            out.append(" ").append(row).append("\n");
        }
        appendColumnLabels(out, whiteView);
        return out.toString();
    }

    private static void appendColumnLabels(StringBuilder out, boolean whiteView) {
        out.append("   ");
        for (int j = 0; j < BOARD_SIZE; j++) {
            char letter = (char) (whiteView ? 'a' + j : 'h' - j);
            out.append(" ").append(letter).append(" ");
        }
        out.append("\n");
    }

    private static String squareColor(int row, int col) {
        boolean light = (row + col) % 2 == 1;
        return light ? SET_BG_COLOR_WHITE : SET_BG_COLOR_DARK_GREY;
    }

    private static String pieceText(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }
        String color = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
        return color + " " + pieceLetter(piece.getPieceType()) + " ";
    }

    private static char pieceLetter(ChessPiece.PieceType type) {
        return switch (type) {
            case KING -> 'K';
            case QUEEN -> 'Q';
            case BISHOP -> 'B';
            case KNIGHT -> 'N';
            case ROOK -> 'R';
            case PAWN -> 'P';
        };
    }
}
