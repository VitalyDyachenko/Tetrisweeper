package View;

import Controller.Context;
import Controller.GameMode;
import Controller.GameState;
import Model.minesweeper.Field;
import Model.tetris.FallingTetrimino;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GameView {
    private JFrame game_frame = new JFrame("Tetrisweeper");
    private JPanel game_panel = new JPanel(new GridLayout(Field.FIELD_Y, Field.FIELD_X));
    private FieldDrawer field;
    private JPanel main_panel = new JPanel(new BorderLayout());
    private JPanel game_info_panel = new JPanel();
    private JPanel menu_panel;
    private JLabel mode_label;
    JLabel score_label;

    private JButton restart_button;
    private JButton menu_button;
    private JRadioButton tetris_mode;
    private JRadioButton tetrisweeper_mode;
    private ButtonGroup mode_group;


    public interface InputHandler {
        void onLeft();
        void onRight();
        void onDown();
        void onRotateLeft();
        void onRotateRight();

        void onRestart();
        void onMenu();
        void onModeChanged(GameMode mode);

        void onCellLeftClick(int x, int y);
        void onCellRightClick(int x, int y);
    }
    private InputHandler input_handler;
    public void setInputHandler(InputHandler handler) {
        input_handler = handler;
    }

    public GameView() {
        game_frame.setSize(FieldDrawer.FRAME_X, FieldDrawer.FRAME_Y);
        game_frame.setResizable(false);
        game_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game_frame.setLocation(500, 50);

        createGameMenu();
        createMenuPanel();

        game_info_panel.setVisible(false);
        game_panel.setVisible(false);

        game_frame.setVisible(true);
    }

    private void createGameMenu() {
        field = new FieldDrawer(game_panel, new FieldDrawer.CellClickHandler() {
            @Override
            public void onLeftClick(int x, int y) {
                if (input_handler != null) input_handler.onCellLeftClick(x, y);
            }

            @Override
            public void onRightClick(int x, int y) {
                if (input_handler != null) input_handler.onCellRightClick(x, y);
            }
        });

        game_panel.setBackground(Color.BLACK);
        game_panel.setPreferredSize(new Dimension(
                Field.FIELD_X * FieldDrawer.SIZE,
                Field.FIELD_Y * FieldDrawer.SIZE
        ));

        main_panel.add(game_panel, BorderLayout.WEST);
        main_panel.setBackground(Color.GRAY);
        setupKeyBindings();
        game_frame.add(main_panel);

        createGameInfoPanel();
    }
    private void createGameInfoPanel() {
        game_info_panel.setBackground(Color.LIGHT_GRAY);
        game_info_panel.setPreferredSize(new Dimension(5 * FieldDrawer.SIZE, Field.FIELD_Y * FieldDrawer.SIZE));
        game_info_panel.setLayout(new BoxLayout(game_info_panel, BoxLayout.Y_AXIS));

        mode_label = new JLabel("TETRISWEEPER");
        mode_label.setFont(new Font("Arial", Font.BOLD, 18));
        mode_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        //mode_label.setForeground(new Color(255, 255, 255));

        score_label = new JLabel("SCORE:");
        score_label.setFont(new Font("Arial", Font.BOLD, 18));
        score_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel next_label = new JLabel("NEXT:");
        next_label.setFont(new Font("Arial", Font.BOLD, 18));
        next_label.setForeground(Color.WHITE);
        next_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel next_panel = new JPanel();
        next_panel.setBackground(Color.DARK_GRAY);
        next_panel.setPreferredSize(new Dimension(FieldDrawer.SIZE*4, FieldDrawer.SIZE*4));
        next_panel.setMaximumSize(new Dimension(FieldDrawer.SIZE*4, FieldDrawer.SIZE*4));
        next_panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        restart_button = new JButton("RESTART");
        restart_button.setFont(new Font("Arial", Font.PLAIN, 16));
        restart_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        restart_button.addActionListener(e -> {
            if (input_handler != null) input_handler.onRestart();
        });
        restart_button.setFocusable(false);
        restart_button.setMargin(new Insets(5, 30, 5, 30));
        restart_button.setBackground(new Color(52, 52, 52));
        restart_button.setForeground(new Color(255, 255, 255));


        menu_button = new JButton("MENU");
        menu_button.setFont(new Font("Arial", Font.PLAIN, 16));
        menu_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu_button.addActionListener(e -> {
            if (input_handler != null) input_handler.onMenu();
        });
        menu_button.setFocusable(false);
        menu_button.setMargin(new Insets(5, 44, 5, 44));
        menu_button.setBackground(new Color(93, 93, 93));
        menu_button.setForeground(new Color(255, 255, 255));


        game_info_panel.add(mode_label);
        game_info_panel.add(Box.createVerticalStrut(FieldDrawer.SIZE/2));
        next_panel.add(next_label);
        game_info_panel.add(next_panel);
        game_info_panel.add(Box.createVerticalStrut(FieldDrawer.SIZE/2));

        game_info_panel.add(Box.createVerticalStrut(FieldDrawer.SIZE));
        game_info_panel.add(score_label);
        game_info_panel.add(Box.createVerticalStrut(FieldDrawer.SIZE*10));
        game_info_panel.add(restart_button);
        game_info_panel.add(Box.createVerticalStrut(10));
        game_info_panel.add(menu_button);

        main_panel.add(game_info_panel, BorderLayout.EAST);
        game_info_panel.setVisible(true);
    }
    private void createMenuPanel() {
        menu_panel = new JPanel();
        menu_panel.setBackground(Color.DARK_GRAY);
        menu_panel.setLayout(new BoxLayout(menu_panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("TETRISWEEPER");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton start_button = new JButton("START");
        start_button.setFont(new Font("Arial", Font.PLAIN, 24));
        start_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        start_button.addActionListener(e -> {
            if (input_handler != null) input_handler.onRestart();
        });
        start_button.setFocusable(false);
        start_button.setMargin(new Insets(5, 30, 5, 30));
        start_button.setBackground(new Color(96, 96, 96));
        start_button.setForeground(new Color(255, 255, 255));

        JPanel mode_panel = new JPanel();
        mode_panel.setBackground(Color.DARK_GRAY);
        mode_panel.setLayout(new FlowLayout());

        tetris_mode = new JRadioButton("Tetris");
        //tetris_mode.setBackground(Color.DARK_GRAY);
        //tetris_mode.setForeground(Color.WHITE);
        tetris_mode.setSelected(true);

        tetrisweeper_mode = new JRadioButton("Tetrisweeper");
        //tetrisweeper_mode.setBackground(Color.DARK_GRAY);
        //tetrisweeper_mode.setForeground(Color.WHITE);

        mode_group = new ButtonGroup();
        mode_group.add(tetris_mode);
        mode_group.add(tetrisweeper_mode);

        tetris_mode.addActionListener(e -> {
            if (input_handler != null) input_handler.onModeChanged(GameMode.TETRIS);
        });
        tetrisweeper_mode.addActionListener(e -> {
            if (input_handler != null) input_handler.onModeChanged(GameMode.TETRISWEEPER);
        });

        menu_panel.add(Box.createVerticalStrut(100));
        menu_panel.add(title);
        menu_panel.add(Box.createVerticalStrut(50));
        mode_panel.add(tetris_mode);
        mode_panel.add(tetrisweeper_mode);
        menu_panel.add(mode_panel);
        menu_panel.add(Box.createVerticalStrut(20));
        menu_panel.add(start_button);

        main_panel.add(menu_panel, BorderLayout.CENTER);
    }

    private void setupKeyBindings() {
        InputMap input_map = main_panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap action_map = main_panel.getActionMap();

        input_map.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        action_map.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onLeft();
            }
        });

        input_map.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        action_map.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onRight();
            }
        });

        input_map.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        action_map.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onDown();
            }
        });

        input_map.put(KeyStroke.getKeyStroke("Q"), "rotateLeft");
        action_map.put("rotateLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onRotateLeft();
            }
        });

        input_map.put(KeyStroke.getKeyStroke("W"), "rotateRight");
        input_map.put(KeyStroke.getKeyStroke("UP"), "rotateRight");
        action_map.put("rotateRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onRotateRight();
            }
        });
    }

    public void update(Context context) {
        field.update(context.field, context.tet);

        if (context.state == GameState.MENU) {
            menu_panel.setVisible(true);
            game_info_panel.setVisible(false);
            game_panel.setVisible(false);
        }
        else {
            menu_panel.setVisible(false);
            game_info_panel.setVisible(true);
            game_panel.setVisible(true);
        }

        mode_label.setText(context.mode.toString());
        score_label.setText("SCORE: " + context.score);
    }
}
