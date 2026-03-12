package View;

import Model.minesweeper.Field;
import Model.tetris.FallingTetrimino;

import javax.swing.*;
import java.awt.*;

public class GameView {
    private JFrame game_frame = new JFrame("Tetrisweeper");
    private JPanel game_panel = new JPanel(new GridLayout(Field.FIELD_Y, Field.FIELD_X));
    private FieldDrawer field = new FieldDrawer(game_panel);
    private JPanel main_panel = new JPanel(new BorderLayout());

    public GameView() {
        game_frame.setSize(FieldDrawer.FRAME_X, FieldDrawer.FRAME_Y);
        game_frame.setResizable(false);

        game_panel.setBackground(Color.BLACK);
        game_panel.setPreferredSize(new Dimension(
                Field.FIELD_X * FieldDrawer.SIZE,
                Field.FIELD_Y * FieldDrawer.SIZE
        ));

        main_panel.add(game_panel, BorderLayout.WEST);
        main_panel.setBackground(Color.GRAY);

        game_frame.add(main_panel);
        game_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game_frame.setLocation(500, 50);
        game_frame.setVisible(true);
    }

    public void update(Field f, FallingTetrimino tet) {
        field.update(f, tet);
    }
}
