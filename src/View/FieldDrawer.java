package View;

import Model.minesweeper.Cell;
import Model.minesweeper.Field;
import Model.tetris.FallingTetrimino;
import Model.tetris.TetriminoType;
import game.Context;
import game.GameMode;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FieldDrawer {
    public static final int SIZE = 33;
    public static final int FRAME_X = SIZE*(Field.FIELD_X + 6);
    public static final int FRAME_Y = SIZE*(Field.FIELD_Y) + 37;

    private JButton[][][] buttons = new JButton[Field.FIELD_X][Field.FIELD_Y][2];
    private static final Border CELL_BORDER =
            BorderFactory.createLineBorder(new Color(255, 255, 255), 2);

    private JPanel[] layers = new JPanel[2];
    private int active_layer = 0;

    public interface CellClickHandler {
        void onLeftClick(int x, int y);
        void onRightClick(int x, int y);
    }
    private CellClickHandler cell_handler;

    public FieldDrawer(JLayeredPane game_panel, CellClickHandler handler) {
        cell_handler = handler;

        for (int layer = 0; layer < 2; layer++) {
            layers[layer] = new JPanel(new GridLayout(Field.FIELD_Y, Field.FIELD_X, 0, 0));
            layers[layer].setBounds(0, 0,
                    Field.FIELD_X * SIZE,
                    Field.FIELD_Y * SIZE);
            layers[layer].setOpaque(false);

            createButtons(layer);
            game_panel.add(layers[layer], JLayeredPane.DEFAULT_LAYER);
        }

    }
    private void createButtons(int layer) {
        for (int y = 0; y < Field.FIELD_Y; y++) {
            for (int x = 0; x < Field.FIELD_X; x++) {
                int xi = x;
                int yi = y;
                buttons[x][y][layer] = new JButton(CellTexture.EMPTY.getIcon());
                buttons[x][y][layer].setFocusable(false);
                buttons[x][y][layer].setContentAreaFilled(false);
                buttons[xi][yi][layer].setBorder(null);
                buttons[x][y][layer].setRequestFocusEnabled(false);

                buttons[x][y][layer].getModel().addChangeListener(e -> {
                    ButtonModel model = (ButtonModel) e.getSource();
                    buttons[xi][yi][layer].setBorder(model.isRollover() ? CELL_BORDER : null);
                });

                buttons[x][y][layer].addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            cell_handler.onLeftClick(xi, yi);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            cell_handler.onRightClick(xi, yi);
                        }
                    }
                });

                layers[layer].add(buttons[x][y][layer]);
            }
        }
    }

    public void update(Context context) {
        int next_layer = (active_layer + 1) % 2;
        for (int x = 0; x < Field.FIELD_X; x++) {
            for (int y = 0; y < Field.FIELD_Y; y++) {
                buttons[x][y][next_layer].setIcon(getCellTexture(context, x, y));
            }
        }
        for (int i = 0; i < FallingTetrimino.TETROMINO_SIZE; i++) {
            Point p = context.tet.getCellsPos()[i];
            Cell c = context.tet.getCells()[i];
            if (p.y + context.tet.getPos().y >= 0)
                buttons[p.x + context.tet.getPos().x][p.y + context.tet.getPos().y][next_layer].setIcon(getTetCellTexture(c));
        }

        layers[active_layer].setVisible(false);
        layers[next_layer].setVisible(true);
        active_layer = next_layer;
    }

    private ImageIcon getCellTexture(Context context, int x, int y) {
        Cell cell = context.field.getCell(x, y);
        if (cell == null) return CellTexture.EMPTY.getIcon();
        if (!cell.isOpened()) {
            if (context.mode == GameMode.TETRISWEEPER && context.field.isCellOnBorder(x, y) && !cell.haveFlag())
                return switch (cell.getType()) {
                    case TetriminoType.T -> CellTexture.T_X.getIcon();
                    case TetriminoType.O -> CellTexture.O_X.getIcon();
                    case TetriminoType.I -> CellTexture.I_X.getIcon();
                    case TetriminoType.S -> CellTexture.S_X.getIcon();
                    case TetriminoType.Z -> CellTexture.Z_X.getIcon();
                    case TetriminoType.L -> CellTexture.L_X.getIcon();
                    case TetriminoType.J -> CellTexture.J_X.getIcon();
                };
            return getTetCellTexture(cell);
        }
        else {
            if (cell.haveMine()) return CellTexture.MINE.getIcon();
            else {
                return switch (context.field.minesNextToMe(x, y)) {
                    case 1 -> CellTexture.N1.getIcon();
                    case 2 -> CellTexture.N2.getIcon();
                    case 3 -> CellTexture.N3.getIcon();
                    case 4 -> CellTexture.N4.getIcon();
                    case 5 -> CellTexture.N5.getIcon();
                    case 6 -> CellTexture.N6.getIcon();
                    case 7 -> CellTexture.N7.getIcon();
                    case 8 -> CellTexture.N8.getIcon();
                    default -> CellTexture.N0.getIcon();
                };
            }
        }
    }
    private ImageIcon getTetCellTexture(Cell cell) {
        if (cell == null) return CellTexture.EMPTY.getIcon();
        return switch (cell.getType()) {
            case TetriminoType.T -> cell.haveFlag() ? CellTexture.T_FLAG.getIcon() : CellTexture.T.getIcon();
            case TetriminoType.O -> cell.haveFlag() ? CellTexture.O_FLAG.getIcon() : CellTexture.O.getIcon();
            case TetriminoType.I -> cell.haveFlag() ? CellTexture.I_FLAG.getIcon() : CellTexture.I.getIcon();
            case TetriminoType.S -> cell.haveFlag() ? CellTexture.S_FLAG.getIcon() : CellTexture.S.getIcon();
            case TetriminoType.Z -> cell.haveFlag() ? CellTexture.Z_FLAG.getIcon() : CellTexture.Z.getIcon();
            case TetriminoType.L -> cell.haveFlag() ? CellTexture.L_FLAG.getIcon() : CellTexture.L.getIcon();
            case TetriminoType.J -> cell.haveFlag() ? CellTexture.J_FLAG.getIcon() : CellTexture.J.getIcon();
        };
    }
}
