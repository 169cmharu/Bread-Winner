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
import android.text.Layout;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

public class ChooseLevelActivity extends AppCompatActivity {
    private View decorView;
    HomeWatcher mHomeWatcher;
    Dialog confirmDayMenu, mainMenuDialog;
    private LinearLayout week1, week2, week3;
    Button hamburger, prevPage, nextPage;
    int currentWeek = 1;
    private TextView weekNum;
    boolean musicFlag, soundFlag = true;
    private SoundPlayer soundPlayer;

    // Shared Preferences
    private SharedPreferences dataLevel;
    int day1Status, day1HighScore, day1EarnedStrawberries;
    int day2Status, day2HighScore, day2EarnedStrawberries;
    int day3Status, day3HighScore, day3EarnedStrawberries;
    int day4Status, day4HighScore, day4EarnedStrawberries;
    int day5Status, day5HighScore, day5EarnedStrawberries;
    int day6Status, day6HighScore, day6EarnedStrawberries;
    int day7Status, day7HighScore, day7EarnedStrawberries;
    int day8Status, day8HighScore, day8EarnedStrawberries;
    int day9Status, day9HighScore, day9EarnedStrawberries;
    int day10Status, day10HighScore, day10EarnedStrawberries;
    int day11Status, day11HighScore, day11EarnedStrawberries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);

        // decorView
        decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
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

        // START OF MAIN CODE
        confirmDayMenu = new Dialog(this);
        mainMenuDialog = new Dialog(this);

        // Button Declarations
        Button day1Btn, day2Btn, day3Btn, day4Btn, day5Btn, day6Btn;
        Button day7Btn, day8Btn, day9Btn, day10Btn, day11Btn, day12Btn;
        Button day13Btn, day14Btn, day15Btn, day16Btn, day17Btn, day18Btn;

        // Find Buttons by their ID
        hamburger = findViewById(R.id.btnHamburger);
        prevPage = findViewById(R.id.btnPrev);
        nextPage = findViewById(R.id.btnNext);
        weekNum = findViewById(R.id.weekNum);
        week1 = findViewById(R.id.week1Layout);
        week2 = findViewById(R.id.week2Layout);
        week3 = findViewById(R.id.week3Layout);

        day1Btn = findViewById(R.id.btnDay1);
        day2Btn = findViewById(R.id.btnDay2);
        day3Btn = findViewById(R.id.btnDay3);
        day4Btn = findViewById(R.id.btnDay4);
        day5Btn = findViewById(R.id.btnDay5);
        day6Btn = findViewById(R.id.btnDay6);
        day7Btn = findViewById(R.id.btnDay7);
        day8Btn = findViewById(R.id.btnDay8);
        day9Btn = findViewById(R.id.btnDay9);
        day10Btn = findViewById(R.id.btnDay10);
        day11Btn = findViewById(R.id.btnDay11);
        day12Btn = findViewById(R.id.btnDay12);
        day13Btn = findViewById(R.id.btnDay13);
        day14Btn = findViewById(R.id.btnDay14);
        day15Btn = findViewById(R.id.btnDay15);
        day16Btn = findViewById(R.id.btnDay16);
        day17Btn = findViewById(R.id.btnDay17);
        day18Btn = findViewById(R.id.btnDay18);


        // Add OnClick Listener
        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMenu();
                soundPlayer.playButtonClicked();
            }
        });
        prevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                if (currentWeek == 3) {
                    nextPage.setVisibility(View.VISIBLE);
                    weekNum.setText(R.string.w2);
                    week1.setVisibility(View.INVISIBLE);
                    week2.setVisibility(View.VISIBLE);
                    week3.setVisibility(View.INVISIBLE);
                    currentWeek = 2;
                }
                else if (currentWeek == 2) {
                    prevPage.setVisibility(View.INVISIBLE);
                    nextPage.setVisibility(View.VISIBLE);
                    weekNum.setText(R.string.w1);
                    week1.setVisibility(View.VISIBLE);
                    week2.setVisibility(View.INVISIBLE);
                    week3.setVisibility(View.INVISIBLE);
                    currentWeek = 1;
                }
            }
        });
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                if (currentWeek == 1) {
                    prevPage.setVisibility(View.VISIBLE);
                    weekNum.setText(R.string.w2);
                    week1.setVisibility(View.INVISIBLE);
                    week2.setVisibility(View.VISIBLE);
                    week3.setVisibility(View.INVISIBLE);
                    currentWeek = 2;
                }
                else if (currentWeek == 2) {
                    nextPage.setVisibility(View.INVISIBLE);
                    weekNum.setText(R.string.w3);
                    week1.setVisibility(View.INVISIBLE);
                    week2.setVisibility(View.INVISIBLE);
                    week3.setVisibility(View.VISIBLE);
                    currentWeek = 3;
                }

            }
        });
        day1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(1);
                soundPlayer.playButtonClicked();
            }
        });
        day2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(2);
                soundPlayer.playButtonClicked();
            }
        });
        day3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(3);
                soundPlayer.playButtonClicked();
            }
        });
        day4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(4);
                soundPlayer.playButtonClicked();
            }
        });
        day5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(5);
                soundPlayer.playButtonClicked();
            }
        });
        day6Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(6);
                soundPlayer.playButtonClicked();
            }
        });
        day7Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(7);
                soundPlayer.playButtonClicked();
            }
        });
        day8Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(8);
                soundPlayer.playButtonClicked();
            }
        });
        day9Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(9);
                soundPlayer.playButtonClicked();
            }
        });
        day10Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(10);
                soundPlayer.playButtonClicked();
            }
        });
        day11Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(11);
                soundPlayer.playButtonClicked();
            }
        });
        day12Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(12);
                soundPlayer.playButtonClicked();
            }
        });
        day13Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(13);
                soundPlayer.playButtonClicked();
            }
        });
        day14Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(14);
                soundPlayer.playButtonClicked();
            }
        });
        day15Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(15);
                soundPlayer.playButtonClicked();
            }
        });
        day16Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(16);
                soundPlayer.playButtonClicked();
            }
        });
        day17Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(17);
                soundPlayer.playButtonClicked();
            }
        });
        day18Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDay(18);
                soundPlayer.playButtonClicked();
            }
        });

        // LOAD DATA
        // TODO: Add Next Level Dara
        dataLevel = getSharedPreferences("LEVEL_DATA", Context.MODE_PRIVATE);

        day1Status = dataLevel.getInt("LEVEL_1_STATUS", 0);
        day1HighScore = dataLevel.getInt("LEVEL_1_HIGH_SCORE", 0);
        day1EarnedStrawberries = dataLevel.getInt("LEVEL_1_STRAWBERRIES", 0);

        day2Status = dataLevel.getInt("LEVEL_2_STATUS", 0);
        day2HighScore = dataLevel.getInt("LEVEL_2_HIGH_SCORE", 0);
        day2EarnedStrawberries = dataLevel.getInt("LEVEL_2_STRAWBERRIES", 0);

        day3Status = dataLevel.getInt("LEVEL_3_STATUS", 0);
        day3HighScore = dataLevel.getInt("LEVEL_3_HIGH_SCORE", 0);
        day3EarnedStrawberries = dataLevel.getInt("LEVEL_3_STRAWBERRIES", 0);

        day4Status = dataLevel.getInt("LEVEL_4_STATUS", 0);
        day4HighScore = dataLevel.getInt("LEVEL_4_HIGH_SCORE", 0);
        day4EarnedStrawberries = dataLevel.getInt("LEVEL_4_STRAWBERRIES", 0);

        day5Status = dataLevel.getInt("LEVEL_5_STATUS", 0);
        day5HighScore = dataLevel.getInt("LEVEL_5_HIGH_SCORE", 0);
        day5EarnedStrawberries = dataLevel.getInt("LEVEL_5_STRAWBERRIES", 0);

        day6Status = dataLevel.getInt("LEVEL_6_STATUS", 0);
        day6HighScore = dataLevel.getInt("LEVEL_6_HIGH_SCORE", 0);
        day6EarnedStrawberries = dataLevel.getInt("LEVEL_6_STRAWBERRIES", 0);

        day7Status = dataLevel.getInt("LEVEL_7_STATUS", 0);
        day7HighScore = dataLevel.getInt("LEVEL_7_HIGH_SCORE", 0);
        day7EarnedStrawberries = dataLevel.getInt("LEVEL_7_STRAWBERRIES", 0);

        day8Status = dataLevel.getInt("LEVEL_8_STATUS", 0);
        day8HighScore = dataLevel.getInt("LEVEL_8_HIGH_SCORE", 0);
        day8EarnedStrawberries = dataLevel.getInt("LEVEL_8_STRAWBERRIES", 0);

        day9Status = dataLevel.getInt("LEVEL_9_STATUS", 0);
        day9HighScore = dataLevel.getInt("LEVEL_9_HIGH_SCORE", 0);
        day9EarnedStrawberries = dataLevel.getInt("LEVEL_9_STRAWBERRIES", 0);

        day10Status = dataLevel.getInt("LEVEL_10_STATUS", 0);
        day10HighScore = dataLevel.getInt("LEVEL_10_HIGH_SCORE", 0);
        day10EarnedStrawberries = dataLevel.getInt("LEVEL_10_STRAWBERRIES", 0);

        day11Status = dataLevel.getInt("LEVEL_11_STATUS", 0);
        day11HighScore = dataLevel.getInt("LEVEL_11_HIGH_SCORE", 0);
        day11EarnedStrawberries = dataLevel.getInt("LEVEL_11_STRAWBERRIES", 0);
    }

    public void selectDay(int day) {
        if (day == 1) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            // Declarations
            TextView currentDay, targetScore, highScore;
            ImageView strawberry1, strawberry2, strawberry3;
            Button startGame;

            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);
            highScore = confirmDayMenu.findViewById(R.id.currentScore);
            strawberry1 = confirmDayMenu.findViewById(R.id.strawberry_1);
            strawberry2 = confirmDayMenu.findViewById(R.id.strawberry_2);
            strawberry3 = confirmDayMenu.findViewById(R.id.strawberry_3);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d1);
            String strTargetScore = getString(R.string.td1);
            String strHighScore = "High Score: " + day1HighScore;

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);
            highScore.setText(strHighScore);

            // Show Strawberries Earned
            if (day1EarnedStrawberries == 1) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
            }
            else if (day1EarnedStrawberries == 2) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
            }
            else if (day1EarnedStrawberries == 3) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.with_strawberry);
            }

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);
            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startDay = new Intent(ChooseLevelActivity.this, Level1.class);
                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 2) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore, highScore, lockMessage;
            ImageView strawberry1, strawberry2, strawberry3;
            Button startGame;

            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);
            highScore = confirmDayMenu.findViewById(R.id.currentScore);
            strawberry1 = confirmDayMenu.findViewById(R.id.strawberry_1);
            strawberry2 = confirmDayMenu.findViewById(R.id.strawberry_2);
            strawberry3 = confirmDayMenu.findViewById(R.id.strawberry_3);
            lockMessage = confirmDayMenu.findViewById(R.id.lockMessage);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d2);
            String strTargetScore = getString(R.string.td2);
            String strHighScore = "High Score: " + day2HighScore;

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);
            highScore.setText(strHighScore);

            // Show Strawberries Earned
            if (day2EarnedStrawberries == 1) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
            }
            else if (day2EarnedStrawberries == 2) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
            }
            else if (day2EarnedStrawberries == 3) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.with_strawberry);
            }

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);

            if (day1Status != 1) {
                startGame.setVisibility(View.INVISIBLE);
                lockMessage.setVisibility(View.VISIBLE);
            }
            else {
                startGame.setVisibility(View.VISIBLE);
            }

            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startDay = new Intent(ChooseLevelActivity.this, Level2.class);
                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 3) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore, highScore, lockMessage;
            ImageView strawberry1, strawberry2, strawberry3;
            Button startGame;

            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);
            highScore = confirmDayMenu.findViewById(R.id.currentScore);
            strawberry1 = confirmDayMenu.findViewById(R.id.strawberry_1);
            strawberry2 = confirmDayMenu.findViewById(R.id.strawberry_2);
            strawberry3 = confirmDayMenu.findViewById(R.id.strawberry_3);
            lockMessage = confirmDayMenu.findViewById(R.id.lockMessage);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d3);
            String strTargetScore = getString(R.string.td3);
            String strHighScore = "High Score: " + day3HighScore;

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);
            highScore.setText(strHighScore);

            // Show Strawberries Earned
            if (day3EarnedStrawberries == 1) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
            }
            else if (day3EarnedStrawberries == 2) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
            }
            else if (day3EarnedStrawberries == 3) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.with_strawberry);
            }

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);

            if (day2Status != 1) {
                startGame.setVisibility(View.INVISIBLE);
                lockMessage.setVisibility(View.VISIBLE);
            }
            else {
                startGame.setVisibility(View.VISIBLE);
            }

            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startDay = new Intent(ChooseLevelActivity.this, Level3.class);
                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 4) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore, highScore, lockMessage;
            ImageView strawberry1, strawberry2, strawberry3;
            Button startGame;

            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);
            highScore = confirmDayMenu.findViewById(R.id.currentScore);
            strawberry1 = confirmDayMenu.findViewById(R.id.strawberry_1);
            strawberry2 = confirmDayMenu.findViewById(R.id.strawberry_2);
            strawberry3 = confirmDayMenu.findViewById(R.id.strawberry_3);
            lockMessage = confirmDayMenu.findViewById(R.id.lockMessage);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d4);
            String strTargetScore = getString(R.string.td4);
            String strHighScore = "High Score: " + day4HighScore;

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);
            highScore.setText(strHighScore);

            // Show Strawberries Earned
            if (day4EarnedStrawberries == 1) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
            }
            else if (day4EarnedStrawberries == 2) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
            }
            else if (day4EarnedStrawberries == 3) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.with_strawberry);
            }

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);

            if (day3Status != 1) {
                startGame.setVisibility(View.INVISIBLE);
                lockMessage.setVisibility(View.VISIBLE);
            }
            else {
                startGame.setVisibility(View.VISIBLE);
            }

            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startDay = new Intent(ChooseLevelActivity.this, Level4.class);
                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 5) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore, highScore, lockMessage;
            ImageView strawberry1, strawberry2, strawberry3;
            Button startGame;

            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);
            highScore = confirmDayMenu.findViewById(R.id.currentScore);
            strawberry1 = confirmDayMenu.findViewById(R.id.strawberry_1);
            strawberry2 = confirmDayMenu.findViewById(R.id.strawberry_2);
            strawberry3 = confirmDayMenu.findViewById(R.id.strawberry_3);
            lockMessage = confirmDayMenu.findViewById(R.id.lockMessage);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d5);
            String strTargetScore = getString(R.string.td5);
            String strHighScore = "High Score: " + day5HighScore;

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);
            highScore.setText(strHighScore);

            // Show Strawberries Earned
            if (day5EarnedStrawberries == 1) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
            }
            else if (day5EarnedStrawberries == 2) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
            }
            else if (day5EarnedStrawberries == 3) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.with_strawberry);
            }

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);

            if (day4Status != 1) {
                startGame.setVisibility(View.INVISIBLE);
                lockMessage.setVisibility(View.VISIBLE);
            }
            else {
                startGame.setVisibility(View.VISIBLE);
            }

            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startDay = new Intent(ChooseLevelActivity.this, Level5.class);
                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 6) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore, highScore, lockMessage;
            ImageView strawberry1, strawberry2, strawberry3;
            Button startGame;

            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);
            highScore = confirmDayMenu.findViewById(R.id.currentScore);
            strawberry1 = confirmDayMenu.findViewById(R.id.strawberry_1);
            strawberry2 = confirmDayMenu.findViewById(R.id.strawberry_2);
            strawberry3 = confirmDayMenu.findViewById(R.id.strawberry_3);
            lockMessage = confirmDayMenu.findViewById(R.id.lockMessage);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d6);
            String strTargetScore = getString(R.string.td6);
            String strHighScore = "High Score: " + day6HighScore;

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);
            highScore.setText(strHighScore);

            // Show Strawberries Earned
            if (day6EarnedStrawberries == 1) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
            }
            else if (day6EarnedStrawberries == 2) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
            }
            else if (day6EarnedStrawberries == 3) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.with_strawberry);
            }

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);

            if (day5Status != 1) {
                startGame.setVisibility(View.INVISIBLE);
                lockMessage.setVisibility(View.VISIBLE);
            }
            else {
                startGame.setVisibility(View.VISIBLE);
            }

            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startDay = new Intent(ChooseLevelActivity.this, Level6.class);
                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 7) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore, highScore, lockMessage;
            ImageView strawberry1, strawberry2, strawberry3;
            Button startGame;

            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);
            highScore = confirmDayMenu.findViewById(R.id.currentScore);
            strawberry1 = confirmDayMenu.findViewById(R.id.strawberry_1);
            strawberry2 = confirmDayMenu.findViewById(R.id.strawberry_2);
            strawberry3 = confirmDayMenu.findViewById(R.id.strawberry_3);
            lockMessage = confirmDayMenu.findViewById(R.id.lockMessage);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d7);
            String strTargetScore = getString(R.string.td7);
            String strHighScore = "High Score: " + day7HighScore;

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);
            highScore.setText(strHighScore);

            // Show Strawberries Earned
            if (day7EarnedStrawberries == 1) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
            }
            else if (day7EarnedStrawberries == 2) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
            }
            else if (day7EarnedStrawberries == 3) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.with_strawberry);
            }

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);

            if (day6Status != 1) {
                startGame.setVisibility(View.INVISIBLE);
                lockMessage.setVisibility(View.VISIBLE);
            }
            else {
                startGame.setVisibility(View.VISIBLE);
            }

            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startDay = new Intent(ChooseLevelActivity.this, Level7.class);
                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 8) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore, highScore, lockMessage;
            ImageView strawberry1, strawberry2, strawberry3;
            Button startGame;

            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);
            highScore = confirmDayMenu.findViewById(R.id.currentScore);
            strawberry1 = confirmDayMenu.findViewById(R.id.strawberry_1);
            strawberry2 = confirmDayMenu.findViewById(R.id.strawberry_2);
            strawberry3 = confirmDayMenu.findViewById(R.id.strawberry_3);
            lockMessage = confirmDayMenu.findViewById(R.id.lockMessage);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d8);
            String strTargetScore = getString(R.string.td8);
            String strHighScore = "High Score: " + day8HighScore;

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);
            highScore.setText(strHighScore);

            // Show Strawberries Earned
            if (day8EarnedStrawberries == 1) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
            }
            else if (day8EarnedStrawberries == 2) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
            }
            else if (day8EarnedStrawberries == 3) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.with_strawberry);
            }

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);

            if (day7Status != 1) {
                startGame.setVisibility(View.INVISIBLE);
                lockMessage.setVisibility(View.VISIBLE);
            }
            else {
                startGame.setVisibility(View.VISIBLE);
            }

            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startDay = new Intent(ChooseLevelActivity.this, Level8.class);
                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 9) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore, highScore, lockMessage;
            ImageView strawberry1, strawberry2, strawberry3;
            Button startGame;

            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);
            highScore = confirmDayMenu.findViewById(R.id.currentScore);
            strawberry1 = confirmDayMenu.findViewById(R.id.strawberry_1);
            strawberry2 = confirmDayMenu.findViewById(R.id.strawberry_2);
            strawberry3 = confirmDayMenu.findViewById(R.id.strawberry_3);
            lockMessage = confirmDayMenu.findViewById(R.id.lockMessage);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d9);
            String strTargetScore = getString(R.string.td9);
            String strHighScore = "High Score: " + day9HighScore;

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);
            highScore.setText(strHighScore);

            // Show Strawberries Earned
            if (day9EarnedStrawberries == 1) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
            }
            else if (day9EarnedStrawberries == 2) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
            }
            else if (day9EarnedStrawberries == 3) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.with_strawberry);
            }

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);

            if (day8Status != 1) {
                startGame.setVisibility(View.INVISIBLE);
                lockMessage.setVisibility(View.VISIBLE);
            }
            else {
                startGame.setVisibility(View.VISIBLE);
            }

            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startDay = new Intent(ChooseLevelActivity.this, Level9.class);
                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 10) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore, highScore, lockMessage;
            ImageView strawberry1, strawberry2, strawberry3;
            Button startGame;

            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);
            highScore = confirmDayMenu.findViewById(R.id.currentScore);
            strawberry1 = confirmDayMenu.findViewById(R.id.strawberry_1);
            strawberry2 = confirmDayMenu.findViewById(R.id.strawberry_2);
            strawberry3 = confirmDayMenu.findViewById(R.id.strawberry_3);
            lockMessage = confirmDayMenu.findViewById(R.id.lockMessage);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d10);
            String strTargetScore = getString(R.string.td10);
            String strHighScore = "High Score: " + day10HighScore;

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);
            highScore.setText(strHighScore);

            // Show Strawberries Earned
            if (day10EarnedStrawberries == 1) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
            }
            else if (day10EarnedStrawberries == 2) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
            }
            else if (day10EarnedStrawberries == 3) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.with_strawberry);
            }

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);

            if (day9Status != 1) {
                startGame.setVisibility(View.INVISIBLE);
                lockMessage.setVisibility(View.VISIBLE);
            }
            else {
                startGame.setVisibility(View.VISIBLE);
            }

            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startDay = new Intent(ChooseLevelActivity.this, Level10.class);
                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 11) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore, highScore, lockMessage;
            ImageView strawberry1, strawberry2, strawberry3;
            Button startGame;

            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);
            highScore = confirmDayMenu.findViewById(R.id.currentScore);
            strawberry1 = confirmDayMenu.findViewById(R.id.strawberry_1);
            strawberry2 = confirmDayMenu.findViewById(R.id.strawberry_2);
            strawberry3 = confirmDayMenu.findViewById(R.id.strawberry_3);
            lockMessage = confirmDayMenu.findViewById(R.id.lockMessage);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d11);
            String strTargetScore = getString(R.string.td11);
            String strHighScore = "High Score: " + day11HighScore;

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);
            highScore.setText(strHighScore);

            // Show Strawberries Earned
            if (day11EarnedStrawberries == 1) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
            }
            else if (day11EarnedStrawberries == 2) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
            }
            else if (day11EarnedStrawberries == 3) {
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.with_strawberry);
            }

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);

            if (day10Status != 1) {
                startGame.setVisibility(View.INVISIBLE);
                lockMessage.setVisibility(View.VISIBLE);
            }
            else {
                startGame.setVisibility(View.VISIBLE);
            }

            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startDay = new Intent(ChooseLevelActivity.this, Level11.class);
                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 12) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore;
            Button startGame;
            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d12);
            String strTargetScore = getString(R.string.td12);

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);
            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent startDay = new Intent(ChooseLevelActivity.this, Level1.class);
//                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 13) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore;
            Button startGame;
            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d13);
            String strTargetScore = getString(R.string.td13);

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);
            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent startDay = new Intent(ChooseLevelActivity.this, Level1.class);
//                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 14) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore;
            Button startGame;
            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d14);
            String strTargetScore = getString(R.string.td14);

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);
            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent startDay = new Intent(ChooseLevelActivity.this, Level1.class);
//                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 15) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore;
            Button startGame;
            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d15);
            String strTargetScore = getString(R.string.td15);

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);
            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent startDay = new Intent(ChooseLevelActivity.this, Level1.class);
//                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 16) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore;
            Button startGame;
            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d16);
            String strTargetScore = getString(R.string.td16);

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);
            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent startDay = new Intent(ChooseLevelActivity.this, Level1.class);
//                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 17) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore;
            Button startGame;
            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d17);
            String strTargetScore = getString(R.string.td17);

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);
            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent startDay = new Intent(ChooseLevelActivity.this, Level1.class);
//                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
        else  if (day == 18) {
            hamburger.setBackgroundResource(R.drawable.close);
            confirmDayMenu.setContentView(R.layout.popup_confirmday);
            Objects.requireNonNull(confirmDayMenu.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Edit Values in Confirm Menu According to Day
            TextView currentDay, targetScore;
            Button startGame;
            currentDay = confirmDayMenu.findViewById(R.id.currentDay);
            targetScore = confirmDayMenu.findViewById(R.id.targetScore);

            // Get Values from string.xml
            String strCurrentDay = getString(R.string.d18);
            String strTargetScore = getString(R.string.td18);

            // Set Values
            currentDay.setText(strCurrentDay);
            targetScore.setText(strTargetScore);

            // Show Dialog
            confirmDayMenu.show();
            confirmDayMenu.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            confirmDayMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            confirmDayMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Reset Hamburger Menu
            confirmDayMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hamburger.setBackgroundResource(R.drawable.menu);
                }
            });

            startGame = confirmDayMenu.findViewById(R.id.btnStart);
            startGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent startDay = new Intent(ChooseLevelActivity.this, Level1.class);
//                    startActivity(startDay);
                    if (mServ != null) {
                        mServ.pauseMusic();
                    }
                    soundPlayer.playButtonClicked();
                }
            });
        }
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
                Intent levelIntent = new Intent(ChooseLevelActivity.this, MainActivity.class);
                startActivity(levelIntent);
            }
        });

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
                    music.setClass(ChooseLevelActivity.this, HomeMusicService.class);
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
                //TODO: Add Function Here
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
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

}
