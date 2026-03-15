package View;

import Controller.Context;
import Controller.GameMode;
import Controller.GameState;
import Model.minesweeper.Field;
import Model.tetris.TetriminoType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GameView {
    private JFrame game_frame; // Окно
    private JPanel main_panel; // Главная панель
    private JPanel menu_panel; // Панель главного меню
    private JLayeredPane game_panel; // Панель игры
    private JPanel game_info_panel; // Боковая панель игры

    private FieldDrawer field;
    private JLabel stop_label; // Текст о паузе / конце игры
    private JLabel mode_label; // Текст о режиме игры
    private JLabel score_label;
    private JLabel srs_label;
    private JLabel next_tetrimino; // Картинка следющей фигуры

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
    }
    private InputHandler input_handler;
    public void setInputHandler(InputHandler handler) {
        input_handler = handler;
    }

    public GameView() {
        game_frame = new JFrame("Tetrisweeper");
        game_frame.setSize(FieldDrawer.FRAME_X, FieldDrawer.FRAME_Y);
        game_frame.setResizable(false);
        game_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game_frame.setLocation(500, 10);

        main_panel = new JPanel(new BorderLayout());
        main_panel.setBackground(Color.GRAY);
        setupKeyBindings();
        game_frame.add(main_panel);

        createMenuPanel();
        createGamePanel();

        game_info_panel.setVisible(false);
        game_panel.setVisible(false);

        game_frame.setVisible(true);
    }

    private void createGamePanel() {
        // Панель игрового поля
        JPanel field_panel = new JPanel(new GridLayout(Field.FIELD_Y, Field.FIELD_X));
        field_panel.setBackground(Color.BLACK);
        field_panel.setPreferredSize(new Dimension(
                Field.FIELD_X * FieldDrawer.SIZE,
                Field.FIELD_Y * FieldDrawer.SIZE
        ));
        field_panel.setLayout(new GridLayout(Field.FIELD_Y, Field.FIELD_X));
        field_panel.setBounds(0, 0,
                Field.FIELD_X * FieldDrawer.SIZE,
                Field.FIELD_Y * FieldDrawer.SIZE
        );

        // Поле с кнопками
        field = new FieldDrawer(field_panel, new FieldDrawer.CellClickHandler() {
            @Override
            public void onLeftClick(int x, int y) {
                if (input_handler != null) input_handler.onCellLeftClick(x, y);
            }

            @Override
            public void onRightClick(int x, int y) {
                if (input_handler != null) input_handler.onCellRightClick(x, y);
            }
        });

        // Тексто о Паузе / Конце игры
        stop_label = new JLabel("GAME OVER", SwingConstants.CENTER);
        stop_label.setFont(new Font("Arial", Font.BOLD, 48));
        //stop_label.setOpaque(true);
        //stop_label.setBackground(new Color(255, 255, 255, 10));
        stop_label.setForeground(new Color(255, 255, 255, 150));
        stop_label.setBounds(0, 0,
                Field.FIELD_X * FieldDrawer.SIZE,
                Field.FIELD_Y * FieldDrawer.SIZE
        );
        stop_label.setVisible(false);

        // Панель игры
        game_panel = new JLayeredPane();
        game_panel.setPreferredSize(new Dimension(
                Field.FIELD_X * FieldDrawer.SIZE,
                Field.FIELD_Y * FieldDrawer.SIZE
        ));
        game_panel.add(stop_label, JLayeredPane.POPUP_LAYER);
        game_panel.add(field_panel, JLayeredPane.DEFAULT_LAYER);

        main_panel.add(game_panel, BorderLayout.WEST);

        createGameInfoPanel();
    }
    private void createGameInfoPanel() {
        // Текст о режиме игры
        mode_label = new JLabel("TETRISWEEPER");
        mode_label.setFont(new Font("Arial", Font.BOLD, 18));
        mode_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Счёт
        score_label = new JLabel("SCORE:");
        score_label.setFont(new Font("Arial", Font.BOLD, 18));
        score_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // SRS
        srs_label = new JLabel("SRS:");
        srs_label.setFont(new Font("Arial", Font.BOLD, 18));
        srs_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Панель со следующей фигурой
        JLabel next_label = new JLabel(" NEXT:");
        next_label.setFont(new Font("Arial", Font.BOLD, 18));
        next_label.setForeground(Color.WHITE);
        next_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        next_tetrimino = new JLabel(TetriminoType.T.getIcon());

        JPanel next_panel = new JPanel(new BorderLayout());
        next_panel.setBackground(Color.DARK_GRAY);
        next_panel.setPreferredSize(new Dimension(FieldDrawer.SIZE*4 + 4, FieldDrawer.SIZE*4 + 4));
        next_panel.setMaximumSize(new Dimension(FieldDrawer.SIZE*4 + 4, FieldDrawer.SIZE*4 + 4));
        next_panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        next_panel.add(next_label, BorderLayout.NORTH);
        next_panel.add(next_tetrimino, BorderLayout.CENTER);

        // Кнопка рестарта
        JButton restart_button = new JButton("RESTART");
        restart_button.setFont(new Font("Arial", Font.PLAIN, 16));
        restart_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        restart_button.addActionListener(e -> {
            if (input_handler != null) input_handler.onRestart();
        });
        restart_button.setFocusable(false);
        restart_button.setMargin(new Insets(5, 30, 5, 30));
        restart_button.setBackground(new Color(52, 52, 52));
        restart_button.setForeground(new Color(255, 255, 255));

        // Кнопка возврата в меню
        JButton menu_button = new JButton("MENU");
        menu_button.setFont(new Font("Arial", Font.PLAIN, 16));
        menu_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu_button.addActionListener(e -> {
            if (input_handler != null) input_handler.onMenu();
        });
        menu_button.setFocusable(false);
        menu_button.setMargin(new Insets(5, 44, 5, 44));
        menu_button.setBackground(new Color(93, 93, 93));
        menu_button.setForeground(new Color(255, 255, 255));

        // Вся боковая панель
        game_info_panel = new JPanel();
        game_info_panel.setBackground(Color.LIGHT_GRAY);
        game_info_panel.setPreferredSize(new Dimension(160, Field.FIELD_Y * FieldDrawer.SIZE));
        game_info_panel.setLayout(new BoxLayout(game_info_panel, BoxLayout.Y_AXIS));

        game_info_panel.add(mode_label);
        game_info_panel.add(Box.createVerticalStrut(16));
        game_info_panel.add(next_panel);
        game_info_panel.add(Box.createVerticalStrut(16));
        game_info_panel.add(srs_label);
        game_info_panel.add(Box.createVerticalStrut(10));
        game_info_panel.add(score_label);
        game_info_panel.add(Box.createVerticalStrut(440));
        game_info_panel.add(restart_button);
        game_info_panel.add(Box.createVerticalStrut(10));
        game_info_panel.add(menu_button);

        main_panel.add(game_info_panel, BorderLayout.EAST);
    }
    private void createMenuPanel() {
        // Заголовок
        JLabel title = new JLabel("TETRISWEEPER");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Кнопка режима Тетрис:
        JPanel tetris_mode_panel = new JPanel(new BorderLayout());
        tetris_mode_panel.setBackground(Color.DARK_GRAY);
        tetris_mode_panel.setMaximumSize(new Dimension(150, 30));
        tetris_mode_panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JRadioButton tetris_mode = new JRadioButton("Tetris");
        tetris_mode.setSelected(true);
        tetris_mode.setAlignmentX(Component.CENTER_ALIGNMENT);
        tetris_mode_panel.add(tetris_mode, BorderLayout.CENTER);
        tetris_mode.addActionListener(e -> {
            if (input_handler != null) input_handler.onModeChanged(GameMode.TETRIS);
        });

        // Кнопка режима Тетрис-спёр
        JPanel tetrisweeper_mode_panel = new JPanel(new BorderLayout());
        tetrisweeper_mode_panel.setBackground(Color.DARK_GRAY);
        tetrisweeper_mode_panel.setMaximumSize(new Dimension(150, 30));
        tetrisweeper_mode_panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JRadioButton tetrisweeper_mode = new JRadioButton("Tetrisweeper");
        tetrisweeper_mode.setAlignmentX(Component.CENTER_ALIGNMENT);
        tetrisweeper_mode_panel.add(tetrisweeper_mode, BorderLayout.CENTER);
        tetrisweeper_mode.addActionListener(e -> {
            if (input_handler != null) input_handler.onModeChanged(GameMode.TETRISWEEPER);
        });

        // Объединение tetrisweeper_mode и tetris_mode в группу
        ButtonGroup mode_group = new ButtonGroup();
        mode_group.add(tetris_mode);
        mode_group.add(tetrisweeper_mode);

        // Панель выбора режима игры
        JPanel mode_panel = new JPanel();
        mode_panel.setBackground(Color.DARK_GRAY);
        mode_panel.setLayout(new BoxLayout(mode_panel, BoxLayout.Y_AXIS));
        mode_panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mode_panel.add(tetris_mode_panel);
        mode_panel.add(tetrisweeper_mode_panel);

        // Кнопка выбора Super Rotation System
        JCheckBox srs_checkbox = new JCheckBox("Super Rotation System");
        srs_checkbox.setSelected(true);
        srs_checkbox.setBackground(Color.DARK_GRAY);
        srs_checkbox.setForeground(Color.WHITE);
        srs_checkbox.setFocusable(false);
        srs_checkbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        srs_checkbox.addActionListener(e -> {
            if (input_handler != null) {
                input_handler.onSRSChanged(srs_checkbox.isSelected());
            }
        });

        // Кнопка старта
        JButton start_button = new JButton("START");
        start_button.setFont(new Font("Arial", Font.PLAIN, 24));
        start_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        start_button.addActionListener(e -> {
            if (input_handler != null) input_handler.onStart();
        });
        start_button.setFocusable(false);
        start_button.setMargin(new Insets(5, 30, 5, 30));
        start_button.setBackground(new Color(96, 96, 96));
        start_button.setForeground(new Color(255, 255, 255));
        start_button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Панель главного меню
        menu_panel = new JPanel();
        menu_panel.setBackground(Color.DARK_GRAY);
        menu_panel.setLayout(new BoxLayout(menu_panel, BoxLayout.Y_AXIS));

        menu_panel.add(Box.createVerticalStrut(50));
        menu_panel.add(title);
        menu_panel.add(Box.createVerticalStrut(40));
        menu_panel.add(mode_panel);
        menu_panel.add(Box.createVerticalStrut(20));
        menu_panel.add(srs_checkbox);
        menu_panel.add(Box.createVerticalStrut(480));
        menu_panel.add(start_button);

        main_panel.add(menu_panel, BorderLayout.CENTER);
    }

    private void setupKeyBindings() {
        InputMap input_map = main_panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap action_map = main_panel.getActionMap();

        input_map.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        input_map.put(KeyStroke.getKeyStroke("A"), "moveLeft");
        action_map.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onLeft();
            }
        });

        input_map.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        input_map.put(KeyStroke.getKeyStroke("D"), "moveRight");
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

        input_map.put(KeyStroke.getKeyStroke("ESCAPE"), "switchPause");
        action_map.put("switchPause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onPause();
            }
        });

        input_map.put(KeyStroke.getKeyStroke("ENTER"), "switchStart");
        action_map.put("switchStart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onStart();
            }
        });
    }

    public void update(Context context) {
        if (context.state == GameState.RUN) {
            field.update(context.field, context.tet);
            next_tetrimino.setIcon(context.next_tet.getType().getIcon());
        }

        if (context.state == GameState.MENU) {
            menu_panel.setVisible(true);
            game_info_panel.setVisible(false);
            game_panel.setVisible(false);
        }
        else {
            menu_panel.setVisible(false);
            game_info_panel.setVisible(true);
            game_panel.setVisible(true);
            if (context.state == GameState.LOOSE) {
                stop_label.setText("GAME OVER");
                stop_label.setVisible(true);
            }
            else if (context.state == GameState.PAUSE) {
                stop_label.setText("PAUSE");
                stop_label.setVisible(true);
            }
            else {
                stop_label.setVisible(false);
            }
        }

        mode_label.setText(context.mode.toString());
        score_label.setText("SCORE: " + context.score);
        srs_label.setText("SRS: " + (context.super_rotation_system ? "ON" : "OFF"));
    }
}
