package com.everyday.breadwinner;

import androidx.appcompat.app.AppCompatActivity;

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
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class AlmanacActivity extends AppCompatActivity {
    private View decorView;
    HomeWatcher mHomeWatcher;
    boolean musicFlag, soundFlag = true;
    private SoundPlayer soundPlayer;

    Dialog mainMenuDialog;

    private ImageView selectedBread;
    private TextView breadNum, breadName, availability;
    private Button hamburger, prevBread, nextBread;
    int currentBread = 1;

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

        // Select Current Bread
        breadNum = findViewById(R.id.breadNum);
        breadName = findViewById(R.id.breadName);
        availability = findViewById(R.id.availability);
        selectedBread = findViewById(R.id.selectedBread);

        // Next Button
        nextBread = findViewById(R.id.btnNext);
        nextBread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBread == 1) {
                    soundPlayer.playButtonClicked();
                    currentBread += 1;
                    breadNum.setText(R.string.b2);
                    breadName.setText(R.string.nb2);
                    availability.setText(R.string.ud1);
                    selectedBread.setImageResource(R.drawable.bread_2);
                    // Show Previous Button
                    prevBread.setVisibility(View.VISIBLE);
                }
                else if (currentBread == 2) {
                    soundPlayer.playButtonClicked();
                    currentBread += 1;
                    breadNum.setText(R.string.b3);
                    breadName.setText(R.string.nb3);
                    availability.setText(R.string.ud3);
                    selectedBread.setImageResource(R.drawable.bread_3);
                }
                else if (currentBread == 3) {
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
                    currentBread += 1;
                    breadNum.setText(R.string.b18);
                    breadName.setText(R.string.nb18);
                    availability.setText(R.string.ud18);
                    selectedBread.setImageResource(R.drawable.bread_18);
                }
                else if (currentBread == 18) {
                    soundPlayer.playButtonClicked();
                    currentBread += 1;
                    breadNum.setText(R.string.b19);
                    breadName.setText(R.string.nb19);
                    availability.setText(R.string.ud19);
                    selectedBread.setImageResource(R.drawable.bread_19);
                }
                else if (currentBread == 19) {
                    soundPlayer.playButtonClicked();
                    currentBread += 1;
                    breadNum.setText(R.string.b20);
                    breadName.setText(R.string.nb20);
                    availability.setText(R.string.ud20);
                    selectedBread.setImageResource(R.drawable.bread_20);
                }
                else if (currentBread == 20) {
                    soundPlayer.playButtonClicked();
                    currentBread += 1;
                    breadNum.setText(R.string.b21);
                    breadName.setText(R.string.nb21);
                    availability.setText(R.string.ud21);
                    selectedBread.setImageResource(R.drawable.bread_21);
                }
                else if (currentBread == 21) {
                    soundPlayer.playButtonClicked();
                    currentBread += 1;
                    breadNum.setText(R.string.b22);
                    breadName.setText(R.string.nb22);
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
                if (currentBread == 22) {
                    soundPlayer.playButtonClicked();
                    currentBread -= 1;
                    breadNum.setText(R.string.b21);
                    breadName.setText(R.string.nb21);
                    availability.setText(R.string.ud21);
                    selectedBread.setImageResource(R.drawable.bread_21);
                    // Show Next Button
                    nextBread.setVisibility(View.VISIBLE);
                }
                else if (currentBread == 21) {
                    soundPlayer.playButtonClicked();
                    currentBread -= 1;
                    breadNum.setText(R.string.b20);
                    breadName.setText(R.string.nb20);
                    availability.setText(R.string.ud20);
                    selectedBread.setImageResource(R.drawable.bread_20);
                }
                else if (currentBread == 20) {
                    soundPlayer.playButtonClicked();
                    currentBread -= 1;
                    breadNum.setText(R.string.b19);
                    breadName.setText(R.string.nb19);
                    availability.setText(R.string.ud19);
                    selectedBread.setImageResource(R.drawable.bread_19);
                }
                else if (currentBread == 19) {
                    soundPlayer.playButtonClicked();
                    currentBread -= 1;
                    breadNum.setText(R.string.b18);
                    breadName.setText(R.string.nb18);
                    availability.setText(R.string.ud18);
                    selectedBread.setImageResource(R.drawable.bread_18);
                }
                else if (currentBread == 18) {
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
                    currentBread -= 1;
                    breadNum.setText(R.string.b3);
                    breadName.setText(R.string.nb3);
                    availability.setText(R.string.ud3);
                    selectedBread.setImageResource(R.drawable.bread_3);
                }
                else if (currentBread == 3) {
                    soundPlayer.playButtonClicked();
                    currentBread -= 1;
                    breadNum.setText(R.string.b2);
                    breadName.setText(R.string.nb2);
                    availability.setText(R.string.ud2);
                    selectedBread.setImageResource(R.drawable.bread_2);
                }
                else if (currentBread == 2) {
                    soundPlayer.playButtonClicked();
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
                    Intent detailsBread = new Intent(AlmanacActivity.this, B1_Cupcake.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 2) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B2_Sprinkle.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 3) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B3_Muffin.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 4) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B4_Anadama.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 5) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B5_FBaguette.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 6) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B6_Scotch.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 7) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B7_LBaccia.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 8) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B8_Marraqueta.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 9) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B9_Zopf.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 10) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B10_broll.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 11) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B11_Borodinsky.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 12) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B12_Pretzel.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 13) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B13_Bsticks.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 14) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B14_Altamura.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 15) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B15_Croissant.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 16) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B16_Rbread.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 17) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B17_Brioche.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 18) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B18_Sufganiyah.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 19) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B19_Bammy.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 20) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B20_Cookies.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 21) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B21_PButter.class);
                    startActivity(detailsBread);
                }
                else if (currentBread == 22) {
                    soundPlayer.playButtonClicked();
                    Intent detailsBread = new Intent(AlmanacActivity.this, B22_GDonut.class);
                    startActivity(detailsBread);
                }
            }
        });

    }

    Button musicBtn;
    Button soundBtn;
    Button creditsBtn;

    public void launchMenu() {
        hamburger.setBackgroundResource(R.drawable.close);

        mainMenuDialog.setContentView(R.layout.popup_settings);
        Objects.requireNonNull(mainMenuDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        mainMenuDialog.show();
        mainMenuDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
        mainMenuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mainMenuDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mainMenuDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        mainMenuDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hamburger.setBackgroundResource(R.drawable.menu);
            }
        });

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

        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                soundPlayer.playButtonClicked();
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
                    soundPlayer.playButtonClicked();
                    // TODO: Add Turn on Sound on SharedPreferences
                }
            }
        });

        creditsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Add Function Here
                soundPlayer.playButtonClicked();
            }
        });

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
