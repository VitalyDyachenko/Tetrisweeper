package View;

import javax.swing.*;

public enum CellTexture {
    EMPTY("empty"),
    O("O"),
    I("I"),
    S("S"),
    Z("Z"),
    L("L"),
    J("J"),
    T("T");

    private final ImageIcon icon;

    CellTexture(String fileName) {
        this.icon = new ImageIcon("resources/" + fileName + ".png");
    }

    public ImageIcon getIcon() {
        return icon;
    }
}
