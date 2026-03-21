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
import java.io.File;
import java.util.*;
import java.util.List;

import static View.music.MusicPlayer.DEFAULT_VOLUME;

public class GameView {
    private static final Color MENU_COLOR = new Color(49, 49, 49);
    private static final Color MENU_TEXT_COLOR = new Color(210, 210, 210);
    private static final int GAME_INFO_PANEL_X = 168;

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
    private JLabel lvl_label;
    private JLabel next_tetrimino; // Картинка следющей фигуры
    private JLabel hold_tetrimino; // Картинка удержанной фигуры
    private JButton save_score_button;
    private JTextArea record_list;
    private JPanel stop_panel;
    JLabel volume_panel;

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
        game_frame.setIconImage(CellTexture.T_FLAG.getIcon().getImage());

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
        mode_label = new JLabel("TETRISWEEPER", SwingConstants.CENTER);
        mode_label.setFont(new Font("Arial", Font.BOLD, 18));
        mode_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        mode_label.setBackground(MENU_COLOR);
        mode_label.setOpaque(true);
        mode_label.setForeground(MENU_TEXT_COLOR);
        mode_label.setMaximumSize(new Dimension(GAME_INFO_PANEL_X, 30));

        // Счёт
        score_label = new JLabel("SCORE:");
        score_label.setFont(new Font("Arial", Font.BOLD, 18));
        score_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Уровень
        lvl_label = new JLabel("Leevl:");
        lvl_label.setFont(new Font("Arial", Font.BOLD, 18));
        lvl_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // SRS
        srs_label = new JLabel("SRS:");
        srs_label.setFont(new Font("Arial", Font.BOLD, 18));
        srs_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        srs_label.setForeground(new Color(123, 123, 123));


        // Панель со следующей фигурой
        JLabel next_label = new JLabel(" NEXT:");
        next_label.setFont(new Font("Arial", Font.BOLD, 18));
        next_label.setForeground(MENU_TEXT_COLOR);
        next_label.setAlignmentX(Component.CENTER_ALIGNMENT);

        next_tetrimino = new JLabel(TetriminoType.T.getIcon());

        JPanel next_panel = new JPanel(new BorderLayout());
        next_panel.setBackground(MENU_COLOR);
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
        hold_panel.setBackground(MENU_COLOR);
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
        game_info_panel.setBackground(new Color(192, 192, 192));
        game_info_panel.setPreferredSize(new Dimension(GAME_INFO_PANEL_X, Field.FIELD_Y * FieldDrawer.SIZE));
        game_info_panel.setLayout(new BoxLayout(game_info_panel, BoxLayout.Y_AXIS));

        game_info_panel.add(mode_label);
        game_info_panel.add(Box.createVerticalStrut(15));
        game_info_panel.add(next_panel);
        game_info_panel.add(Box.createVerticalStrut(15));
        game_info_panel.add(hold_panel);
        game_info_panel.add(Box.createVerticalStrut(15));
        game_info_panel.add(score_label);
        game_info_panel.add(Box.createVerticalStrut(5));
        game_info_panel.add(lvl_label);
        game_info_panel.add(Box.createVerticalStrut(20));
        game_info_panel.add(srs_label);
        game_info_panel.add(Box.createVerticalStrut(160));
        //                                <- Место под volume_label
        game_info_panel.add(Box.createVerticalStrut(20));
        game_info_panel.add(restart_button);
        game_info_panel.add(Box.createVerticalStrut(10));
        game_info_panel.add(menu_button);

        main_panel.add(game_info_panel, BorderLayout.EAST);
    }
    private void createMenuPanel() {
        // Заголовок
        JLabel title = new JLabel(new ImageIcon("resources/UI/Title.png"));
        //title.setForeground(Color.WHITE);
        //title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        Icon normal_icon = new ImageIcon("resources/UI/RadioButton icon 1.png");
        Icon selected_icon = new ImageIcon("resources/UI/RadioButton icon 2.png");

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

        // Переключатель громкости
        Icon volume_icon0 = new ImageIcon("resources/UI/Volume0.png");
        Icon volume_icon1 = new ImageIcon("resources/UI/Volume1.png");
        Icon volume_icon2 = new ImageIcon("resources/UI/Volume2.png");
        Icon volume_icon3 = new ImageIcon("resources/UI/Volume3.png");
        volume_panel = new JLabel(volume_icon2);
        volume_panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        volume_panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSlider volume_slider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(DEFAULT_VOLUME*100));
        volume_slider.setFocusable(false);
        volume_slider.setMaximumSize(new Dimension(118, 30));
        volume_slider.setPreferredSize(new Dimension(118, 30));
        volume_slider.setPaintLabels(false);
        volume_slider.setPaintTicks(false);
        volume_slider.setMajorTickSpacing(5);
        volume_slider.setMinorTickSpacing(5);
        volume_slider.setSnapToTicks(true);
        volume_slider.setOpaque(false);
        volume_slider.addChangeListener(e -> {
            float volume = volume_slider.getValue() / 100f;
            if (volume == 0)
                volume_panel.setIcon(volume_icon0);
            else if (volume <= 0.33f)
                volume_panel.setIcon(volume_icon1);
            else if (volume <= 0.66f)
                volume_panel.setIcon(volume_icon2);
            else
                volume_panel.setIcon(volume_icon3);
            if (input_handler != null) input_handler.onVolumeChanged(volume);
        });
        volume_slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(volume_slider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(86, 86, 86));
                g2.fillRect(trackRect.x, trackRect.y + trackRect.height/2 - 2, trackRect.width, 4);

                int fillWidth = (int)(trackRect.width * (volume_slider.getValue() / 100.0));
                g2.setColor(MENU_TEXT_COLOR);
                g2.fillRect(trackRect.x, trackRect.y + trackRect.height/2 - 2, fillWidth, 4);
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(MENU_TEXT_COLOR);
                g2.fillRect(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
        });
        volume_panel.add(volume_slider);


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

        // Помощь
        JButton help = new JButton("HELP");
        help.setFont(new Font("Arial", Font.BOLD, 20));
        help.setAlignmentX(Component.CENTER_ALIGNMENT);
        help.addActionListener(e -> {
            if (input_handler != null) input_handler.onHelp();
        });
        help.setFocusable(false);
        help.setMargin(new Insets(5, 30, 5, 30));
        help.setBackground(new Color(96, 96, 96));
        help.setForeground(MENU_TEXT_COLOR);
        help.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Кнопка старта
        JButton start_button = new JButton("START");
        start_button.setFont(new Font("Arial", Font.BOLD, 24));
        start_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        start_button.addActionListener(e -> {
            if (input_handler != null) input_handler.onStart();
        });
        start_button.setFocusable(false);
        start_button.setMargin(new Insets(5, 30, 5, 30));
        start_button.setBackground(MENU_TEXT_COLOR);
        start_button.setForeground(MENU_COLOR);
        start_button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Панель с кнопкой старта и помощи
        JPanel down_panel = new JPanel();
        down_panel.setBackground(MENU_COLOR);
        down_panel.setLayout(new BoxLayout(down_panel, BoxLayout.X_AXIS));
        down_panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        down_panel.add(help);
        down_panel.add(Box.createHorizontalStrut(20));
        down_panel.add(start_button);
        down_panel.setMaximumSize(new Dimension(300, 40));

        // Панель главного меню
        menu_panel = new JPanel();
        menu_panel.setBackground(MENU_COLOR);
        menu_panel.setLayout(new BoxLayout(menu_panel, BoxLayout.Y_AXIS));

        menu_panel.add(Box.createVerticalStrut(10));
        menu_panel.add(title);
        menu_panel.add(Box.createVerticalStrut(20));
        menu_panel.add(mode_panel);
        menu_panel.add(Box.createVerticalStrut(20));
        menu_panel.add(record_panel_title);
        menu_panel.add(record_panel);
        menu_panel.add(Box.createVerticalStrut(10));
        menu_panel.add(srs_checkbox);
        menu_panel.add(Box.createVerticalStrut(10));
        menu_panel.add(volume_panel); // Место под volume_panel
        menu_panel.add(Box.createVerticalStrut(20));
        //menu_panel.add(help);
        menu_panel.add(Box.createVerticalStrut(30));
        menu_panel.add(down_panel);

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
        if (context.state == GameState.RUN) field.update(context);

        if (context.hold_tet == null) hold_tetrimino.setIcon(null);
        else hold_tetrimino.setIcon(context.hold_tet.getType().getIcon());

        if (context.next_tet != null) next_tetrimino.setIcon(context.next_tet.getType().getIcon());

        stop_label.setText(context.state == GameState.LOSE ? "GAME OVER" : "PAUSE");
        mode_label.setText(context.mode.toString());
        score_label.setText("SCORE: " + context.score);
        srs_label.setText("SRS: " + (context.super_rotation_system ? "ON" : "OFF"));
        lvl_label.setText("Level: " + context.level);

        setVolumeLabel(context);

        updateMenuVisions(context);
    }
    private void setVolumeLabel(Context context) {
        if (context.state == GameState.MENU) {
            if (volume_panel.getParent() != menu_panel) {
                game_info_panel.remove(volume_panel);
                menu_panel.add(volume_panel, 10);
            }
        }
        else {
            if (volume_panel.getParent() != game_info_panel) {
                menu_panel.remove(volume_panel);
                game_info_panel.add(volume_panel, 12);
            }
        }
    }
    private void updateMenuVisions(Context context) {
        menu_panel.setVisible(context.state == GameState.MENU);
        game_info_panel.setVisible(
                        context.state == GameState.RUN ||
                        context.state == GameState.LOSE ||
                        context.state == GameState.PAUSE
        );
        game_panel.setVisible(
                        context.state == GameState.RUN ||
                        context.state == GameState.LOSE ||
                        context.state == GameState.PAUSE
        );
        stop_label.setVisible(context.state == GameState.LOSE || context.state == GameState.PAUSE);
        stop_panel.setVisible(context.state == GameState.LOSE || context.state == GameState.PAUSE);
        save_score_button.setVisible(context.state == GameState.LOSE);
    }

    public void addRecordWindow() {
        JFrame record_window = new JFrame("Save score");
        record_window.setIconImage(CellTexture.MINE.getIcon().getImage());

        JTextField text_field = new JTextField();
        text_field.setColumns(24);
        text_field.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;

                if ((getLength() + str.length()) <= 24) {
                    super.insertString(offs, str, a);
                }
            }
        });
        text_field.addActionListener(e -> {
            String text = text_field.getText();
            if (text.length() != 0) {
                if (input_handler != null) input_handler.onRecordAdd(text);
                record_window.dispose();
            }
        });

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Your name:"));
        panel.add(text_field);

        record_window.setSize(300, 100);
        record_window.setResizable(false);
        record_window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        record_window.setLocation(521, 440);
        record_window.setVisible(true);
        record_window.add(panel);
    }
    public void updateScores(List<Map.Entry<String, Integer>> scores) {
        StringBuilder sb = new StringBuilder();
        int place = 1;
        for (Map.Entry<String, Integer> entry : scores) {
            sb.append(String.format("%3d. %-24s %7d\n", place++, entry.getKey(), entry.getValue()));
        }

        record_list.setText(sb.toString());
    }

    public void addHelpWindow() {
        JFrame help_window = new JFrame("Help");
        help_window.setIconImage(new ImageIcon("resources/UI/help.png").getImage());
        help_window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        help_window.setSize(500, 400);
        help_window.setLocation(1000, 240);

        JTextArea help_text = new JTextArea();
        help_text.setEditable(false);
        help_text.setFont(new Font("Consolas", Font.BOLD, 18));
        help_text.setFocusable(false);
        help_text.setBackground(new Color(80, 80, 80));
        help_text.setForeground(MENU_TEXT_COLOR);

        JScrollPane help_panel = new JScrollPane(help_text);
        help_panel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        help_panel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        help_panel.setBorder(null);
        JScrollBar scrollbar = help_panel.getVerticalScrollBar();
        scrollbar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
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
        setHelpText(help_text);
        SwingUtilities.invokeLater(() -> scrollbar.setValue(0));


        help_window.add(help_panel);
        help_window.setVisible(true);
    }
    private void setHelpText(JTextArea help_text) {
        StringBuilder sb = new StringBuilder();
        sb.append("\tControls:\n");
        sb.append("[D|→]\t Move tetrimino right\n");
        sb.append("[A|←]\t Move tetrimino left\n");
        sb.append("[S|↓]\t Move tetrimino down\n");
        sb.append("\n");
        sb.append("[W|E|↑]\t Rotate tetrimino right\n");
        sb.append("[Q]\t Rotate tetrimino left\n");
        sb.append("\n");
        sb.append("[SPACE]\t Hard drop\n");
        sb.append("\n");
        sb.append("[C]\t Hold\n");
        sb.append("\n\n");
        sb.append("[RMB]\t Set flag\n");
        sb.append("[LMB]\t Open cell\n");
        sb.append("\n\n");
        sb.append("\tGame Description:\n");
        String text = """
                Tetrisweeper - смесь тетриса и сапёра.
                Изначально всё выглядит, как тетрис. Здесь
                точно также нужно управлять падающими
                фигурами и пытаться запонить ими линии.
                Но после запонения линии она не удаляется.
                
                Для этого клктки нужно открывать точно так
                же, как в классическом сапёре:
                В каждой клетке может оказаться мина, если
                открыть клетку с ней - это поражение.
                Но если открыть клетку без мины, то в ней
                отобразится число - количество мин вокруг
                неё (0-8). Таким образом можно вычислять
                мины. На предполагаемое место мины можно
                поставить флажок.
                
                Если все клетки в линии, которые имеют мины,
                будут помечены флажками, а все клетки без мин
                открыты, линия удалится.
                
                Но есть ключевые правила, которые я решил
                добавить, чтобы игра была интеремнее.
                Первое - клетки у границ блокируются, их
                открывать нельзя (визуально отображаются с
                крестом). Это сделано для того, чтобы
                нельзя было просто вычислить мину, когда
                фигура только приземлилась у открытой клетки.
                
                Второе правило, которое пришлось сделать -
                это исключение из первого. Ведь если накрыть
                пустое пространство клетками, то его больше
                никак не заполнить, ведь клетки над этой
                "дыркой" заблокированы, а значит их
                невозможно открыть и удалить. Я назвал это
                "Hole problem". И второе правило эту
                проблему решает: все клетки над такими
                дырками можно открывать.
                
                Чем больше линий будет очищено, тем больше
                очков. Удачи!
                """;
        sb.append(text);

        help_text.setText(sb.toString());
    }
}
