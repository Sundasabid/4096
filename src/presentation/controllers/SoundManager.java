package presentation.controllers;

import javafx.scene.media.AudioClip;

import java.net.URL;

public final class SoundManager {

    private SoundManager() {}

    private static boolean enabled = true;

    private static AudioClip click;
    private static AudioClip select;
    private static AudioClip error;
    private static AudioClip merge;
    private static boolean loaded = false;

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    private static void ensureLoaded() {
        if (loaded) return;
        loaded = true;

        // IMPORTANT: Never crash the whole game if media fails
        click = safeLoad("/resources/sounds/click.wav");
        select = safeLoad("/resources/sounds/select.wav");
        error = safeLoad("/resources/sounds/error.wav");
//        powerupSuccess = safeLoad("/sounds/powerup.wav");
        merge=safeLoad("/resources/sounds/merge.wav");
    }

    private static AudioClip safeLoad(String path) {
        try {
            URL url = SoundManager.class.getResource(path);
            if (url == null) {
                System.err.println("Sound file not found: " + path);
                return null;
            }
            return new AudioClip(url.toExternalForm());
        } catch (Throwable t) {
            // Catch Throwable to handle IllegalAccessError / linkage errors too
            System.err.println("Audio disabled (failed to load " + path + "): " + t);
            return null;
        }
    }

    private static void play(AudioClip clip) {
        if (!enabled) return;
        ensureLoaded();
        if (clip == null) return;

        try {
            clip.play();
        } catch (Throwable t) {
            System.err.println("Audio play failed: " + t);
        }
    }

    public static void click() {
        play(click);
    }

    public static void select() {
        play(select);
    }

    public static void error() {
        play(error);
    }

    public static void merge() {
        play(merge);
    }
}
