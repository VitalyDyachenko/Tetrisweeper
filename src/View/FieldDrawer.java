package View;

import Model.minesweeper.Cell;
import Model.minesweeper.Field;
import Model.tetris.FallingTetrimino;
import Model.tetris.TetriminoType;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FieldDrawer {
    public static final int SIZE = 32;
    public static final int FRAME_X = SIZE*(Field.FIELD_X + 6);
    public static final int FRAME_Y = SIZE*(Field.FIELD_Y) + 40;

    private JButton[][] buttons = new JButton[Field.FIELD_X][Field.FIELD_Y];
    private static final Border CELL_BORDER =
            BorderFactory.createLineBorder(new Color(100, 150, 255), 2);

    public interface CellClickHandler {
        void onLeftClick(int x, int y);
        void onRightClick(int x, int y);
    }
    private CellClickHandler cell_handler;

    public FieldDrawer(JPanel game_panel, CellClickHandler handler) {
        cell_handler = handler;
        for (int y = 0; y < Field.FIELD_Y; y++) {
            for (int x = 0; x < Field.FIELD_X; x++) {
                int xi = x;
                int yi = y;
                buttons[x][y] = new JButton(CellTexture.EMPTY.getIcon());
                buttons[x][y].setFocusable(false);
                buttons[x][y].setContentAreaFilled(false);
                buttons[xi][yi].setBorder(null);
                buttons[x][y].setRequestFocusEnabled(false);

                buttons[x][y].getModel().addChangeListener(e -> {
                    ButtonModel model = (ButtonModel) e.getSource();
                    buttons[xi][yi].setBorder(model.isRollover() ? CELL_BORDER : null);
                });

                buttons[x][y].addActionListener(e -> {
                    cell_handler.onLeftClick(xi, yi);
                });
                buttons[x][y].addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            cell_handler.onRightClick(xi, yi);
                        }
                    }
                });

                game_panel.add(buttons[x][y]);
            }
        }
    }

    public void update(Field f, FallingTetrimino tet) {
        for (int x = 0; x < Field.FIELD_X; x++) {
            for (int y = 0; y < Field.FIELD_Y; y++) {
                buttons[x][y].setIcon(getCellTexture(f.getCell(x,y)));
            }
        }
        for (int i = 0; i < FallingTetrimino.TETROMINO_SIZE; i++) {
            Point p = tet.getCellsPos()[i];
            Cell c = tet.getCells()[i];
            buttons[p.x + tet.getPos().x][p.y + tet.getPos().y].setIcon(getCellTexture(c));
        }
    }

    private ImageIcon getCellTexture(Cell cell) {
        if (cell == null) return CellTexture.EMPTY.getIcon();
        if (cell.getType() == TetriminoType.T) return CellTexture.T.getIcon();
        return CellTexture.EMPTY.getIcon();
    }
}
