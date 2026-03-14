package View;

import Model.minesweeper.Field;
import Model.tetris.FallingTetrimino;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GameView {
    private JFrame game_frame = new JFrame("Tetrisweeper");
    private JPanel game_panel = new JPanel(new GridLayout(Field.FIELD_Y, Field.FIELD_X));
    private FieldDrawer field;
    private JPanel main_panel = new JPanel(new BorderLayout());

    public interface InputHandler {
        void onLeft();
        void onRight();

        void onCellLeftClick(int x, int y);
        void onCellRightClick(int x, int y);
    }
    private InputHandler input_handler;
    public void setInputHandler(InputHandler handler) {
        input_handler = handler;
    }

    public GameView() {
        field = new FieldDrawer(game_panel, new FieldDrawer.CellClickHandler() {
            @Override
            public void onLeftClick(int x, int y) {
                if (input_handler != null) input_handler.onCellLeftClick(x, y);
            }

            @Override
            public void onRightClick(int x, int y) {
                if (input_handler != null) input_handler.onCellRightClick(x, y);
            }
        });

        game_frame.setSize(FieldDrawer.FRAME_X, FieldDrawer.FRAME_Y);
        game_frame.setResizable(false);

        game_panel.setBackground(Color.BLACK);
        game_panel.setPreferredSize(new Dimension(
                Field.FIELD_X * FieldDrawer.SIZE,
                Field.FIELD_Y * FieldDrawer.SIZE
        ));

        main_panel.add(game_panel, BorderLayout.WEST);
        main_panel.setBackground(Color.GRAY);
        setupKeyBindings();

        game_frame.add(main_panel);
        game_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game_frame.setLocation(500, 50);
        game_frame.setVisible(true);
    }

    private void setupKeyBindings() {
        InputMap inputMap = main_panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = main_panel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onLeft();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onRight();
            }
        });
    }

    public void update(Field f, FallingTetrimino tet) {
        field.update(f, tet);
    }
}
