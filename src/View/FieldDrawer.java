package View;

import minesweeper.Field;

import javax.swing.*;
import java.awt.*;

public class FieldDrawer {
    public static final int SIZE = 32;
    public static final int FRAME_X = SIZE*(Field.FIELD_X + 6);
    public static final int FRAME_Y = SIZE*(Field.FIELD_Y) + 40;

    private ImageIcon icon = new ImageIcon("textures/empty.png");
    private JButton[][] buttons = new JButton[Field.FIELD_X][Field.FIELD_Y];

    public FieldDrawer(JPanel game_panel) {
        for (int x = 0; x < Field.FIELD_X; x++) {
            for (int y = 0; y < Field.FIELD_Y; y++) {
                buttons[x][y] = new JButton(icon);
                buttons[x][y].setFocusable(false);
                //buttons[x][y].setBorderPainted(false);
                buttons[x][y].setContentAreaFilled(false);

                game_panel.add(buttons[x][y]);
            }
        }
    }

    public void update(Field f) {

    }
}
