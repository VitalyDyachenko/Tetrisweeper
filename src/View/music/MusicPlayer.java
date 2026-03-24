package View.music;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MusicPlayer {
    public static final float DEFAULT_VOLUME = 0.7f;

    private Clip background_music;
    private Map<MusicType, Clip> sounds = new HashMap<>();
    private float volume = DEFAULT_VOLUME;

    private Clip getClip(MusicType type) {
        if (sounds.containsKey(type)) {
            return sounds.get(type);
        }

        try {
            InputStream is = getClass().getResourceAsStream(type.getFilePath());
            if (is == null) {
                return null;
            }
            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            sounds.put(type, clip);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setVolume(float new_v) {
        volume = new_v;
        changeVolume(background_music);
    }

    public boolean isPlaying() {
        return background_music != null && background_music.isRunning();
    }

    public void playMusic(MusicType type) {
        try {
            if (background_music != null) {
                if (background_music.isRunning()) background_music.stop();
            }

            Clip clip = getClip(type);
            if (clip != null) {
                clip.setFramePosition(0);
                background_music = clip;
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                changeVolume(clip);
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSound(MusicType type) {
        try {
            Clip clip = getClip(type);
            if (clip != null) {
                clip.setFramePosition(0);
                changeVolume(clip);
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (background_music != null && background_music.isRunning()) {
            background_music.stop();
        }
    }

    public void continueMusic() {
        if (background_music != null && !background_music.isRunning()) {
            background_music.start();
        }
    }

    public void changeVolume(Clip clip) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    }
}
