package com.everyday.breadwinner;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class SoundPlayer {
    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 5;

    private static SoundPool soundPool;
    private static int hitCorrectBasket;
    private static int hitWrongBasket;
    private static int levelPassed;
    private static int levelFailed;
    private static int buttonClicked;


    public SoundPlayer(Context context) {
        audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(SOUND_POOL_MAX)
                .build();

        hitCorrectBasket = soundPool.load(context, R.raw.tone_correct, 1);
        hitWrongBasket = soundPool.load(context, R.raw.tone_error, 1);
        levelPassed = soundPool.load(context, R.raw.tone_success, 1);
        levelFailed = soundPool.load(context, R.raw.tone_failed, 1);
        buttonClicked = soundPool.load(context, R.raw.button_click, 1);
    }

    public void playHitCorrectBasket () {
        soundPool.play(hitCorrectBasket, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitWrongBasket () {
        soundPool.play(hitWrongBasket, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playLevelPassed () {
        soundPool.play(levelPassed, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playLevelFailed () {
        soundPool.play(levelFailed, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playButtonClicked () {
        soundPool.play(buttonClicked, 1.0f, 1.0f, 1, 0, 1.0f);
    }

}
