package Controller;

import Model.minesweeper.Field;
import Model.tetris.FallingTetrimino;
import View.GameView;

import javax.swing.Timer;

public class TetrisWeeperEngine {
    private GameView view = new GameView();

    private Field field = new Field();
    private FallingTetrimino tet = new FallingTetrimino();

    private Timer gameTimer;

    public void run() {
        view.update(field, tet);
        gameTimer = new Timer(1000, e -> {
            // Это выполняется в EDT
            tet.move();
            view.update(field, tet);
        });
        gameTimer.start();
    }
}
