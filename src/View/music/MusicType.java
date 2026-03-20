package View.music;

import java.io.File;

public enum MusicType {
    BOMB("Bomb"),
    FLAG("Flag"),
    CLICK("Click"),
    LOSE("Lose"),
    LINE("Line"),
    ROTATE("Rotate"),
    MOVE("Move"),
    DROP("Drop"),

    MENU("Menu"),
    GAME("Game");

    private final File file;
    public File getFile() {return file;}

    MusicType(String filename) {
        this.file = new File("resources/" + filename + ".wav");
    }
}
