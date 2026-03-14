package View;

import javax.swing.*;

public enum CellTexture {
    EMPTY("empty"),
    T("T");
    private final ImageIcon icon;

    CellTexture(String fileName) {
        this.icon = new ImageIcon("textures/" + fileName + ".png");
    }

    public ImageIcon getIcon() {
        return icon;
    }
}
