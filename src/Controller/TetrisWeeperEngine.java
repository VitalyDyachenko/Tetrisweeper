package Controller;

import Model.minesweeper.Field;
import Model.tetris.FallingTetrimino;
import Model.tetris.MoveCause;
import View.GameView;

import javax.swing.Timer;
import java.util.Random;

public class TetrisWeeperEngine {
    private Context context = new Context();

    private GameView view = new GameView();
    private Timer game_timer;
    private static final Random RANDOM = new Random();

    public TetrisWeeperEngine() {
        view.setInputHandler(new GameView.InputHandler() {
            @Override
            public void onLeft() {
                if (context.state == GameState.RUN) {
                    context.tet.moveLeft(context.field);
                    view.update(context);
                }
            }

            @Override
            public void onRight() {
                if (context.state == GameState.RUN) {
                    context.tet.moveRight(context.field);
                    view.update(context);
                }
            }

            @Override
            public void onDown() {
                if (context.state == GameState.RUN) {
                    if (!context.tet.moveDown(context, MoveCause.SOFT_DROP)) context.tet = new FallingTetrimino(RANDOM);
                    view.update(context);
                }
            }

            @Override
            public void onRotateLeft() {
                if (context.state == GameState.RUN) {
                    context.tet.rotateLeft(context.field);
                    view.update(context);
                }
            }

            @Override
            public void onRotateRight() {
                if (context.state == GameState.RUN) {
                    context.tet.rotateRight(context.field);
                    view.update(context);
                }
            }



            @Override
            public void onModeChanged(GameMode new_mode) {
                context.mode = new_mode;
                view.update(context);
            }

            @Override
            public void onRestart() {
                context.field.clear();
                context.tet = new FallingTetrimino(RANDOM);
                context.state = GameState.RUN;
                view.update(context);
                game_timer.start();
            }

            @Override
            public void onMenu() {
                context.state = GameState.MENU;
                view.update(context);
                game_timer.stop();
            }
            @Override
            public void onPause() {
                if (context.state == GameState.PAUSE) {
                    context.state = GameState.RUN;
                    game_timer.start();
                }
                else if (context.state == GameState.RUN) {
                    context.state = GameState.PAUSE;
                    game_timer.stop();
                }
                view.update(context);
            }

            @Override
            public void onCellLeftClick(int x, int y) {
                if (context.state == GameState.RUN) {
                    System.out.println("Левая кнопка " + x + " " + y + " нажата");
                    view.update(context);
                }
            }

            @Override
            public void onCellRightClick(int x, int y) {
                if (context.state == GameState.RUN) {
                    System.out.println("Правая кнопка " + x + " " + y + " нажата");
                    view.update(context);
                }
            }
        });

        game_timer = new Timer(1000, e -> {
            if (!context.tet.moveDown(context, MoveCause.GRAVITY)) context.tet = new FallingTetrimino(RANDOM);
            view.update(context);
            if (context.state == GameState.LOOSE) game_timer.stop();
        });
    }
}
