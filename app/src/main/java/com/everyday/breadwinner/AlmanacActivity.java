package com.everyday.breadwinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Objects;

public class AlmanacActivity extends AppCompatActivity {
    private View decorView;
    HomeWatcher mHomeWatcher;
    boolean musicFlag, soundFlag = true;
    private SoundPlayer soundPlayer;
    private View mainLayout;

    Dialog mainMenuDialog;

    private ImageView selectedBread;
    private TextView breadNum, breadName, availability;
    private Button hamburger, prevBread, nextBread;
    private LinearLayout headerCon;
    int currentBread = 1;

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private int revealX;
    private int revealY;

    private SharedPreferences dataLevel;
    int day1Status, day2Status, day3Status, day4Status, day5Status, day6Status;
    int day7Status, day8Status, day9Status, day10Status, day11Status, day12Status;
    int day13Status, day14Status, day15Status, day16Status, day17Status, day18Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almanac);

        // decorView
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
        mainLayout = findViewById(R.id.mainLayout);
        final Intent intent = getIntent();
        if (savedInstanceState == null && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            mainLayout.setVisibility(View.INVISIBLE);
            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);


            ViewTreeObserver viewTreeObserver = mainLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            mainLayout.setVisibility(View.VISIBLE);
        }

        soundPlayer = new SoundPlayer(this);

        // Background Music
        SharedPreferences loadToggleState = this.getSharedPreferences("MusicStatus", Context.MODE_PRIVATE);
        musicFlag = loadToggleState.getBoolean("music", true); //0 is the default value

        // Bind Music
        if (musicFlag) {
            doBindService();
            Intent music = new Intent();
            music.setClass(this, HomeMusicService.class);
            startService(music);
        }

        // Add Home Watcher
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();

        hamburger = findViewById(R.id.btnHamburger);
        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMenu();
                soundPlayer.playButtonClicked();
                YoYo.with(Techniques.Pulse)
                        .duration(400)
                        .playOn(hamburger);
            }
        });

        // Dialog
        mainMenuDialog = new Dialog(this);

        // Call SharedPreference for Day Status
        dataLevel = getSharedPreferences("LEVEL_DATA", Context.MODE_PRIVATE);
        day1Status = dataLevel.getInt("LEVEL_1_STATUS", 0);
        day2Status = dataLevel.getInt("LEVEL_2_STATUS", 0);
        day3Status = dataLevel.getInt("LEVEL_3_STATUS", 0);
        day4Status = dataLevel.getInt("LEVEL_4_STATUS", 0);
        day5Status = dataLevel.getInt("LEVEL_5_STATUS", 0);
        day6Status = dataLevel.getInt("LEVEL_6_STATUS", 0);
        day7Status = dataLevel.getInt("LEVEL_7_STATUS", 0);
        day8Status = dataLevel.getInt("LEVEL_8_STATUS", 0);
        day9Status = dataLevel.getInt("LEVEL_9_STATUS", 0);
        day10Status = dataLevel.getInt("LEVEL_10_STATUS", 0);
        day11Status = dataLevel.getInt("LEVEL_11_STATUS", 0);
        day12Status = dataLevel.getInt("LEVEL_12_STATUS", 0);
        day13Status = dataLevel.getInt("LEVEL_13_STATUS", 0);
        day14Status = dataLevel.getInt("LEVEL_14_STATUS", 0);
        day15Status = dataLevel.getInt("LEVEL_15_STATUS", 0);
        day16Status = dataLevel.getInt("LEVEL_16_STATUS", 0);
        day17Status = dataLevel.getInt("LEVEL_17_STATUS", 0);
        day18Status = dataLevel.getInt("LEVEL_18_STATUS", 0);

        // Select Current Bread
        headerCon = findViewById(R.id.headerCon);
        breadNum = findViewById(R.id.breadNum);
        breadName = findViewById(R.id.breadName);
        availability = findViewById(R.id.availability);
        selectedBread = findViewById(R.id.selectedBread);

        // Next Button
        nextBread = findViewById(R.id.btnNext);
        nextBread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                YoYo.with(Techniques.Pulse)
                        .duration(400)
                        .playOn(nextBread);

                YoYo.with(Techniques.BounceInDown)
                        .duration(400)
                        .playOn(headerCon);

                YoYo.with(Techniques.BounceInDown)
                        .duration(400)
                        .playOn(selectedBread);

                if (currentBread == 1) {
                    currentBread += 1;
                    breadNum.setText(R.string.b2);
                    breadName.setText(R.string.nb2);
                    availability.setText(R.string.ud1);
                    selectedBread.setImageResource(R.drawable.bread_2);
                    // Show Previous Button
                    prevBread.setVisibility(View.VISIBLE);
                }
                else if (currentBread == 2) {
                    currentBread += 1;
                    breadNum.setText(R.string.b3);
                    breadName.setText(R.string.nb3);
                    availability.setText(R.string.ud3);
                    selectedBread.setImageResource(R.drawable.bread_3);
                }
                else if (currentBread == 3) {
                    currentBread += 1;
                    breadNum.setText(R.string.b4);
                    if (day1Status == 1) {
                        breadName.setText(R.string.nb4);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    breadName.setText(R.string.nb4);
                    availability.setText(R.string.ud4);
                    selectedBread.setImageResource(R.drawable.bread_4);
                }
                else if (currentBread == 4) {
                    currentBread += 1;
                    breadNum.setText(R.string.b5);
                    if (day2Status == 1) {
                        breadName.setText(R.string.nb5);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud5);
                    selectedBread.setImageResource(R.drawable.bread_5);
                }
                else if (currentBread == 5) {
                    currentBread += 1;
                    breadNum.setText(R.string.b6);
                    if (day3Status == 1) {
                        breadName.setText(R.string.nb6);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud6);
                    selectedBread.setImageResource(R.drawable.bread_6);
                }
                else if (currentBread == 6) {
                    currentBread += 1;
                    breadNum.setText(R.string.b7);
                    if (day4Status == 1) {
                        breadName.setText(R.string.nb7);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud7);
                    selectedBread.setImageResource(R.drawable.bread_7);
                }
                else if (currentBread == 7) {
                    currentBread += 1;
                    breadNum.setText(R.string.b8);
                    if (day5Status == 1) {
                        breadName.setText(R.string.nb8);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud8);
                    selectedBread.setImageResource(R.drawable.bread_8);
                }
                else if (currentBread == 8) {
                    currentBread += 1;
                    breadNum.setText(R.string.b9);
                    if (day6Status == 1) {
                        breadName.setText(R.string.nb9);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud9);
                    selectedBread.setImageResource(R.drawable.bread_9);
                }
                else if (currentBread == 9) {
                    currentBread += 1;
                    breadNum.setText(R.string.b10);
                    if (day6Status == 1) {
                        breadName.setText(R.string.nb10);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud10);
                    selectedBread.setImageResource(R.drawable.bread_10);
                }
                else if (currentBread == 10) {
                    currentBread += 1;
                    breadNum.setText(R.string.b11);
                    if (day7Status == 1) {
                        breadName.setText(R.string.nb11);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud11);
                    selectedBread.setImageResource(R.drawable.bread_11);
                }
                else if (currentBread == 11) {
                    currentBread += 1;
                    breadNum.setText(R.string.b12);
                    if (day8Status == 1) {
                        breadName.setText(R.string.nb12);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud12);
                    selectedBread.setImageResource(R.drawable.bread_12);
                }
                else if (currentBread == 12) {
                    currentBread += 1;
                    breadNum.setText(R.string.b13);
                    if (day9Status == 1) {
                        breadName.setText(R.string.nb13);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud13);
                    selectedBread.setImageResource(R.drawable.bread_13);
                }
                else if (currentBread == 13) {
                    currentBread += 1;
                    breadNum.setText(R.string.b14);
                    if (day10Status == 1) {
                        breadName.setText(R.string.nb14);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud14);
                    selectedBread.setImageResource(R.drawable.bread_14);
                }
                else if (currentBread == 14) {
                    currentBread += 1;
                    breadNum.setText(R.string.b15);
                    if (day11Status == 1) {
                        breadName.setText(R.string.nb15);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud15);
                    selectedBread.setImageResource(R.drawable.bread_15);
                }
                else if (currentBread == 15) {
                    currentBread += 1;
                    breadNum.setText(R.string.b16);
                    if (day12Status == 1) {
                        breadName.setText(R.string.nb16);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud16);
                    selectedBread.setImageResource(R.drawable.bread_16);
                }
                else if (currentBread == 16) {
                    currentBread += 1;
                    breadNum.setText(R.string.b17);
                    if (day12Status == 1) {
                        breadName.setText(R.string.nb17);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud17);
                    selectedBread.setImageResource(R.drawable.bread_17);
                }
                else if (currentBread == 17) {
                    currentBread += 1;
                    breadNum.setText(R.string.b18);
                    if (day13Status == 1) {
                        breadName.setText(R.string.nb18);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud18);
                    selectedBread.setImageResource(R.drawable.bread_18);
                }
                else if (currentBread == 18) {
                    currentBread += 1;
                    breadNum.setText(R.string.b19);
                    if (day14Status == 1) {
                        breadName.setText(R.string.nb19);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }

                    availability.setText(R.string.ud19);
                    selectedBread.setImageResource(R.drawable.bread_19);
                }
                else if (currentBread == 19) {
                    currentBread += 1;
                    breadNum.setText(R.string.b20);
                    if (day15Status == 1) {
                        breadName.setText(R.string.nb20);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud20);
                    selectedBread.setImageResource(R.drawable.bread_20);
                }
                else if (currentBread == 20) {
                    currentBread += 1;
                    breadNum.setText(R.string.b21);
                    if (day16Status == 1) {
                        breadName.setText(R.string.nb21);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud21);
                    selectedBread.setImageResource(R.drawable.bread_21);
                }
                else if (currentBread == 21) {
                    currentBread += 1;
                    breadNum.setText(R.string.b22);
                    if (day17Status == 1) {
                        breadName.setText(R.string.nb22);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud22);
                    selectedBread.setImageResource(R.drawable.bread_22);
                    // Hide Next Button
                    nextBread.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Previous Button
        prevBread = findViewById(R.id.btnPrev);
        prevBread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                YoYo.with(Techniques.Pulse)
                        .duration(400)
                        .playOn(prevBread);

                YoYo.with(Techniques.BounceInDown)
                        .duration(400)
                        .playOn(headerCon);

                YoYo.with(Techniques.BounceInDown)
                        .duration(400)
                        .playOn(selectedBread);

                if (currentBread == 22) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b21);
                    if (day16Status == 1) {
                        breadName.setText(R.string.nb21);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud21);
                    selectedBread.setImageResource(R.drawable.bread_21);
                    // Show Next Button
                    nextBread.setVisibility(View.VISIBLE);
                }
                else if (currentBread == 21) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b20);
                    if (day15Status == 1) {
                        breadName.setText(R.string.nb20);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud20);
                    selectedBread.setImageResource(R.drawable.bread_20);
                }
                else if (currentBread == 20) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b19);
                    if (day14Status == 1) {
                        breadName.setText(R.string.nb19);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud19);
                    selectedBread.setImageResource(R.drawable.bread_19);
                }
                else if (currentBread == 19) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b18);
                    if (day13Status == 1) {
                        breadName.setText(R.string.nb18);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud18);
                    selectedBread.setImageResource(R.drawable.bread_18);
                }
                else if (currentBread == 18) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b17);
                    if (day12Status == 1) {
                        breadName.setText(R.string.nb17);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud17);
                    selectedBread.setImageResource(R.drawable.bread_17);
                }
                else if (currentBread == 17) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b16);
                    if (day12Status == 1) {
                        breadName.setText(R.string.nb16);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud16);
                    selectedBread.setImageResource(R.drawable.bread_16);
                }
                else if (currentBread == 16) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b15);
                    if (day12Status == 1) {
                        breadName.setText(R.string.nb15);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud15);
                    selectedBread.setImageResource(R.drawable.bread_15);
                }
                else if (currentBread == 15) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b14);
                    if (day11Status == 1) {
                        breadName.setText(R.string.nb14);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud14);
                    selectedBread.setImageResource(R.drawable.bread_14);
                }
                else if (currentBread == 14) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b13);
                    if (day10Status == 1) {
                        breadName.setText(R.string.nb13);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud13);
                    selectedBread.setImageResource(R.drawable.bread_13);
                }
                else if (currentBread == 13) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b12);
                    if (day9Status == 1) {
                        breadName.setText(R.string.nb12);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud12);
                    selectedBread.setImageResource(R.drawable.bread_12);
                }
                else if (currentBread == 12) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b11);
                    if (day8Status == 1) {
                        breadName.setText(R.string.nb11);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud11);
                    selectedBread.setImageResource(R.drawable.bread_11);
                }
                else if (currentBread == 11) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b10);
                    if (day7Status == 1) {
                        breadName.setText(R.string.nb10);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud10);
                    selectedBread.setImageResource(R.drawable.bread_10);
                }
                else if (currentBread == 10) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b9);
                    if (day6Status == 1) {
                        breadName.setText(R.string.nb9);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud9);
                    selectedBread.setImageResource(R.drawable.bread_9);
                }
                else if (currentBread == 9) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b8);
                    if (day5Status == 1) {
                        breadName.setText(R.string.nb8);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud8);
                    selectedBread.setImageResource(R.drawable.bread_8);
                }
                else if (currentBread == 8) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b7);
                    if (day4Status == 1) {
                        breadName.setText(R.string.nb7);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud7);
                    selectedBread.setImageResource(R.drawable.bread_7);
                }
                else if (currentBread == 7) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b6);
                    if (day3Status == 1) {
                        breadName.setText(R.string.nb6);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud6);
                    selectedBread.setImageResource(R.drawable.bread_6);
                }
                else if (currentBread == 6) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b5);
                    if (day2Status == 1) {
                        breadName.setText(R.string.nb5);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud5);
                    selectedBread.setImageResource(R.drawable.bread_5);
                }
                else if (currentBread == 5) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b4);
                    if (day1Status == 1) {
                        breadName.setText(R.string.nb4);
                    }
                    else {
                        breadName.setText(R.string.nbunknown);
                    }
                    availability.setText(R.string.ud4);
                    selectedBread.setImageResource(R.drawable.bread_4);
                }
                else if (currentBread == 4) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b3);
                    breadName.setText(R.string.nb3);
                    availability.setText(R.string.ud3);
                    selectedBread.setImageResource(R.drawable.bread_3);
                }
                else if (currentBread == 3) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b2);
                    breadName.setText(R.string.nb2);
                    availability.setText(R.string.ud2);
                    selectedBread.setImageResource(R.drawable.bread_2);
                }
                else if (currentBread == 2) {
                    currentBread -= 1;
                    breadNum.setText(R.string.b1);
                    breadName.setText(R.string.nb1);
                    availability.setText(R.string.ud1);
                    selectedBread.setImageResource(R.drawable.bread_1);
                    // Hide Previous Button
                    prevBread.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Add Click Listener
        selectedBread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBread == 1) {
                    soundPlayer.playButtonClicked();
                    presentBread1(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 2) {
                    soundPlayer.playButtonClicked();
                    presentBread2(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 3) {
                    soundPlayer.playButtonClicked();
                    presentBread3(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 4) {
                    soundPlayer.playButtonClicked();
                    presentBread4(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 5) {
                    soundPlayer.playButtonClicked();
                    presentBread5(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 6) {
                    soundPlayer.playButtonClicked();
                    presentBread6(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 7) {
                    soundPlayer.playButtonClicked();
                    presentBread7(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 8) {
                    soundPlayer.playButtonClicked();
                    presentBread8(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 9) {
                    soundPlayer.playButtonClicked();
                    presentBread9(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 10) {
                    soundPlayer.playButtonClicked();
                    presentBread10(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 11) {
                    soundPlayer.playButtonClicked();
                    presentBread11(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 12) {
                    soundPlayer.playButtonClicked();
                    presentBread12(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 13) {
                    soundPlayer.playButtonClicked();
                    presentBread13(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 14) {
                    soundPlayer.playButtonClicked();
                    presentBread14(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 15) {
                    soundPlayer.playButtonClicked();
                    presentBread15(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 16) {
                    soundPlayer.playButtonClicked();
                    presentBread16(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 17) {
                    soundPlayer.playButtonClicked();
                    presentBread17(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 18) {
                    soundPlayer.playButtonClicked();
                    presentBread18(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 19) {
                    soundPlayer.playButtonClicked();
                    presentBread19(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 20) {
                    soundPlayer.playButtonClicked();
                    presentBread20(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 21) {
                    soundPlayer.playButtonClicked();
                    presentBread21(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
                else if (currentBread == 22) {
                    soundPlayer.playButtonClicked();
                    presentBread22(v);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                }
            }
        });

    }

    public void presentMain(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(MainActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentCredits(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, CreditsActivity.class);
        intent.putExtra(CreditsActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(CreditsActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread1(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B1_Cupcake.class);
        intent.putExtra(B1_Cupcake.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B1_Cupcake.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread2(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B2_Sprinkle.class);
        intent.putExtra(B2_Sprinkle.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B2_Sprinkle.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread3(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B3_Muffin.class);
        intent.putExtra(B3_Muffin.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B3_Muffin.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread4(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B4_Anadama.class);
        intent.putExtra(B4_Anadama.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B4_Anadama.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread5(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B5_FBaguette.class);
        intent.putExtra(B5_FBaguette.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B5_FBaguette.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread6(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B6_Scotch.class);
        intent.putExtra(B6_Scotch.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B6_Scotch.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread7(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B7_LBaccia.class);
        intent.putExtra(B7_LBaccia.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B7_LBaccia.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread8(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B8_Marraqueta.class);
        intent.putExtra(B8_Marraqueta.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B8_Marraqueta.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread9(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B9_Zopf.class);
        intent.putExtra(B9_Zopf.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B9_Zopf.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread10(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B10_broll.class);
        intent.putExtra(B10_broll.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B10_broll.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread11(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B11_Borodinsky.class);
        intent.putExtra(B11_Borodinsky.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B11_Borodinsky.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread12(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B12_Pretzel.class);
        intent.putExtra(B12_Pretzel.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B12_Pretzel.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread13(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B13_Bsticks.class);
        intent.putExtra(B13_Bsticks.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B13_Bsticks.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread14(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B14_Altamura.class);
        intent.putExtra(B14_Altamura.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B14_Altamura.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread15(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B15_Croissant.class);
        intent.putExtra(B15_Croissant.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B15_Croissant.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread16(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B16_Rbread.class);
        intent.putExtra(B16_Rbread.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B16_Rbread.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread17(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B17_Brioche.class);
        intent.putExtra(B17_Brioche.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B17_Brioche.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread18(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B18_Sufganiyah.class);
        intent.putExtra(B18_Sufganiyah.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B18_Sufganiyah.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread19(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B19_Bammy.class);
        intent.putExtra(B19_Bammy.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B19_Bammy.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread20(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B20_Cookies.class);
        intent.putExtra(B20_Cookies.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B20_Cookies.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread21(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B21_PButter.class);
        intent.putExtra(B21_PButter.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B21_PButter.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentBread22(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, B22_GDonut.class);
        intent.putExtra(B22_GDonut.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(B22_GDonut.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    // Menu Buttons
    Button mainMenu;
    Button musicBtn;
    Button soundBtn;
    Button creditsBtn;

    public void launchMenu() {
        hamburger.setBackgroundResource(R.drawable.close);

        mainMenuDialog.setContentView(R.layout.popup_mainmenu);
        Objects.requireNonNull(mainMenuDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        mainMenuDialog.show();
        mainMenuDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
        mainMenuDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mainMenuDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        mainMenuDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                YoYo.with(Techniques.Pulse)
                        .duration(400)
                        .playOn(hamburger);
                hamburger.setBackgroundResource(R.drawable.menu);
            }
        });

        mainMenu = mainMenuDialog.findViewById(R.id.btnMainMenu);
        musicBtn = mainMenuDialog.findViewById(R.id.btnMusic);
        soundBtn = mainMenuDialog.findViewById(R.id.btnSound);
        creditsBtn = mainMenuDialog.findViewById(R.id.btnCredits);

        // Initiate Current Music State
        String currentMusicState;
        if (musicFlag) {
            currentMusicState = getString(R.string.music_on);
            musicBtn.setText(currentMusicState);
        }
        else if(!musicFlag) {
            currentMusicState = getString(R.string.music_off);
            musicBtn.setText(currentMusicState);
        }

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                unRevealActivity();
            }
        });

        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                String musicText;
                if (musicFlag) {
                    musicText = getString(R.string.music_off);
                    musicBtn.setText(musicText);
                    // Stop Music
                    if (mServ != null) {
                        mServ.stopMusic();
                    }
                    // Change Flag
                    musicFlag = false;
                }
                else if (!musicFlag) {
                    musicText = getString(R.string.music_on);
                    musicBtn.setText(musicText);
                    // Play Music Again
                    doBindService();
                    Intent music = new Intent();
                    music.setClass(AlmanacActivity.this, HomeMusicService.class);
                    startService(music);
                    if (mServ != null) {
                        mServ.startMusic();
                    }
                    // Change Flag
                    musicFlag = true;
                }
                SharedPreferences saveMusic = getSharedPreferences("MusicStatus", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorMusic = saveMusic.edit();
                editorMusic.putBoolean("music", musicFlag);
                editorMusic.apply();
            }
        });

        // TODO: Initiate Current Sound Status

        soundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String soundText;
                if (soundFlag) {
                    soundText = getString(R.string.sound_off);
                    soundBtn.setText(soundText);
                    soundFlag = false;
                    // TODO: Add Turn off Sound on SharedPreferences
                }
                else if (!soundFlag) {
                    soundText = getString(R.string.sound_on);
                    soundBtn.setText(soundText);
                    soundFlag = true;
                    // TODO: Add Turn on Sound on SharedPreferences
                }
            }
        });

        creditsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                presentCredits(v);
            }
        });


    }

    protected void revealActivity(int x, int y) {
        float finalRadius = (float) (Math.max(mainLayout.getWidth(), mainLayout.getHeight()) * 1.1);

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(mainLayout, x, y, 0, finalRadius);
        circularReveal.setDuration(1000);
        circularReveal.setInterpolator(new AccelerateInterpolator());

        mainLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    protected void unRevealActivity() {
        float finalRadius = (float) (Math.max(mainLayout.getWidth(), mainLayout.getHeight()) * 1.1);
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                mainLayout, revealX, revealY, finalRadius, 0);

        circularReveal.setDuration(1000);
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainLayout.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        circularReveal.start();
    }

    @Override
    public void onBackPressed() {
        unRevealActivity();
    }

    // For Music Service
    private boolean mIsBound = false;
    private HomeMusicService mServ;
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((HomeMusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    // For Music Service
    void doBindService(){
        bindService(new Intent(this,HomeMusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    // For Music Service
    void doUnbindService() {
        if(mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Resume Music Service
        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn;
        isScreenOn = false;

        if (pm != null) {
            if (pm.isInteractive()) isScreenOn = true;
            else isScreenOn = false;
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }

    // For Music Service
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unbind Music Service
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,HomeMusicService.class);
        stopService(music);

        mHomeWatcher.stopWatch();

    }

    // For Navigation
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    }
}
