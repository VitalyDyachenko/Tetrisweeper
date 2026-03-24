package game.best_score;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BestScoreTable {
    private Properties scores = new Properties();
    private String jarPath = BestScoreTable.class.getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath();
    private File jarFile;
    private File jarFolder;

    private File records_folder = new File(jarFolder, "records");
    private File file;

    public BestScoreTable(String name) {
        jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);
        jarFile = new File(jarPath);
        jarFolder = jarFile.getParentFile();
        records_folder.mkdirs();
        file = new File(records_folder, name);
        load();
    }

    private void save() {
        try (FileOutputStream os = new FileOutputStream(file)) {
            scores.store(os, "Tetrisweeper High Scores");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void load() {
        if (file.exists()) {
            try (FileInputStream is = new FileInputStream(file)) {
                scores.load(is);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addScore(String name, int score) {
        if (name.length() != 0) {
            scores.setProperty(name, String.valueOf(score));
            save();
        }
    }

    public List<Map.Entry<String, Integer>> getTopScores(int top) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>();

        for (String name : scores.stringPropertyNames()) {
            int score = Integer.parseInt(scores.getProperty(name));
            list.add(new AbstractMap.SimpleEntry<>(name, score));
        }

        // Сортируем по убыванию
        list.sort((a, b) -> b.getValue() - a.getValue());

        // Возвращаем только первые top рекордов
        return list.subList(0, Math.min(top, list.size()));
    }
    public List<Map.Entry<String, Integer>> getScores() {
        return getTopScores(Integer.MAX_VALUE);
    }
}
