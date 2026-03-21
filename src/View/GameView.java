package View;

import game.Context;
import game.GameMode;
import game.GameState;
import Model.minesweeper.Field;
import Model.tetris.TetriminoType;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class GameView {
    private static final Color MENU_COLOR = new Color(49, 49, 49);
    private static final Color MENU_TEXT_COLOR = new Color(210, 210, 210);

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
    private JLabel hold_tetrimino; // Картинка удержанной фигуры
    private JButton save_score_button;
    private JTextArea record_list;
    private JPanel stop_panel;

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
        ImageIcon icon = new ImageIcon("resources/icon.png");
        game_frame.setIconImage(icon.getImage());

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
        game_panel = new JLayeredPane();

        // Поле с кнопками
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

        // Тексто о Паузе / Конце игры
        stop_label = new JLabel("GAME OVER", SwingConstants.CENTER);
        stop_label.setFont(new Font("Arial", Font.BOLD, 48));
        //stop_label.setOpaque(true);
        //stop_label.setBackground(new Color(255, 255, 255, 10));
        stop_label.setForeground(new Color(255, 255, 255, 220));
        stop_label.setBounds(0, 0,
                Field.FIELD_X * FieldDrawer.SIZE,
                Field.FIELD_Y * FieldDrawer.SIZE
        );
        stop_label.setVisible(false);

        // Кнопка добавления рекорда
        save_score_button = new JButton("SAVE MY SCORE");
        save_score_button.setFont(new Font("Arial", Font.PLAIN, 16));
        save_score_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        save_score_button.addActionListener(e -> {
            if (input_handler != null) input_handler.onRecordAdd();
        });
        save_score_button.setFocusable(false);
        save_score_button.setMargin(new Insets(5, 30, 5, 30));
        save_score_button.setBackground(new Color(52, 52, 52, 220));
        save_score_button.setForeground(new Color(255, 255, 255, 220));
        save_score_button.setVisible(false);
        save_score_button.setBounds(
                (Field.FIELD_X * FieldDrawer.SIZE - 200) / 2,
                Field.FIELD_Y * FieldDrawer.SIZE / 2 + 50,
                200,
                50
        );

        stop_panel = new JPanel();
        stop_panel.setBackground(new Color(5, 5, 5, 180));
        stop_panel.setBounds(
                0,
                0,
                Field.FIELD_X * FieldDrawer.SIZE,
                Field.FIELD_Y * FieldDrawer.SIZE - 1
        );

        // Панель игры
        game_panel.setPreferredSize(new Dimension(
                Field.FIELD_X * FieldDrawer.SIZE,
                Field.FIELD_Y * FieldDrawer.SIZE
        ));
        game_panel.add(stop_panel, JLayeredPane.PALETTE_LAYER);
        game_panel.add(stop_label, JLayeredPane.POPUP_LAYER);
        game_panel.add(save_score_button, JLayeredPane.POPUP_LAYER);

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
        next_label.setForeground(MENU_TEXT_COLOR);
        next_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        next_tetrimino = new JLabel(TetriminoType.T.getIcon());

        JPanel next_panel = new JPanel(new BorderLayout());
        next_panel.setBackground(new Color(40, 40, 40));
        next_panel.setPreferredSize(new Dimension(FieldDrawer.SIZE*4 + 4, FieldDrawer.SIZE*4 + 4));
        next_panel.setMaximumSize(new Dimension(FieldDrawer.SIZE*4 + 4, FieldDrawer.SIZE*4 + 4));
        next_panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        next_panel.add(next_label, BorderLayout.NORTH);
        next_panel.add(next_tetrimino, BorderLayout.CENTER);

        // Панель с удержанной фигурой
        JLabel hold_label = new JLabel(" HOLD:");
        hold_label.setFont(new Font("Arial", Font.BOLD, 18));
        hold_label.setForeground(MENU_TEXT_COLOR);
        hold_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        hold_tetrimino = new JLabel(TetriminoType.T.getIcon());

        JPanel hold_panel = new JPanel(new BorderLayout());
        hold_panel.setBackground(new Color(40, 40, 40));
        //hold_panel.setPreferredSize(new Dimension(FieldDrawer.SIZE*4 + 4, FieldDrawer.SIZE*4 + 4));
        hold_panel.setMaximumSize(new Dimension(FieldDrawer.SIZE*4 + 4, FieldDrawer.SIZE*4 + 4));
        hold_panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hold_panel.add(hold_label, BorderLayout.NORTH);
        hold_panel.add(hold_tetrimino, BorderLayout.CENTER);

        // Кнопка рестарта
        JButton restart_button = new JButton("RESTART");
        restart_button.setFont(new Font("Arial", Font.BOLD, 16));
        restart_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        restart_button.addActionListener(e -> {
            if (input_handler != null) input_handler.onRestart();
        });
        restart_button.setFocusable(false);
        restart_button.setMargin(new Insets(5, 30, 5, 30));
        restart_button.setBackground(new Color(52, 52, 52));
        restart_button.setForeground(MENU_TEXT_COLOR);

        // Кнопка возврата в меню
        JButton menu_button = new JButton("MENU");
        menu_button.setFont(new Font("Arial", Font.BOLD, 16));
        menu_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu_button.addActionListener(e -> {
            if (input_handler != null) input_handler.onMenu();
        });
        menu_button.setFocusable(false);
        menu_button.setMargin(new Insets(5, 44, 5, 44));
        menu_button.setBackground(new Color(93, 93, 93));
        menu_button.setForeground(MENU_TEXT_COLOR);

        // Вся боковая панель
        game_info_panel = new JPanel();
        game_info_panel.setBackground(Color.LIGHT_GRAY);
        game_info_panel.setPreferredSize(new Dimension(160, Field.FIELD_Y * FieldDrawer.SIZE));
        game_info_panel.setLayout(new BoxLayout(game_info_panel, BoxLayout.Y_AXIS));

        game_info_panel.add(mode_label);
        game_info_panel.add(Box.createVerticalStrut(15));
        game_info_panel.add(next_panel);
        game_info_panel.add(Box.createVerticalStrut(15));
        game_info_panel.add(hold_panel);
        game_info_panel.add(Box.createVerticalStrut(15));
        game_info_panel.add(srs_label);
        game_info_panel.add(Box.createVerticalStrut(10));
        game_info_panel.add(score_label);
        game_info_panel.add(Box.createVerticalStrut(160));
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

        Icon normal_icon = new ImageIcon("resources/RadioButton icon 1.png");
        Icon selected_icon = new ImageIcon("resources/RadioButton icon 2.png");

        // Кнопка режима Тетрис:
        JPanel tetris_mode_panel = new JPanel(new BorderLayout());
        tetris_mode_panel.setBackground(MENU_COLOR);
        tetris_mode_panel.setMaximumSize(new Dimension(150, 30));
        tetris_mode_panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JRadioButton tetris_mode = new JRadioButton("Tetris");
        tetris_mode.setSelected(true);
        tetris_mode.setAlignmentX(Component.CENTER_ALIGNMENT);
        tetris_mode_panel.add(tetris_mode, BorderLayout.CENTER);
        tetris_mode.addActionListener(e -> {
            if (input_handler != null) input_handler.onModeChanged(GameMode.TETRIS);
        });
        tetris_mode.setBackground(MENU_COLOR);
        tetris_mode.setForeground(MENU_TEXT_COLOR);
        tetris_mode.setIcon(normal_icon);
        tetris_mode.setSelectedIcon(selected_icon);
        tetris_mode.setFocusPainted(false);
        tetris_mode.setFont(new Font("Arial", Font.BOLD, 20));

        // Кнопка режима Тетрис-спёр
        JPanel tetrisweeper_mode_panel = new JPanel(new BorderLayout());
        tetrisweeper_mode_panel.setBackground(MENU_COLOR);
        tetrisweeper_mode_panel.setMaximumSize(new Dimension(150, 30));
        tetrisweeper_mode_panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JRadioButton tetrisweeper_mode = new JRadioButton("Tetrisweeper");
        tetrisweeper_mode.setAlignmentX(Component.CENTER_ALIGNMENT);
        tetrisweeper_mode_panel.add(tetrisweeper_mode, BorderLayout.CENTER);
        tetrisweeper_mode.addActionListener(e -> {
            if (input_handler != null) input_handler.onModeChanged(GameMode.TETRISWEEPER);
        });
        tetrisweeper_mode.setBackground(MENU_COLOR);
        tetrisweeper_mode.setForeground(MENU_TEXT_COLOR);
        tetrisweeper_mode.setIcon(normal_icon);
        tetrisweeper_mode.setSelectedIcon(selected_icon);
        tetrisweeper_mode.setFocusPainted(false);
        tetrisweeper_mode.setFont(new Font("Arial", Font.BOLD, 20));

        // Объединение tetrisweeper_mode и tetris_mode в группу
        ButtonGroup mode_group = new ButtonGroup();
        mode_group.add(tetris_mode);
        mode_group.add(tetrisweeper_mode);

        // Панель выбора режима игры
        JPanel mode_panel = new JPanel();
        mode_panel.setBackground(MENU_COLOR);
        mode_panel.setLayout(new BoxLayout(mode_panel, BoxLayout.Y_AXIS));
        mode_panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mode_panel.add(tetris_mode_panel);
        mode_panel.add(tetrisweeper_mode_panel);

        // Кнопка выбора Super Rotation System
        JCheckBox srs_checkbox = new JCheckBox("Super Rotation System");
        srs_checkbox.setSelected(true);
        srs_checkbox.setFocusable(false);
        srs_checkbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        srs_checkbox.addActionListener(e -> {
            if (input_handler != null) {
                input_handler.onSRSChanged(srs_checkbox.isSelected());
            }
        });
        srs_checkbox.setBackground(MENU_COLOR);
        srs_checkbox.setForeground(MENU_TEXT_COLOR);
        srs_checkbox.setIcon(normal_icon);
        srs_checkbox.setSelectedIcon(selected_icon);
        //srs_checkbox.setFocusPainted(false);
        srs_checkbox.setFont(new Font("Arial", Font.BOLD, 20));

        // Таблица рекордов
        record_list = new JTextArea();
        record_list.setEditable(false);
        record_list.setFont(new Font("Consolas", Font.BOLD, 18));
        record_list.setFocusable(false);
        record_list.setBackground(new Color(80, 80, 80));
        record_list.setForeground(MENU_TEXT_COLOR);

        JScrollPane record_panel = new JScrollPane(record_list);
        record_panel.setPreferredSize(new Dimension(420, 200));
        record_panel.setMaximumSize(new Dimension(420, 200));
        record_panel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        record_panel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        record_panel.setBorder(null);
        JScrollBar verticalBar = record_panel.getVerticalScrollBar();
        verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(51, 51, 51);
                this.thumbDarkShadowColor = new Color(122, 122, 122);
                this.thumbHighlightColor = new Color(122, 122, 122);
                this.trackColor = new Color(40, 40, 40);
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });

        JLabel record_panel_title = new JLabel("BEST SCORE TABLE", SwingConstants.CENTER);
        record_panel_title.setAlignmentX(Component.CENTER_ALIGNMENT);
        record_panel_title.setForeground(MENU_TEXT_COLOR);
        record_panel_title.setFont(new Font("Arial", Font.BOLD, 20));
        record_panel_title.setBackground(new Color(101, 101, 101));
        record_panel_title.setOpaque(true);
        record_panel_title.setMaximumSize(new Dimension(420, 40));

        // Кнопка старта
        JButton start_button = new JButton("START");
        start_button.setFont(new Font("Arial", Font.BOLD, 24));
        start_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        start_button.addActionListener(e -> {
            if (input_handler != null) input_handler.onStart();
        });
        start_button.setFocusable(false);
        start_button.setMargin(new Insets(5, 30, 5, 30));
        start_button.setBackground(new Color(96, 96, 96));
        start_button.setForeground(MENU_TEXT_COLOR);
        start_button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Панель главного меню
        menu_panel = new JPanel();
        menu_panel.setBackground(MENU_COLOR);
        menu_panel.setLayout(new BoxLayout(menu_panel, BoxLayout.Y_AXIS));

        menu_panel.add(Box.createVerticalStrut(50));
        menu_panel.add(title);
        menu_panel.add(Box.createVerticalStrut(40));
        menu_panel.add(mode_panel);
        menu_panel.add(Box.createVerticalStrut(20));
        menu_panel.add(record_panel_title);
        menu_panel.add(record_panel);
        menu_panel.add(Box.createVerticalStrut(20));
        menu_panel.add(srs_checkbox);
        menu_panel.add(Box.createVerticalStrut(200));
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

        input_map.put(KeyStroke.getKeyStroke("S"), "moveDown");
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
        input_map.put(KeyStroke.getKeyStroke("E"), "rotateRight");
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

        input_map.put(KeyStroke.getKeyStroke("ENTER"), "start");
        action_map.put("start", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onStart();
            }
        });

        input_map.put(KeyStroke.getKeyStroke("SPACE"), "hardDrop");
        action_map.put("hardDrop", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onHardDrop();
            }
        });

        input_map.put(KeyStroke.getKeyStroke("C"), "hold");
        action_map.put("hold", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (input_handler != null) input_handler.onHold();
            }
        });
    }

    public void update(Context context) {
        field.setButtonsEnabled(context.state == GameState.RUN);
        if (context.state == GameState.RUN) {
            field.update(context);
            next_tetrimino.setIcon(context.next_tet.getType().getIcon());
            if (context.hold_tet == null) {
                hold_tetrimino.setIcon(null);
            }
            else {
                hold_tetrimino.setIcon(context.hold_tet.getType().getIcon());
            }
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
            if (context.state == GameState.LOSE) {
                stop_label.setText("GAME OVER");
                stop_label.setVisible(true);
                stop_panel.setVisible(true);
                save_score_button.setVisible(true);
            }
            else if (context.state == GameState.PAUSE) {
                stop_label.setText("PAUSE");
                stop_label.setVisible(true);
                stop_panel.setVisible(true);
                save_score_button.setVisible(false);
            }
            else {
                stop_label.setVisible(false);
                stop_panel.setVisible(false);
                save_score_button.setVisible(false);
            }
        }

        mode_label.setText(context.mode.toString());
        score_label.setText("SCORE: " + context.score);
        srs_label.setText("SRS: " + (context.super_rotation_system ? "ON" : "OFF"));
    }
    public void updateScores(List<Map.Entry<String, Integer>> scores) {
        StringBuilder sb = new StringBuilder();
        int place = 1;
        for (Map.Entry<String, Integer> entry : scores) {
            sb.append(String.format("%3d. %-24s %7d\n",
                    place++, entry.getKey(), entry.getValue()));
        }

        record_list.setText(sb.toString());
    }
    public void addRecordWindow() {
        JFrame record_window = new JFrame("Save score");

        JTextField text_field = new JTextField();
        text_field.setColumns(24);
        text_field.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;

                // Проверяем, не превысит ли лимит
                if ((getLength() + str.length()) <= 24) {
                    super.insertString(offs, str, a);
                }
            }
        });
        text_field.addActionListener(e -> {
            if (input_handler != null) input_handler.onRecordAdd(text_field.getText());
            record_window.dispose();
        });

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Your name:"));
        panel.add(text_field);

        record_window.setSize(300, 100);
        record_window.setResizable(false);
        record_window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        record_window.setLocation(540, 300);
        record_window.setVisible(true);
        record_window.add(panel);
    }
}
