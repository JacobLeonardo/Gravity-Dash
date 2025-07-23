package edu.commonwealthu.finalproject;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

/**
 * Provides game-related sound effects to an activity.
 *
 * @author Jacob Leonardo
 */
public class SoundManager {
    private SoundPool soundPool;
    private boolean soundEnabled = true;
    private final int thruPipe; // sound effect when going through a pipe
    private final int music; // background music
    private final int point; // point scored
    private final int collision; // contact collision

    /**
     * Initializes a new sound manager for a given context.
     * @param context of the given context
     */
    public SoundManager(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder().setMaxStreams(1)
                .setAudioAttributes(audioAttributes).build();

        thruPipe = soundPool.load(context, R.raw.through_pipe, 1);
        music = soundPool.load(context, R.raw.background_music, 1);
        point = soundPool.load(context, R.raw.point, 1);
        collision = soundPool.load(context, R.raw.collision, 1);
    }

    /**
     * @return soundEnabled
     */
    public boolean isSoundEnabled() {return soundEnabled;}


    /**
     * Toggles soundEnabled
     */
    public void toggleSoundEnabled() {
        soundEnabled = !soundEnabled;
    }


    /**
     * Plays the thrupipe sound
     */
    public void playPipeSound() { play(thruPipe); }


    /**
     * Plays the point sound
     */
    public void playPointSound() { play(point); }


    /**
     * Plays the collision sound
     */
    public void playCollisionSound() { play(collision); }


    /**
     * Plays a sound specified by its resource ID.
     */
    private void play(int id) {
        if (soundEnabled && soundPool != null) {
            soundPool.play(id, 1, 1, 0, 0, 1);
        }
    }
}
