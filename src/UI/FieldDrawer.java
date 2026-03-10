package UI;

import javax.swing.*;
import java.awt.*;

public class FieldDrawer {
    public void draw() {
        JFrame frame = new JFrame("Моё окно");
        JPanel panel = new JPanel(new GridLayout());

        frame.add(panel);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
