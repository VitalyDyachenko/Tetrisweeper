package View;

import game.GameMode;

public interface InputHandler {
    void onLeft();
    void onRight();
    void onDown();
    void onRotateLeft();
    void onRotateRight();

    void onCellLeftClick(int x, int y);
    void onCellRightClick(int x, int y);

    void onStart();
    void onRestart();
    void onMenu();
    void onModeChanged(GameMode mode);
    void onPause();
    void onSRSChanged(boolean enable);
    void onRecordAdd();
    void onRecordAdd(String name);
    void onHardDrop();
}
