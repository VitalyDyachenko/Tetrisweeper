package View;

import Model.minesweeper.Field;
import Model.tetris.FallingTetromino;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class FieldDrawer {
    public static final int SIZE = 34;
    public static final int FRAME_X = SIZE*(Field.FIELD_X + 6);
    public static final int FRAME_Y = SIZE*(Field.FIELD_Y) + 40;

    private ImageIcon icon_e = new ImageIcon("textures/empty.png");
    private ImageIcon icon_t = new ImageIcon("textures/O.png");
    private JButton[][] buttons = new JButton[Field.FIELD_X][Field.FIELD_Y];
    private static final Border CELL_BORDER =
            BorderFactory.createLineBorder(new Color(100, 150, 255), 2);

    public FieldDrawer(JPanel game_panel) {
        for (int y = 0; y < Field.FIELD_Y; y++) {
            for (int x = 0; x < Field.FIELD_X; x++) {
                int xi = x;
                int yi = y;
                buttons[x][y] = new JButton(icon_e);
                buttons[x][y].setFocusable(false);
                buttons[x][y].setContentAreaFilled(false);
                buttons[xi][yi].setBorder(null);

                buttons[x][y].getModel().addChangeListener(e -> {
                    ButtonModel model = (ButtonModel) e.getSource();
                    buttons[xi][yi].setBorder(model.isRollover() ? CELL_BORDER : null);
                });

                buttons[x][y].addActionListener(e -> {
                    System.out.println("Кнопка " + xi + " " + yi + " нажата");
                });

                game_panel.add(buttons[x][y]);
            }
        }
    }

    public void update(Field f, FallingTetromino tet) {
        for (int x = 0; x < Field.FIELD_X; x++) {
            for (int y = 0; y < Field.FIELD_Y; y++) {
                buttons[x][y].setIcon(icon_e);
            }
        }
        for (Point p : tet.getAllCoordinates()) {
            buttons[p.x + tet.getPos().x][p.y + tet.getPos().y].setIcon(icon_t);
        }

    }
}
