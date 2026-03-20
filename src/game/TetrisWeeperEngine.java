package game;

import Model.tetris.FallingTetrimino;
import Model.tetris.MoveCause;
import View.GameView;
import View.InputHandler;
import View.music.MusicPlayer;
import View.music.MusicType;
import game.best_score.BestScoreTable;

import javax.swing.Timer;
import java.util.Random;

public class TetrisWeeperEngine {
    private Context context = new Context();

    private GameView view = new GameView();
    private Timer game_timer;
    private static final Random RANDOM = new Random();
    private BestScoreTable tetris_best = new BestScoreTable("src/game/best_score/tetris_best.properties");
    private BestScoreTable tetrisweeper_best = new BestScoreTable("src/game/best_score/tetrisweeper_best.properties");

    public TetrisWeeperEngine() {
        view.setInputHandler(new InputHandler() {
            @Override
            public void onLeft() {
                if (context.state == GameState.RUN) {
                    context.tet.moveLeft(context.field);
                    view.update(context);
                }
                //context.sound_player.playMusic(MusicType.MOVE);
            }
            @Override
            public void onRight() {
                if (context.state == GameState.RUN) {
                    context.tet.moveRight(context.field);
                    view.update(context);
                }
                //context.sound_player.playMusic(MusicType.MOVE);
            }
            @Override
            public void onDown() {
                if (context.state == GameState.RUN) {
                    if (!context.tet.moveDown(context, MoveCause.SOFT_DROP)) nextTetrimino();
                    view.update(context);
                }
            }

            @Override
            public void onRotateLeft() {
                if (context.state == GameState.RUN) {
                    context.tet.rotateLeft(context);
                    view.update(context);
                }
                //context.sound_player.playMusic(MusicType.ROTATE);
            }
            @Override
            public void onRotateRight() {
                if (context.state == GameState.RUN) {
                    context.tet.rotateRight(context);
                    view.update(context);
                }
                //context.sound_player.playMusic(MusicType.ROTATE);
            }
            @Override
            public void onHardDrop() {
                if (context.state == GameState.RUN) {
                    context.tet.hardDrop(context);
                    nextTetrimino();
                    view.update(context);
                }
                view.update(context);
            }
            @Override
            public void onHold() {
                if (!context.was_hold) {
                    FallingTetrimino t = context.hold_tet;
                    context.hold_tet = context.tet;
                    if (t == null) {
                        nextTetrimino();
                    } else {
                        context.tet = t;
                        context.tet.setToStart();
                    }
                    context.was_hold = true;
                    view.update(context);
                }
            }



            @Override
            public void onCellLeftClick(int x, int y) {
                if (context.state == GameState.RUN) {
                    //System.out.println("Левая кнопка " + x + " " + y + " нажата");
                    if (context.field.open(context, x, y, true)) {
                        view.update(context);
                        context.state = GameState.LOSE;
                        context.sound_player.playMusic(MusicType.BOMB);
                    }
                    else {
                        context.sound_player.playMusic(MusicType.CLICK);
                    }
                    view.update(context);
                }
            }
            @Override
            public void onCellRightClick(int x, int y) {
                if (context.state == GameState.RUN) {
                    //System.out.println("Правая кнопка " + x + " " + y + " нажата");
                    context.field.flag(context, x, y);
                    context.sound_player.playMusic(MusicType.FLAG);
                    view.update(context);
                }
            }



            @Override
            public void onStart() {
                if (context.state != GameState.RUN) {
                    context.field.clear();
                    context.score = 0;
                    context.hold_tet = null;
                    nextTetrimino();
                    nextTetrimino();
                    context.state = GameState.RUN;
                    view.update(context);
                    game_timer.start();
                    context.music_player.playMusic(MusicType.GAME);
                }
            }
            @Override
            public void onRestart() {
                context.field.clear();
                context.score = 0;
                context.hold_tet = null;
                nextTetrimino();
                nextTetrimino();
                context.state = GameState.RUN;
                view.update(context);
                game_timer.start();
                context.music_player.playMusic(MusicType.GAME);
            }
            @Override
            public void onMenu() {
                context.state = GameState.MENU;
                view.update(context);
                game_timer.stop();
                context.music_player.playMusic(MusicType.MENU);
            }
            @Override
            public void onPause() {
                if (context.state == GameState.PAUSE) {
                    context.state = GameState.RUN;
                    game_timer.start();
                    context.music_player.continueMusic();
                }
                else if (context.state == GameState.RUN) {
                    context.state = GameState.PAUSE;
                    game_timer.stop();
                    context.music_player.stopMusic();
                }
                view.update(context);
            }

            @Override
            public void onModeChanged(GameMode new_mode) {
                context.mode = new_mode;
                view.update(context);
                updateScores();
            }
            @Override
            public void onSRSChanged(boolean enable) {
                context.super_rotation_system = enable;
                view.update(context);
            }

            @Override
            public void onRecordAdd() {
                view.addRecordWindow();
                view.update(context);
            }
            @Override
            public void onRecordAdd(String name) {
                if (context.mode == GameMode.TETRIS) {
                    tetris_best.addScore(name, context.score);
                }
                else if (context.mode == GameMode.TETRISWEEPER) {
                    tetrisweeper_best.addScore(name, context.score);
                }
                view.update(context);
                updateScores();
            }
        });

        game_timer = new Timer(1000, e -> {
            context.field.clearLines(context);
            if (!context.tet.moveDown(context, MoveCause.GRAVITY)) nextTetrimino();
            view.update(context);
            if (context.state == GameState.LOSE) {
                game_timer.stop();
                if (!context.sound_player.isPlaying()) context.sound_player.playMusic(MusicType.LOSE);
                context.music_player.stopMusic();
            }
        });

        updateScores();
        context.music_player.playMusic(MusicType.MENU);
    }

    private void nextTetrimino() {
        context.tet = context.next_tet;
        context.next_tet = new FallingTetrimino(RANDOM);
    }

    private void updateScores() {
        if (context.mode == GameMode.TETRIS)
            view.updateScores(tetris_best.getScores());
        else if (context.mode == GameMode.TETRISWEEPER)
            view.updateScores(tetrisweeper_best.getScores());
    }
}
