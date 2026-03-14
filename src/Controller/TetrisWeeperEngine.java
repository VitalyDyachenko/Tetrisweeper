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

    public TetrisWeeperEngine() {
        view.setInputHandler(new GameView.InputHandler() {
            @Override
            public void onLeft() {
                //tet.moveLeft(field);
                System.out.println("Влево");
                view.update(field, tet);
            }

            @Override
            public void onRight() {
                //tet.moveRight(field);
                System.out.println("Вправо");
                view.update(field, tet);
            }

            @Override
            public void onCellLeftClick(int x, int y) {
                System.out.println("Левая кнопка " + x + " " + y + " нажата");
                view.update(field, tet);
            }

            @Override
            public void onCellRightClick(int x, int y) {
                System.out.println("Правая кнопка " + x + " " + y + " нажата");
                view.update(field, tet);
            }
        });
    }


    public void run() {
        view.update(field, tet);
        gameTimer = new Timer(1000, e -> {
            // Это выполняется в EDT
            if (!tet.move(field)) tet = new FallingTetrimino();
            view.update(field, tet);
        });
        gameTimer.start();
    }
}
