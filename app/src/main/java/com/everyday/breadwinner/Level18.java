package com.everyday.breadwinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Level18 extends AppCompatActivity implements View.OnTouchListener {
    private View decorView;
    private View mainLayout;

    // Frame
    private int screenHeight, screenWidth;

    // Dialogs
    Dialog successDialog, failDialog, mainMenuDialog, newBreadDialog;

    // Images
    // TODO: Step 1: Add New Bread ImageView
    private ImageView bread15, bread16, bread17, bread18, bread19, bread20, bread21, bread22;
    private ImageView rbread15, rbread16, rbread17, rbread18, rbread19, rbread20, rbread21, rbread22;
    private ImageView specialSb;
    private ImageView sb1, sb2, sb3;
    private ImageView hand;

    // Button
    Button hamburger;

    // Hand Size
    private int handSize;

    // Positions
    // TODO: Step 2: Add New Bread X and Y
    private float handX, handY;
    private float bread15X, bread15Y;
    private float bread16X, bread16Y;
    private float bread17X, bread17Y;
    private float bread18X, bread18Y;
    private float bread19X, bread19Y;
    private float bread20X, bread20Y;
    private float bread21X, bread21Y;
    private float bread22X, bread22Y;

    private float rbread15X, rbread15Y;
    private float rbread16X, rbread16Y;
    private float rbread17X, rbread17Y;
    private float rbread18X, rbread18Y;
    private float rbread19X, rbread19Y;
    private float rbread20X, rbread20Y;
    private float rbread21X, rbread21Y;
    private float rbread22X, rbread22Y;

    private float specialSbX, specialSbY;

    // Day
    private TextView currentDay;

    // Score
    private TextView scoreLabel;
    private int currentScore, highScore, currentStrawberries, earnedStrawberries, timeCount;
    private SharedPreferences dataLevel;

    // Class
    private Timer timer;
    private Handler handler = new Handler();
    private SoundPlayer soundPlayer;

    // Home Watcher and Flags
    HomeWatcher mHomeWatcher;
    private boolean musicFlag, specialFlag = false, failedFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        // Background Music
        SharedPreferences loadToggleState = this.getSharedPreferences("MusicStatus", Context.MODE_PRIVATE);
        musicFlag = loadToggleState.getBoolean("music", true); //0 is the default value

        // Bind Music
        if (musicFlag) {
            doBindService();
            Intent music = new Intent();
            music.setClass(this, LevelMusicService.class);
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
        soundPlayer = new SoundPlayer(this);

        // FIND IDs
        hamburger = findViewById(R.id.btnHamburger);
        hand = findViewById(R.id.hand);
        scoreLabel = findViewById(R.id.score);
        currentDay = findViewById(R.id.c_day);
        sb1 = findViewById(R.id.straw_1);
        sb2 = findViewById(R.id.straw_2);
        sb3 = findViewById(R.id.straw_3);

        // TODO: Step 3: Find New Bread ID
        bread15 = findViewById(R.id.bread_15);
        bread16 = findViewById(R.id.bread_16);
        bread17 = findViewById(R.id.bread_17);
        bread18 = findViewById(R.id.bread_18);
        bread19 = findViewById(R.id.bread_19);
        bread20 = findViewById(R.id.bread_20);
        bread21 = findViewById(R.id.bread_21);
        bread22 = findViewById(R.id.bread_22);

        rbread15 = findViewById(R.id.rbread_15);
        rbread16 = findViewById(R.id.rbread_16);
        rbread17 = findViewById(R.id.rbread_17);
        rbread18 = findViewById(R.id.rbread_18);
        rbread19 = findViewById(R.id.rbread_19);
        rbread20 = findViewById(R.id.rbread_20);
        rbread21 = findViewById(R.id.rbread_21);
        rbread22 = findViewById(R.id.rbread_22);

        specialSb = findViewById(R.id.specialSb);

        // Dialog Initialization
        mainMenuDialog = new Dialog(this);
        successDialog = new Dialog(this);
        failDialog = new Dialog(this);
        newBreadDialog = new Dialog(this);

        // SET DAY
        // TODO: Step 4: Change Day
        currentDay.setText(R.string.d18);

        // GET HIGH SCORE
        // TODO: Step 5: Change dataLevel
        dataLevel = getSharedPreferences("LEVEL_DATA", Context.MODE_PRIVATE);
        highScore = dataLevel.getInt("LEVEL_18_HIGH_SCORE", 0);

        // START GAME
        startGame();
    }

    public void startGame() {
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size  = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y - 100;

        // Move Bread Out of Screen
        // TODO: Step 6: Move New Bread Out of Screen
        bread15.setX((float)Math.floor(Math.random() * (screenWidth - bread15.getWidth() - 100.0f)));
        bread16.setX((float)Math.floor(Math.random() * (screenWidth - bread16.getWidth() - 100.0f)));
        bread17.setX((float)Math.floor(Math.random() * (screenWidth - bread17.getWidth() - 100.0f)));
        bread18.setX((float)Math.floor(Math.random() * (screenWidth - bread18.getWidth() - 100.0f)));
        bread19.setX((float)Math.floor(Math.random() * (screenWidth - bread19.getWidth() - 100.0f)));
        bread20.setX((float)Math.floor(Math.random() * (screenWidth - bread20.getWidth() - 100.0f)));
        bread21.setX((float)Math.floor(Math.random() * (screenWidth - bread21.getWidth() - 100.0f)));
        bread22.setX((float)Math.floor(Math.random() * (screenWidth - bread22.getWidth() - 100.0f)));

        bread15.setY(-500.0f);
        bread16.setY(-500.0f);
        bread17.setY(-500.0f);
        bread18.setY(-500.0f);
        bread19.setY(-500.0f);
        bread20.setY(-500.0f);
        bread21.setY(-500.0f);
        bread22.setY(-500.0f);

        // Rotten Breads
        rbread15.setX((float)Math.floor(Math.random() * (screenWidth - rbread15.getWidth() - 100.0f)));
        rbread16.setX((float)Math.floor(Math.random() * (screenWidth - rbread16.getWidth() - 100.0f)));
        rbread17.setX((float)Math.floor(Math.random() * (screenWidth - rbread17.getWidth() - 100.0f)));
        rbread18.setX((float)Math.floor(Math.random() * (screenWidth - rbread18.getWidth() - 100.0f)));
        rbread19.setX((float)Math.floor(Math.random() * (screenWidth - rbread19.getWidth() - 100.0f)));
        rbread20.setX((float)Math.floor(Math.random() * (screenWidth - rbread20.getWidth() - 100.0f)));
        rbread21.setX((float)Math.floor(Math.random() * (screenWidth - rbread21.getWidth() - 100.0f)));
        rbread22.setX((float)Math.floor(Math.random() * (screenWidth - rbread22.getWidth() - 100.0f)));

        rbread15.setY(-500.0f);
        rbread16.setY(-500.0f);
        rbread17.setY(-500.0f);
        rbread18.setY(-500.0f);
        rbread19.setY(-500.0f);
        rbread20.setY(-500.0f);
        rbread21.setY(-500.0f);
        rbread22.setY(-500.0f);

        // Special Strawberry
        specialSb.setX((float)Math.floor(Math.random() * (screenWidth - specialSb.getWidth())));
        specialSb.setY(-500.0f);

        // TODO: Step 7: Get New Bread's Y
        bread15X = bread15.getX();
        bread16X = bread16.getX();
        bread17X = bread17.getX();
        bread18X = bread18.getX();
        bread19X = bread19.getX();
        bread20X = bread20.getX();
        bread21X = bread21.getX();
        bread22X = bread22.getX();

        bread15Y = bread15.getY();
        bread16Y = bread16.getY();
        bread17Y = bread17.getY();
        bread18Y = bread18.getY();
        bread19Y = bread19.getY();
        bread20Y = bread20.getY();
        bread21Y = bread21.getY();
        bread22Y = bread22.getY();

        // Rotten Breads
        rbread15X = rbread15.getX();
        rbread16X = rbread16.getX();
        rbread17X = rbread17.getX();
        rbread18X = rbread18.getX();
        rbread19X = rbread19.getX();
        rbread20X = rbread20.getX();
        rbread21X = rbread21.getX();
        rbread22X = rbread22.getX();

        rbread15Y = rbread15.getY();
        rbread16Y = rbread16.getY();
        rbread17Y = rbread17.getY();
        rbread18Y = rbread18.getY();
        rbread19Y = rbread19.getY();
        rbread20Y = rbread20.getY();
        rbread21Y = rbread21.getY();
        rbread22Y = rbread22.getY();

        // Special Strawberry
        specialSbX = specialSb.getX();
        specialSbY = specialSb.getY();

        // Make Bread Visible
        // TODO: Step 8: Make New Bread Visible
        bread15.setVisibility(View.VISIBLE);
        bread16.setVisibility(View.VISIBLE);
        bread17.setVisibility(View.VISIBLE);
        bread18.setVisibility(View.VISIBLE);
        bread19.setVisibility(View.VISIBLE);
        bread20.setVisibility(View.VISIBLE);
        bread21.setVisibility(View.VISIBLE);
        bread22.setVisibility(View.VISIBLE);

        // Rotten Breads
        rbread15.setVisibility(View.VISIBLE);
        rbread16.setVisibility(View.VISIBLE);
        rbread17.setVisibility(View.VISIBLE);
        rbread18.setVisibility(View.VISIBLE);
        rbread19.setVisibility(View.VISIBLE);
        rbread20.setVisibility(View.VISIBLE);
        rbread21.setVisibility(View.VISIBLE);
        rbread22.setVisibility(View.VISIBLE);

        specialSb.setVisibility(View.VISIBLE);
        hand.setVisibility(View.INVISIBLE);

        // Set OnTouchListener
        hand.setOnTouchListener(this);

        // Set Score to 0
        currentScore = 0;
        scoreLabel.setText(String.valueOf(currentScore));

        // LAUNCH NEW BREAD DIALOG
        launchBreadDialog();

    }

    public void launchBreadDialog() {
        hamburger.setBackgroundResource(R.drawable.close);
        newBreadDialog.setContentView(R.layout.popup_newbread);
        Objects.requireNonNull(newBreadDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        // Initialization of Variables + Find IDs in  New Bread Dialog
        ImageView newBread;
        Button accept;

        newBread = newBreadDialog.findViewById(R.id.unlockedBread);
        accept = newBreadDialog.findViewById(R.id.great);

        // TODO: Step 8.5: Change New Bread
        newBread.setImageResource(R.drawable.bread_22);

        // Show Dialog
        newBreadDialog.show();
        // Prevent Dialog from Getting Dismissed
        newBreadDialog.setCancelable(false);
        newBreadDialog.setCanceledOnTouchOutside(false);

        // Hide Shadows
        newBreadDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
        newBreadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        newBreadDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        newBreadDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                newBreadDialog.dismiss();
            }
        });

        newBreadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hamburger.setBackgroundResource(R.drawable.menu);
                hand.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.BounceInUp)
                        .duration(500)
                        .playOn(hand);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dropBread();
                            }
                        });
                    }
                }, 0, 20);
            }
        });
    }

    public void dropBread() {
        timeCount += 20;

        if (timeCount <= 45000) {
            // MOVE HANDS
            handSize = hand.getWidth();
            handX = hand.getX();
            handY = hand.getY();
            hand.setX(0.0f);

            // DROP BREADS

            // BREAD 15
            bread15Y += 9;
            float bread15CenterX = bread15X + ((float) bread15.getWidth()/2);
            float bread15CenterY = bread15Y + ((float) bread15.getHeight()/2);
            if (hitCheck(bread15CenterX, bread15CenterY)) {
                bread15X = (float)Math.floor(Math.random() * (screenWidth - bread15.getWidth() - 100.0f));
                bread15Y = -500.0f;
                currentScore += 800;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread15.getY() > screenHeight) {
                bread15X = (float)Math.floor(Math.random() * (screenWidth - bread15.getWidth() - 100.0f));
                bread15Y = -500.0f;
                currentScore -= 400;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 15
            bread15.setX(bread15X);
            bread15.setY(bread15Y);

            // ROTTEN BREAD 15
//            rbread15Y += 6;
//            float rbread15CenterX = rbread15X + ((float) rbread15.getWidth()/2);
//            float rbread15CenterY = rbread15Y + ((float) rbread15.getHeight()/2);
//            if (hitCheck(rbread15CenterX, rbread15CenterY)) {
//                rbread15Y = -500.0f;
//                currentScore -= 800;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//                soundPlayer.playHitWrongBasket();
//            }
//            if (rbread15.getY() > screenHeight) {
//                rbread15X = (float)Math.floor(Math.random() * (screenWidth - rbread15.getWidth()));
//                rbread15Y = -500.0f;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//            }
//            // Update Location of Bread 15
//            rbread15.setX(rbread15X);
//            rbread15.setY(rbread15Y);

            // BREAD 16
            bread16Y += 10;
            float bread16CenterX = bread16X + ((float) bread16.getWidth()/2);
            float bread16CenterY = bread16Y + ((float) bread16.getHeight()/2);
            if (hitCheck(bread16CenterX, bread16CenterY)) {
                bread16X = (float)Math.floor(Math.random() * (screenWidth - bread16.getWidth() - 100.0f));
                bread16Y = -500.0f;
                currentScore += 850;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread16.getY() > screenHeight) {
                bread16X = (float)Math.floor(Math.random() * (screenWidth - bread16.getWidth() - 100.0f));
                bread16Y = -500.0f;
                currentScore -= 425;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 16
            bread16.setX(bread16X);
            bread16.setY(bread16Y);

            // ROTTEN BREAD 16
//            rbread16Y += 7;
//            float rbread16CenterX = rbread16X + ((float) rbread16.getWidth()/2);
//            float rbread16CenterY = rbread16Y + ((float) rbread16.getHeight()/2);
//            if (hitCheck(rbread16CenterX, rbread16CenterY)) {
//                rbread16Y = -500.0f;
//                currentScore -= 850;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//                soundPlayer.playHitWrongBasket();
//            }
//            if (rbread16.getY() > screenHeight) {
//                rbread16X = (float)Math.floor(Math.random() * (screenWidth - rbread16.getWidth()));
//                rbread16Y = -500.0f;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//            }
//            // Update Location of Bread 16
//            rbread16.setX(rbread16X);
//            rbread16.setY(rbread16Y);

            // BREAD 17
            bread17Y += 11;
            float bread17CenterX = bread17X + ((float) bread17.getWidth()/2);
            float bread17CenterY = bread17Y + ((float) bread17.getHeight()/2);
            if (hitCheck(bread17CenterX, bread17CenterY)) {
                bread17X = (float)Math.floor(Math.random() * (screenWidth - bread17.getWidth() - 100.0f));
                bread17Y = -500.0f;
                currentScore += 900;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread17.getY() > screenHeight) {
                bread17X = (float)Math.floor(Math.random() * (screenWidth - bread17.getWidth() - 100.0f));
                bread17Y = -500.0f;
                currentScore -= 450;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 17
            bread17.setX(bread17X);
            bread17.setY(bread17Y);

            // ROTTEN BREAD 17
//            rbread17Y += 8;
//            float rbread17CenterX = rbread17X + ((float) rbread17.getWidth()/2);
//            float rbread17CenterY = rbread17Y + ((float) rbread17.getHeight()/2);
//            if (hitCheck(rbread17CenterX, rbread17CenterY)) {
//                rbread17Y = -500.0f;
//                currentScore -= 900;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//                soundPlayer.playHitWrongBasket();
//            }
//            if (rbread17.getY() > screenHeight) {
//                rbread17X = (float)Math.floor(Math.random() * (screenWidth - rbread17.getWidth()));
//                rbread17Y = -500.0f;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//            }
//            // Update Location of Bread 17
//            rbread17.setX(rbread17X);
//            rbread17.setY(rbread17Y);

            // BREAD 18
            bread18Y += 12;
            float bread18CenterX = bread18X + ((float) bread18.getWidth()/2);
            float bread18CenterY = bread18Y + ((float) bread18.getHeight()/2);
            if (hitCheck(bread18CenterX, bread18CenterY)) {
                bread18X = (float)Math.floor(Math.random() * (screenWidth - bread18.getWidth() - 100.0f));
                bread18Y = -500.0f;
                currentScore += 950;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread18.getY() > screenHeight) {
                bread18X = (float)Math.floor(Math.random() * (screenWidth - bread18.getWidth() - 100.0f));
                bread18Y = -500.0f;
                currentScore -= 475;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 18
            bread18.setX(bread18X);
            bread18.setY(bread18Y);

            // ROTTEN BREAD 18
//            rbread18Y += 9;
//            float rbread18CenterX = rbread18X + ((float) rbread18.getWidth()/2);
//            float rbread18CenterY = rbread18Y + ((float) rbread18.getHeight()/2);
//            if (hitCheck(rbread18CenterX, rbread18CenterY)) {
//                rbread18Y = -500.0f;
//                currentScore -= 950;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//                soundPlayer.playHitWrongBasket();
//            }
//            if (rbread18.getY() > screenHeight) {
//                rbread18X = (float)Math.floor(Math.random() * (screenWidth - rbread18.getWidth()));
//                rbread18Y = -500.0f;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//            }
//            // Update Location of Bread 18
//            rbread18.setX(rbread18X);
//            rbread18.setY(rbread18Y);

            // BREAD 19
            bread19Y += 13;
            float bread19CenterX = bread19X + ((float) bread19.getWidth()/2);
            float bread19CenterY = bread19Y + ((float) bread19.getHeight()/2);
            if (hitCheck(bread19CenterX, bread19CenterY)) {
                bread19X = (float)Math.floor(Math.random() * (screenWidth - bread19.getWidth() - 100.0f));
                bread19Y = -500.0f;
                currentScore += 1000;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread19.getY() > screenHeight) {
                bread19X = (float)Math.floor(Math.random() * (screenWidth - bread19.getWidth() - 100.0f));
                bread19Y = -500.0f;
                currentScore -= 500;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 19
            bread19.setX(bread19X);
            bread19.setY(bread19Y);

            // ROTTEN BREAD 19
//            rbread19Y += 10;
//            float rbread19CenterX = rbread19X + ((float) rbread19.getWidth()/2);
//            float rbread19CenterY = rbread19Y + ((float) rbread19.getHeight()/2);
//            if (hitCheck(rbread19CenterX, rbread19CenterY)) {
//                rbread19Y = -500.0f;
//                currentScore -= 1000;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//                soundPlayer.playHitWrongBasket();
//            }
//            if (rbread19.getY() > screenHeight) {
//                rbread19X = (float)Math.floor(Math.random() * (screenWidth - rbread19.getWidth()));
//                rbread19Y = -500.0f;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//            }
//            // Update Location of Bread 19
//            rbread19.setX(rbread19X);
//            rbread19.setY(rbread19Y);

            // BREAD 20
            bread20Y += 14;
            float bread20CenterX = bread20X + ((float) bread20.getWidth()/2);
            float bread20CenterY = bread20Y + ((float) bread20.getHeight()/2);
            if (hitCheck(bread20CenterX, bread20CenterY)) {
                bread20X = (float)Math.floor(Math.random() * (screenWidth - bread20.getWidth() - 100.0f));
                bread20Y = -500.0f;
                currentScore += 1050;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread20.getY() > screenHeight) {
                bread20X = (float)Math.floor(Math.random() * (screenWidth - bread20.getWidth() - 100.0f));
                bread20Y = -500.0f;
                currentScore -= 525;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 20
            bread20.setX(bread20X);
            bread20.setY(bread20Y);

            // ROTTEN BREAD 20
            rbread20Y += 11;
            float rbread20CenterX = rbread20X + ((float) rbread20.getWidth()/2);
            float rbread20CenterY = rbread20Y + ((float) rbread20.getHeight()/2);
            if (hitCheck(rbread20CenterX, rbread20CenterY)) {
                rbread20X = (float)Math.floor(Math.random() * (screenWidth - rbread20.getWidth() - 100.0f));
                rbread20Y = -500.0f;
                currentScore -= 1050;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            if (rbread20.getY() > screenHeight) {
                rbread20X = (float)Math.floor(Math.random() * (screenWidth - rbread20.getWidth() - 100.0f));
                rbread20Y = -500.0f;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
            }
            // Update Location of Bread 20
            rbread20.setX(rbread20X);
            rbread20.setY(rbread20Y);

            // BREAD 21
            bread21Y += 15;
            float bread21CenterX = bread21X + ((float) bread21.getWidth()/2);
            float bread21CenterY = bread21Y + ((float) bread21.getHeight()/2);
            if (hitCheck(bread21CenterX, bread21CenterY)) {
                bread21X = (float)Math.floor(Math.random() * (screenWidth - bread21.getWidth() - 100.0f));
                bread21Y = -500.0f;
                currentScore += 1100;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread21.getY() > screenHeight) {
                bread21X = (float)Math.floor(Math.random() * (screenWidth - bread21.getWidth() - 100.0f));
                bread21Y = -500.0f;
                currentScore -= 550;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 21
            bread21.setX(bread21X);
            bread21.setY(bread21Y);

            // ROTTEN BREAD 21
            rbread21Y += 12;
            float rbread21CenterX = rbread21X + ((float) rbread21.getWidth()/2);
            float rbread21CenterY = rbread21Y + ((float) rbread21.getHeight()/2);
            if (hitCheck(rbread21CenterX, rbread21CenterY)) {
                rbread21X = (float)Math.floor(Math.random() * (screenWidth - rbread21.getWidth() - 100.0f));
                rbread21Y = -500.0f;
                currentScore -= 1100;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            if (rbread21.getY() > screenHeight) {
                rbread21X = (float)Math.floor(Math.random() * (screenWidth - rbread21.getWidth() - 100.0f));
                rbread21Y = -500.0f;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
            }
            // Update Location of Bread 21
            rbread21.setX(rbread21X);
            rbread21.setY(rbread21Y);

            // BREAD 22
            bread22Y += 16;
            float bread22CenterX = bread22X + ((float) bread22.getWidth()/2);
            float bread22CenterY = bread22Y + ((float) bread22.getHeight()/2);
            if (hitCheck(bread22CenterX, bread22CenterY)) {
                bread22X = (float)Math.floor(Math.random() * (screenWidth - bread22.getWidth() - 100.0f));
                bread22Y = -500.0f;
                currentScore += 1150;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread22.getY() > screenHeight) {
                bread22X = (float)Math.floor(Math.random() * (screenWidth - bread22.getWidth() - 100.0f));
                bread22Y = -500.0f;
                currentScore -= 575;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 22
            bread22.setX(bread22X);
            bread22.setY(bread22Y);

            // ROTTEN BREAD 22
            rbread22Y += 13;
            float rbread22CenterX = rbread22X + ((float) rbread22.getWidth()/2);
            float rbread22CenterY = rbread22Y + ((float) rbread22.getHeight()/2);
            if (hitCheck(rbread22CenterX, rbread22CenterY)) {
                rbread22X = (float)Math.floor(Math.random() * (screenWidth - rbread22.getWidth() - 100.0f));
                rbread22Y = -500.0f;
                currentScore -= 1150;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            if (rbread22.getY() > screenHeight) {
                rbread22X = (float)Math.floor(Math.random() * (screenWidth - rbread22.getWidth() - 100.0f));
                rbread22Y = -500.0f;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
            }
            // Update Location of Bread 22
            rbread22.setX(rbread22X);
            rbread22.setY(rbread22Y);

            // SPECIAL STRAWBERRY
            if (!specialFlag && timeCount % 3000 == 0) {
                specialFlag = true;
                specialSbY = -500.0f;
            }

            if (specialFlag) {
                specialSbY += 16;
                float specialSbCenterX = specialSbX + ((float) specialSb.getWidth()/2);
                float specialSbCenterY = specialSbY + ((float) specialSb.getHeight()/2);
                if (hitCheck(specialSbCenterX, specialSbCenterY)) {
                    specialSbX = (float)Math.floor(Math.random() * (screenWidth - specialSb.getWidth()));
                    specialSbY = -500.0f;
                    currentScore += 1200;
                    scoreLabel.setText(String.valueOf(currentScore));
                    checkNumOfStrawberries();
                    soundPlayer.playHitCorrectBasket();
                }
                if (specialSb.getY() > screenHeight) {
                    failedFlag = true;
                    endGame();
                }
                // Update Location of Bread 17
                specialSb.setX(specialSbX);
                specialSb.setY(specialSbY);
            }

            // TODO: Step 9: Add New Bread & Rotten Bread

        }
        else {
            endGame();
        }

    }

    public boolean hitCheck(float x, float y) {
        return handX <= x && x <= handX + handSize && handY <= y && y <= screenHeight;
    }

    // TODO: Step 10: Max Score
    private int maxScore = 113400;
    private double firstCut = maxScore * 0.5;
    private double secondCut = maxScore * 0.75;
    private double thirdCut = maxScore * 0.95;

    public void checkNumOfStrawberries () {
        if (currentScore >= firstCut) {
            if (currentScore >= firstCut && currentScore < secondCut) {
                sb1.setImageResource(R.drawable.with_strawberry);
                sb2.setImageResource(R.drawable.without_strawberry);
                sb3.setImageResource(R.drawable.without_strawberry);
            } else if (currentScore >= secondCut && currentScore < thirdCut) {
                sb1.setImageResource(R.drawable.with_strawberry);
                sb2.setImageResource(R.drawable.with_strawberry);
                sb3.setImageResource(R.drawable.without_strawberry);
            } else if (currentScore >= thirdCut) {
                sb1.setImageResource(R.drawable.with_strawberry);
                sb2.setImageResource(R.drawable.with_strawberry);
                sb3.setImageResource(R.drawable.with_strawberry);
            }
        }
        else if (currentScore <= firstCut) {
            sb1.setImageResource(R.drawable.without_strawberry);
            sb2.setImageResource(R.drawable.without_strawberry);
            sb3.setImageResource(R.drawable.without_strawberry);
        }
    }

    float dX;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = hand.getX() - event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                hand.animate()
                        .x(event.getRawX() + dX - ((float) hand.getWidth() / 2))
                        .setDuration(0)
                        .start();
                break;
            default:
                return false;
        }
        return true;
    }

    public void presentFinale1(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, Finale1.class);
        intent.putExtra(Finale1.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(Finale1.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void endGame() {
        timer.cancel();
        timer = null;

        // TODO: Step 11: Hide New Bread
        hand.setVisibility(View.INVISIBLE);
        bread15.setVisibility(View.INVISIBLE);
        bread16.setVisibility(View.INVISIBLE);
        bread17.setVisibility(View.INVISIBLE);
        bread18.setVisibility(View.INVISIBLE);
        bread19.setVisibility(View.INVISIBLE);
        bread20.setVisibility(View.INVISIBLE);
        bread21.setVisibility(View.INVISIBLE);
        bread22.setVisibility(View.INVISIBLE);

        rbread15.setVisibility(View.INVISIBLE);
        rbread16.setVisibility(View.INVISIBLE);
        rbread17.setVisibility(View.INVISIBLE);
        rbread18.setVisibility(View.INVISIBLE);
        rbread19.setVisibility(View.INVISIBLE);
        rbread20.setVisibility(View.INVISIBLE);
        rbread21.setVisibility(View.INVISIBLE);
        rbread22.setVisibility(View.INVISIBLE);

        specialSb.setVisibility(View.INVISIBLE);

        if (currentScore >= firstCut && (!failedFlag)) {
            soundPlayer.playLevelPassed();
            if (currentScore >= firstCut && currentScore < secondCut) {
                hamburger.setBackgroundResource(R.drawable.close);
                successDialog.setContentView(R.layout.popup_daysuccess);
                Objects.requireNonNull(successDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                // Initialization of Variables + Find IDs in  Success Dialog
                TextView currentDay, targetScore, currentScoreLabel;
                ImageView strawberry1, strawberry2, strawberry3;
                Button nextDay, backToMenu;

                currentDay = successDialog.findViewById(R.id.currentDay);
                targetScore = successDialog.findViewById(R.id.targetScore);
                currentScoreLabel = successDialog.findViewById(R.id.currentScore);
                strawberry1 = successDialog.findViewById(R.id.strawberry_1);
                strawberry2 = successDialog.findViewById(R.id.strawberry_2);
                strawberry3 = successDialog.findViewById(R.id.strawberry_3);

                // Get Values from string.xml
                // TODO: Step 12: Change d & td
                String strCurrentDay = getString(R.string.d18);
                String strTargetScore = getString(R.string.td18);

                // Convert Score to String
                String strCurrentScore = Integer.toString(currentScore);

                // Set Values
                currentDay.setText(strCurrentDay);
                targetScore.setText(strTargetScore);
                String currentScoreText = "Current Score: " + strCurrentScore;
                currentScoreLabel.setText(currentScoreText);

                //Show Strawberries
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.without_strawberry);
                strawberry3.setImageResource(R.drawable.without_strawberry);

                // Show Dialog
                successDialog.show();
                // Prevent Dialog from Getting Dismissed
                successDialog.setCancelable(false);
                successDialog.setCanceledOnTouchOutside(false);

                // Hide Shadows
                successDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
                successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                successDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                successDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                // TODO: Step 13: Change Next Day
                nextDay = successDialog.findViewById(R.id.btnNext);
                nextDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPlayer.playButtonClicked();
                        presentFinale1(v);

                        new CountDownTimer(3000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                finish();
                            }
                        }.start();
                    }
                });

                // TODO: Step 14: Change packageContext
                backToMenu = successDialog.findViewById(R.id.btnMenu);
                backToMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPlayer.playButtonClicked();
                        finish();
                        Intent startDay = new Intent(Level18.this, ChooseLevelActivity.class);
                        startActivity(startDay);
                    }
                });
                currentStrawberries = 1;
            }
            else if (currentScore >= secondCut && currentScore < thirdCut) {
                hamburger.setBackgroundResource(R.drawable.close);
                successDialog.setContentView(R.layout.popup_daysuccess);
                Objects.requireNonNull(successDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                // Initialization of Variables + Find IDs in  Success Dialog
                TextView currentDay, targetScore, currentScoreLabel;
                ImageView strawberry1, strawberry2, strawberry3;
                Button nextDay, backToMenu;

                currentDay = successDialog.findViewById(R.id.currentDay);
                targetScore = successDialog.findViewById(R.id.targetScore);
                currentScoreLabel = successDialog.findViewById(R.id.currentScore);
                strawberry1 = successDialog.findViewById(R.id.strawberry_1);
                strawberry2 = successDialog.findViewById(R.id.strawberry_2);
                strawberry3 = successDialog.findViewById(R.id.strawberry_3);

                // Get Values from string.xml
                // TODO: Step 15: Change d & td
                String strCurrentDay = getString(R.string.d18);
                String strTargetScore = getString(R.string.td18);

                // Convert Score to String
                String strCurrentScore = Integer.toString(currentScore);

                // Set Values
                currentDay.setText(strCurrentDay);
                targetScore.setText(strTargetScore);
                String currentScoreText = "Current Score: " + strCurrentScore;
                currentScoreLabel.setText(currentScoreText);

                //Show Strawberries
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.without_strawberry);

                // Show Dialog
                successDialog.show();
                // Prevent Dialog from Getting Dismissed
                successDialog.setCancelable(false);
                successDialog.setCanceledOnTouchOutside(false);

                // Hide Shadows
                successDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
                successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                successDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                successDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                // TODO: Step 16: Change Next Day
                nextDay = successDialog.findViewById(R.id.btnNext);
                nextDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPlayer.playButtonClicked();
                        presentFinale1(v);

                        new CountDownTimer(3000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                finish();
                            }
                        }.start();
                    }
                });

                // TODO: Step 17: Change packageContext
                backToMenu = successDialog.findViewById(R.id.btnMenu);
                backToMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        soundPlayer.playButtonClicked();
                        Intent startDay = new Intent(Level18.this, ChooseLevelActivity.class);
                        startActivity(startDay);
                    }
                });

                currentStrawberries = 2;
            }
            else if (currentScore >= thirdCut) {
                hamburger.setBackgroundResource(R.drawable.close);
                successDialog.setContentView(R.layout.popup_daysuccess);
                Objects.requireNonNull(successDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                // Initialization of Variables + Find IDs in  Success Dialog
                TextView currentDay, targetScore, currentScoreLabel;
                ImageView strawberry1, strawberry2, strawberry3;
                Button nextDay, backToMenu;

                currentDay = successDialog.findViewById(R.id.currentDay);
                targetScore = successDialog.findViewById(R.id.targetScore);
                currentScoreLabel = successDialog.findViewById(R.id.currentScore);
                strawberry1 = successDialog.findViewById(R.id.strawberry_1);
                strawberry2 = successDialog.findViewById(R.id.strawberry_2);
                strawberry3 = successDialog.findViewById(R.id.strawberry_3);

                // Get Values from string.xml
                // TODO: Step 18: Change d & td
                String strCurrentDay = getString(R.string.d18);
                String strTargetScore = getString(R.string.td18);

                // Convert Score to String
                String strCurrentScore = Integer.toString(currentScore);

                // Set Values
                currentDay.setText(strCurrentDay);
                targetScore.setText(strTargetScore);
                String currentScoreText = "Current Score: " + strCurrentScore;
                currentScoreLabel.setText(currentScoreText);

                //Show Strawberries
                strawberry1.setImageResource(R.drawable.with_strawberry);
                strawberry2.setImageResource(R.drawable.with_strawberry);
                strawberry3.setImageResource(R.drawable.with_strawberry);

                // Show Dialog
                successDialog.show();
                // Prevent Dialog from Getting Dismissed
                successDialog.setCancelable(false);
                successDialog.setCanceledOnTouchOutside(false);

                // Hide Shadows
                successDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
                successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                successDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                successDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                // TODO: Step 19: Change Next Day
                nextDay = successDialog.findViewById(R.id.btnNext);
                nextDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPlayer.playButtonClicked();
                        presentFinale1(v);

                        new CountDownTimer(3000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                finish();
                            }
                        }.start();
                    }
                });

                // TODO: Step 20: Change packageContext
                backToMenu = successDialog.findViewById(R.id.btnMenu);
                backToMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        soundPlayer.playButtonClicked();
                        Intent startDay = new Intent(Level18.this, ChooseLevelActivity.class);
                        startActivity(startDay);
                    }
                });
                currentStrawberries = 3;
            }

            // UPDATE EARNED STRAWBERRIES
            // TODO: Step 21 Change Data Level
            if (currentStrawberries > earnedStrawberries) {
                earnedStrawberries = currentStrawberries;
                SharedPreferences.Editor editor = dataLevel.edit();
                editor.putInt("LEVEL_18_STRAWBERRIES", earnedStrawberries);
                editor.apply();
            }

            // MARK DAY AS COMPLETED
            // TODO: Step 22: Change Data Level
            SharedPreferences.Editor editor = dataLevel.edit();
            editor.putInt("LEVEL_18_STATUS", 1);
            editor.apply();
        }
        else if (currentScore < firstCut || (failedFlag)){
            soundPlayer.playLevelFailed();

            hamburger.setBackgroundResource(R.drawable.close);
            failDialog.setContentView(R.layout.popup_dayfailed);
            Objects.requireNonNull(failDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            // Initialization of Variables + Find IDs in  Success Dialog
            TextView currentFailedDay, targetScoreOnly, currentScoreOnly;
            ImageView strawberry1, strawberry2, strawberry3;
            Button retryDay, backToMenu;

            currentFailedDay = failDialog.findViewById(R.id.currentFailedDay);
            targetScoreOnly = failDialog.findViewById(R.id.targetScoreOnly);
            currentScoreOnly = failDialog.findViewById(R.id.currentScoreOnly);
            strawberry1 = failDialog.findViewById(R.id.strawberry_1);
            strawberry2 = failDialog.findViewById(R.id.strawberry_2);
            strawberry3 = failDialog.findViewById(R.id.strawberry_3);

            // Get Values from string.xml
            // TODO: Step 23: Change d & td
            String strCurrentDay = getString(R.string.d18);
            String strTargetScore = getString(R.string.td18);

            // Convert Score to String
            String strCurrentScore = Integer.toString(currentScore);

            // Set Values
            currentFailedDay.setText(strCurrentDay);
            targetScoreOnly.setText(strTargetScore);
            String currentScoreText = "Current Score: " + strCurrentScore;
            currentScoreOnly.setText(currentScoreText);

            if (failedFlag) {
                if (currentScore >= firstCut && currentScore < secondCut) {
                    strawberry1.setImageResource(R.drawable.with_strawberry);
                }
                else if (currentScore >= secondCut && currentScore < thirdCut) {
                    strawberry1.setImageResource(R.drawable.with_strawberry);
                    strawberry2.setImageResource(R.drawable.with_strawberry);
                }
                else if (currentScore >= thirdCut) {
                    strawberry1.setImageResource(R.drawable.with_strawberry);
                    strawberry2.setImageResource(R.drawable.with_strawberry);
                    strawberry3.setImageResource(R.drawable.with_strawberry);
                }
            }

            // Show Dialog
            failDialog.show();
            // Prevent Dialog from Getting Dismissed
            failDialog.setCancelable(false);
            failDialog.setCanceledOnTouchOutside(false);

            // Hide Shadows
            failDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            failDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            failDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            failDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            retryDay = failDialog.findViewById(R.id.btnRetry);
            retryDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    soundPlayer.playButtonClicked();
                    Intent restartDay = getIntent();
                    finish();
                    startActivity(restartDay);
                }
            });

            // TODO: Step 24: Change packageContext
            backToMenu = failDialog.findViewById(R.id.btnRetryMenu);
            backToMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    soundPlayer.playButtonClicked();
                    Intent backMenu = new Intent(Level18.this, ChooseLevelActivity.class);
                    startActivity(backMenu);
                }
            });
        }

        // UPDATE HIGH SCORE
        // TODO: Step 25: Change Data Level
        if (currentScore > highScore) {
            highScore = currentScore;
            SharedPreferences.Editor editor = dataLevel.edit();
            editor.putInt("LEVEL_18_HIGH_SCORE", highScore);
            editor.apply();
        }
    }

    // For Music Service
    private boolean mIsBound = false;
    private LevelMusicService mServ;
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((LevelMusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    // For Music Service
    void doBindService(){
        bindService(new Intent(this,LevelMusicService.class),
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
        music.setClass(this,LevelMusicService.class);
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
