package View.music;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private Clip clip;
    private float volume = 0.7f;
    private boolean looped;

    public MusicPlayer(boolean loop) {
        looped = loop;
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public void playMusic(MusicType type) {
        try {
            if (clip != null) {
                if (clip.isRunning()) clip.stop();
                clip.close();
            }
            File musicFile = type.getFile();
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            if (looped) clip.loop(Clip.LOOP_CONTINUOUSLY);
            setVolume(volume);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void continueMusic() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }

    public void setVolume(float vol) {
        volume = vol;
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    }
}
