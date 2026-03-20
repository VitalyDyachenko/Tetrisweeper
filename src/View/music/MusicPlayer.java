package View.music;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MusicPlayer {
    private Clip background_music;
    private Map<MusicType, Clip> sounds = new HashMap<>();
    private float volume = 0.7f;

    private Clip getClip(MusicType type) {
        if (sounds.containsKey(type)) {
            return sounds.get(type);
        }

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(type.getFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            sounds.put(type, clip);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
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
                setVolume(clip);
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
                setVolume(clip);
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

    public void setVolume(Clip clip) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    }
}
