package UI;

import minesweeper.Field;

import javax.swing.*;
import java.awt.*;

public class FieldDrawer {
    public static final int SIZE = 34;
    public void draw(Field f) {
        JFrame frame = new JFrame("Моё окно");
        frame.setSize(Field.FIELD_X * SIZE, Field.FIELD_Y * SIZE);
        JPanel panel = new JPanel(new GridLayout(Field.FIELD_Y, Field.FIELD_X));
        for (int i = 0; i < Field.FIELD_X*Field.FIELD_Y; i++) {
            ImageIcon icon = new ImageIcon("textures/empty.png");

            JButton b = new JButton(icon);
            b.setFocusable(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);

            panel.add(b);
        }
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
