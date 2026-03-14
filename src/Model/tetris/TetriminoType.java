package Model.tetris;

import javax.swing.*;

public enum TetriminoType {
    I,
    O,
    S,
    Z,
    L,
    J,
    T;

    private final ImageIcon icon;

    TetriminoType() {
        this.icon = new ImageIcon("resources/" + this.toString() + " show.png");
    }

    public ImageIcon getIcon() {
        return icon;
    }
}
