package reversi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUIView implements IView {
    private IController controller;
    private IModel model;

    private JButton[][] buttons = new JButton[8][8];
    private JFrame frame;

    @Override
    public void initialise(IModel model, IController controller) {
        this.model = model;
        this.controller = controller;

        frame = new JFrame("Reversi - Player View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new GridLayout(8, 8));

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                JButton button = new JButton(".");
                final int fx = x;
                final int fy = y;

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Get current player from the model
                        int player = model.getPlayer();

                        // Call the correct method in SimpleController
                        ((SimpleController) controller).squareSelected(player, fx, fy);
                    }
                });

                buttons[y][x] = button;
                frame.add(button);
            }
        }

        frame.setVisible(true);
    }

    @Override
    public void refreshView() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int cell = ((SimpleModel) model).getBoardContents(x, y);
                String text = switch (cell) {
                    case 1 -> "X";
                    case 2 -> "O";
                    default -> ".";
                };
                buttons[y][x].setText(text);
            }
        }
    }

    @Override
    public void feedbackToUser(int playerNumber, String message) {
        JOptionPane.showMessageDialog(frame, "Player " + playerNumber + ": " + message);
    }
}
