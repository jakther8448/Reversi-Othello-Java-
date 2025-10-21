package reversi;

import java.util.Random;

public class ReversiController implements IController {

    private IModel model;
    private IView view;
    private Random rand = new Random();

    @Override
    public void initialise(IModel model, IView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void startup() {
        int width = model.getBoardWidth();
        int height = model.getBoardHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                model.setBoardContents(x, y, 0);
            }
        }

        // Setup standard Reversi start positions
        int midX = width / 2;
        int midY = height / 2;
        model.setBoardContents(midX - 1, midY - 1, 1); // W
        model.setBoardContents(midX, midY, 1);         // W
        model.setBoardContents(midX - 1, midY, 2);     // B
        model.setBoardContents(midX, midY - 1, 2);     // B

        model.setPlayer(1); // Player 1 starts

        view.feedbackToUser(1, "Reversi started - Player 1's turn");
        view.feedbackToUser(2, "Reversi started - Player 1's turn");
        view.refreshView();
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < model.getBoardWidth() && y >= 0 && y < model.getBoardHeight();
    }

    private boolean tryMove(int player, int x, int y, boolean flip) {
        int opponent = (player == 1) ? 2 : 1;
        boolean legal = false;

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},          {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };

        for (int[] dir : directions) {
            int dx = dir[0], dy = dir[1];
            int cx = x + dx, cy = y + dy;
            boolean hasOpponentBetween = false;

            while (isInBounds(cx, cy) && model.getBoardContents(cx, cy) == opponent) {
                cx += dx;
                cy += dy;
                hasOpponentBetween = true;
            }

            if (hasOpponentBetween && isInBounds(cx, cy) && model.getBoardContents(cx, cy) == player) {
                legal = true;
                if (flip) {
                    cx = x + dx;
                    cy = y + dy;
                    while (model.getBoardContents(cx, cy) == opponent) {
                        model.setBoardContents(cx, cy, player);
                        cx += dx;
                        cy += dy;
                    }
                }
            }
        }

        return legal;
    }

    @Override
    public void squareSelected(int player, int x, int y) {
        if (!isInBounds(x, y) || model.getBoardContents(x, y) != 0) {
            view.feedbackToUser(player, "Invalid move: Not empty or out of bounds");
            return;
        }
        if (model.getPlayer() != player) {
            view.feedbackToUser(player, (player == 1 ? "White" : "Black") + " player - not your turn");
            return;
        }

        boolean legal = tryMove(player, x, y, false);
        if (!legal) {
            view.feedbackToUser(player, "Illegal move: No discs to flip");
            return;
        }

        model.setBoardContents(x, y, player);
        tryMove(player, x, y, true);

        int nextPlayer = (player == 1) ? 2 : 1;
        model.setPlayer(nextPlayer);

        view.feedbackToUser(player, "Move accepted at (" + x + ", " + y + ")");
        view.feedbackToUser(nextPlayer, "Your turn");
        view.refreshView();
    }

    @Override
    public void doAutomatedMove(int player) {
        int width = model.getBoardWidth();
        int height = model.getBoardHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (model.getBoardContents(x, y) == 0 && tryMove(player, x, y, false)) {
                    squareSelected(player, x, y);
                    return;
                }
            }
        }

        view.feedbackToUser(player, "No legal moves available");
    }

    @Override
    public void update() {
        int width = model.getBoardWidth();
        int height = model.getBoardHeight();
        boolean hasMoves = false;

        for (int x = 0; x < width && !hasMoves; x++) {
            for (int y = 0; y < height && !hasMoves; y++) {
                if (model.getBoardContents(x, y) == 0) {
                    hasMoves = true;
                }
            }
        }

        model.setFinished(!hasMoves);
        view.refreshView();
    }
}
