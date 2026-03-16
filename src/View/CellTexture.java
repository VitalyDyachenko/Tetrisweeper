package View;

import javax.swing.*;

public enum CellTexture {
    EMPTY("empty"),
    MINE("bomb"),
    N0("0"),
    N1("1"),
    N2("2"),
    N3("3"),
    N4("4"),
    N5("5"),
    N6("6"),
    N7("7"),
    N8("8"),
    O("O"),
    I("I"),
    S("S"),
    Z("Z"),
    L("L"),
    J("J"),
    T("T"),
    O_FLAG("O f"),
    I_FLAG("I f"),
    S_FLAG("S f"),
    Z_FLAG("Z f"),
    L_FLAG("L f"),
    J_FLAG("J f"),
    T_FLAG("T f"),
    O_X("O x"),
    I_X("I x"),
    S_X("S x"),
    Z_X("Z x"),
    L_X("L x"),
    J_X("J x"),
    T_X("T x");

    private final ImageIcon icon;

    CellTexture(String fileName) {
        this.icon = new ImageIcon("resources/" + fileName + ".png");
    }

    public ImageIcon getIcon() {
        return icon;
    }
}
