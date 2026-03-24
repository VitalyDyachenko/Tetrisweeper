package game;

import Model.minesweeper.Field;
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
    private Timer game_timer = new Timer(1000, e -> {
        gameLoop();
    });;
    private static final Random RANDOM = new Random();
    private BestScoreTable tetris_best = new BestScoreTable("tetris_best.properties");
    private BestScoreTable tetrisweeper_best = new BestScoreTable("tetrisweeper_best.properties");
    private MusicPlayer music_player = new MusicPlayer();

    private int lvl_goal = 5; // Сколько надо очистить линий до следующего lvl

    public TetrisWeeperEngine() {
        setInputHandlerActions();

        updateScores();
        music_player.playMusic(MusicType.MENU);
    }

    private void gameLoop() {
        context.field.clearLines(context);
        moveTetrimino(MoveCause.GRAVITY);
        view.update(context);
        if (context.state == GameState.LOSE) {
            game_timer.stop();
            music_player.playSound(MusicType.LOSE);
            music_player.stopMusic();
        }
        updateLVL();
    }

    private void startGame() {
        lvl_goal = 5;
        context.field.clear();
        context.score = 0;
        context.lines_cleared = 0;
        context.level = 1;
        context.hold_tet = null;
        context.was_hold = false;
        nextTetrimino();
        nextTetrimino();
        context.state = GameState.RUN;

        view.update(context);
        music_player.playMusic(MusicType.GAME);

        game_timer.setDelay(speed());
        game_timer.start();
    }
    void setInputHandlerActions() {
        view.setInputHandler(new InputHandler() {
            @Override
            public void onLeft() {
                if (context.state == GameState.RUN) {
                    if (context.tet.moveLeft(context.field))
                        music_player.playSound(MusicType.MOVE);
                    view.update(context);
                }
            }
            @Override
            public void onRight() {
                if (context.state == GameState.RUN) {
                    if (context.tet.moveRight(context.field))
                        music_player.playSound(MusicType.MOVE);
                    view.update(context);
                }
            }
            @Override
            public void onDown() {
                if (context.state == GameState.RUN) {
                    moveTetrimino(MoveCause.SOFT_DROP);
                    view.update(context);
                }
            }

            @Override
            public void onRotateLeft() {
                if (context.state == GameState.RUN) {
                    if (context.tet.rotateLeft(context))
                        music_player.playSound(MusicType.ROTATE);
                    view.update(context);
                }
            }
            @Override
            public void onRotateRight() {
                if (context.state == GameState.RUN) {
                    if (context.tet.rotateRight(context))
                        music_player.playSound(MusicType.ROTATE);
                    view.update(context);
                }
            }
            @Override
            public void onHardDrop() {
                if (context.state == GameState.RUN) {
                    boolean was_lines_cleared = context.tet.hardDrop(context);
                    music_player.playSound(MusicType.DROP);
                    if (was_lines_cleared) {
                        music_player.playSound(MusicType.LINE);
                    }
                    nextTetrimino();
                    view.update(context);
                }
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
                if (context.state == GameState.RUN && context.mode == GameMode.TETRISWEEPER) {
                    if (context.field.canOpen(x, y)) {
                        music_player.playSound(MusicType.CLICK);
                        Field.OpenResult res = context.field.openByPlayer(context, x, y);
                        if (res.mine_opened) {
                            view.update(context);
                            context.state = GameState.LOSE;
                            music_player.playSound(MusicType.BOMB);
                        }
                        else if (res.was_lines_cleared) {
                            music_player.playSound(MusicType.LINE);
                        }
                        view.update(context);
                    }
                }
            }
            @Override
            public void onCellRightClick(int x, int y) {
                if (context.state == GameState.RUN && context.mode == GameMode.TETRISWEEPER) {
                    boolean was_lines_cleared = context.field.flag(context, x, y);
                    if (was_lines_cleared) music_player.playSound(MusicType.LINE);
                    music_player.playSound(MusicType.FLAG);
                    view.update(context);
                }
            }



            @Override
            public void onStart() {
                if (context.state != GameState.RUN && context.state != GameState.PAUSE) {
                    startGame();
                }
            }
            @Override
            public void onRestart() {
                startGame();
            }
            @Override
            public void onMenu() {
                context.state = GameState.MENU;
                view.update(context);
                game_timer.stop();
                music_player.playMusic(MusicType.MENU);
            }
            @Override
            public void onPause() {
                if (context.state == GameState.PAUSE) {
                    context.state = GameState.RUN;
                    game_timer.start();
                    music_player.continueMusic();
                }
                else if (context.state == GameState.RUN) {
                    context.state = GameState.PAUSE;
                    game_timer.stop();
                    music_player.stopMusic();
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

            @Override
            public void onVolumeChanged(float volume) {
                music_player.setVolume(volume);
            }

            @Override
            public void onHelp() {
                view.addHelpWindow();
            }
        });
    }

    private void nextTetrimino() {
        context.tet = context.next_tet;
        context.next_tet = new FallingTetrimino(RANDOM);
    }
    private void moveTetrimino(MoveCause cause) {
        FallingTetrimino.MoveResult res = context.tet.moveDown(context, cause);
        if (res.landed) {
            nextTetrimino();
            music_player.playSound(MusicType.DROP);
            if (res.was_lines_cleared) {
                music_player.playSound(MusicType.LINE);
            }
        }
    }

    private void updateScores() {
        if (context.mode == GameMode.TETRIS)
            view.updateScores(tetris_best.getScores());
        else if (context.mode == GameMode.TETRISWEEPER)
            view.updateScores(tetrisweeper_best.getScores());
    }

    private void updateLVL() {
        if (context.level < 15) {
            if (context.lines_cleared >= lvl_goal) {
                lvl_goal += context.level * 5;
                context.level++;
                game_timer.setDelay(speed());
                music_player.playSound(MusicType.LVLUP);
            }
        }
    }
    private int speed() {
        int speed = switch (context.level) {
            case 1 -> 1000;
            case 2 -> 793;
            case 3 -> 618;
            case 4 -> 473;
            case 5 -> 355;
            case 6 -> 262;
            case 7 -> 190;
            case 8 -> 135;
            case 9 -> 94;
            case 10 -> 64;
            case 11 -> 43;
            case 12 -> 28;
            case 13 -> 18;
            case 14 -> 11;
            case 15 -> 7;
            default -> 10000;
        };
        if (context.mode == GameMode.TETRISWEEPER) speed = (int)(1.5*speed);
        return speed;
    }

}
