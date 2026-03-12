import Controller.TetrisWeeperEngine;
import View.GameView;
import Model.minesweeper.Field;

public class Main {
    public static void main(String[] args) {
        TetrisWeeperEngine game = new TetrisWeeperEngine();
        game.run();
    }
}