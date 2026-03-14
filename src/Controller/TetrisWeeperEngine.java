package Controller;

import Model.minesweeper.Field;
import Model.tetris.FallingTetrimino;
import View.GameView;

import javax.swing.Timer;
import java.util.Random;

public class TetrisWeeperEngine {
    private GameMode mode = GameMode.TETRIS;
    private GameState state = GameState.MENU;

    private GameView view = new GameView();
    private Field field = new Field();
    private FallingTetrimino tet = new FallingTetrimino(RANDOM);

    private Timer game_timer;
    private static final Random RANDOM = new Random();

    public TetrisWeeperEngine() {
        view.setInputHandler(new GameView.InputHandler() {
            @Override
            public void onLeft() {
                tet.moveLeft(field);
                view.update(field, tet, state);
            }

            @Override
            public void onRight() {
                tet.moveRight(field);
                view.update(field, tet, state);
            }

            @Override
            public void onDown() {
                if (!tet.moveDown(field)) tet = new FallingTetrimino(RANDOM);
                view.update(field, tet, state);
            }

            @Override
            public void onRotateLeft() {
                tet.rotateLeft(field);
                view.update(field, tet, state);
            }

            @Override
            public void onRotateRight() {
                tet.rotateRight(field);
                view.update(field, tet, state);
            }



            @Override
            public void onModeChanged(GameMode new_mode) {
                mode = new_mode;
                view.update(field, tet, state);
            }

            @Override
            public void onRestart() {
                field.clear();
                tet = new FallingTetrimino(RANDOM);
                state = GameState.RUN;
                view.update(field, tet, state);
                game_timer.start();
            }

            @Override
            public void onMenu() {
                state = GameState.MENU;
                view.update(field, tet, state);
                game_timer.stop();
            }

            @Override
            public void onCellLeftClick(int x, int y) {
                System.out.println("Левая кнопка " + x + " " + y + " нажата");
                view.update(field, tet, state);
            }

            @Override
            public void onCellRightClick(int x, int y) {
                System.out.println("Правая кнопка " + x + " " + y + " нажата");
                view.update(field, tet, state);
            }
        });

        game_timer = new Timer(1000, e -> {
            if (!tet.moveDown(field)) tet = new FallingTetrimino(RANDOM);
            view.update(field, tet, state);
        });
    }
}
