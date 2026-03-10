import UI.FieldDrawer;

import minesweeper.Field;

public class Main {
    public static void main(String[] args) {
        Field field = new Field();
        FieldDrawer f = new FieldDrawer();
        f.draw(field);
    }
}