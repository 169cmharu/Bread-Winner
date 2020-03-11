package com.everyday.breadwinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import android.animation.Animator;
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
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Level13 extends AppCompatActivity implements View.OnTouchListener {
    private View decorView;

    // Frame
    private int screenHeight, screenWidth;

    // Dialogs
    Dialog importantDialog, successDialog, failDialog, mainMenuDialog, newBreadDialog;

    // Images
    private ImageView bread15, bread16, bread17;
    private ImageView rbread15, rbread16, rbread17;
    private ImageView specialSb;
    private ImageView sb1, sb2, sb3;
    private ImageView hand;

    // Button
    Button hamburger;

    // Hand Size
    private int handSize;

    // Positions
    private float handX, handY;
    private float bread15X, bread15Y;
    private float bread16X, bread16Y;
    private float bread17X, bread17Y;

    private float rbread15X, rbread15Y;
    private float rbread16X, rbread16Y;
    private float rbread17X, rbread17Y;

    private float specialSbX, specialSbY;

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

    // Transition
    public View mainLayout;
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private int revealX;
    private int revealY;

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
        // Day
        TextView currentDay = findViewById(R.id.c_day);
        sb1 = findViewById(R.id.straw_1);
        sb2 = findViewById(R.id.straw_2);
        sb3 = findViewById(R.id.straw_3);

        bread15 = findViewById(R.id.bread_15);
        bread16 = findViewById(R.id.bread_16);
        bread17 = findViewById(R.id.bread_17);

        rbread15 = findViewById(R.id.rbread_15);
        rbread16 = findViewById(R.id.rbread_16);
        rbread17 = findViewById(R.id.rbread_17);

        specialSb = findViewById(R.id.specialSb);

        // Dialog Initialization
        importantDialog = new Dialog(this);
        mainMenuDialog = new Dialog(this);
        successDialog = new Dialog(this);
        failDialog = new Dialog(this);
        newBreadDialog = new Dialog(this);

        // SET DAY
        currentDay.setText(R.string.d13);

        // GET HIGH SCORE
        dataLevel = getSharedPreferences("LEVEL_DATA", Context.MODE_PRIVATE);
        highScore = dataLevel.getInt("LEVEL_13_HIGH_SCORE", 0);
        earnedStrawberries = dataLevel.getInt("LEVEL_13_STRAWBERRIES", 0);

        // START GAME
        startGame();
    }

    protected void revealActivity(int x, int y) {
        float finalRadius = (float) (Math.max(mainLayout.getWidth(), mainLayout.getHeight()) * 1.1);

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(mainLayout, x, y, 0, finalRadius);
        circularReveal.setDuration(1000);
        circularReveal.setInterpolator(new AccelerateInterpolator());

        mainLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void startGame() {
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size  = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y - 100;

        // Move Bread Out of Screen
        bread15.setX((float)Math.floor(Math.random() * (screenWidth - bread15.getWidth() - 100.0f)));
        bread16.setX((float)Math.floor(Math.random() * (screenWidth - bread16.getWidth() - 100.0f)));
        bread17.setX((float)Math.floor(Math.random() * (screenWidth - bread17.getWidth() - 100.0f)));

        bread15.setY(-500.0f);
        bread16.setY(-500.0f);
        bread17.setY(-500.0f);

        // Rotten Breads
        rbread15.setX((float)Math.floor(Math.random() * (screenWidth - rbread15.getWidth() - 100.0f)));
        rbread16.setX((float)Math.floor(Math.random() * (screenWidth - rbread16.getWidth() - 100.0f)));
        rbread17.setX((float)Math.floor(Math.random() * (screenWidth - rbread17.getWidth() - 100.0f)));

        rbread15.setY(-500.0f);
        rbread16.setY(-500.0f);
        rbread17.setY(-500.0f);

        // Special Strawberry
        specialSb.setX((float)Math.floor(Math.random() * (screenWidth - specialSb.getWidth())));
        specialSb.setY(-500.0f);

        bread15X = bread15.getX();
        bread16X = bread16.getX();
        bread17X = bread17.getX();

        bread15Y = bread15.getY();
        bread16Y = bread16.getY();
        bread17Y = bread17.getY();

        // Rotten Breads
        rbread15X = rbread15.getX();
        rbread16X = rbread16.getX();
        rbread17X = rbread17.getX();

        rbread15Y = rbread15.getY();
        rbread16Y = rbread16.getY();
        rbread17Y = rbread17.getY();

        // Special Strawberry
        specialSbX = specialSb.getX();
        specialSbY = specialSb.getY();

        // Make Bread Visible
        bread15.setVisibility(View.VISIBLE);
        bread16.setVisibility(View.VISIBLE);
        bread17.setVisibility(View.VISIBLE);

        // Rotten Breads
        rbread15.setVisibility(View.VISIBLE);
        rbread16.setVisibility(View.VISIBLE);
        rbread17.setVisibility(View.VISIBLE);

        specialSb.setVisibility(View.VISIBLE);
        hand.setVisibility(View.INVISIBLE);

        // Set OnTouchListener
        hand.setOnTouchListener(this);

        // Set Score to 0
        currentScore = 0;
        scoreLabel.setText(String.valueOf(currentScore));

        // START TIMER

        // LAUNCH IMPORTANT DIALOG
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                launchImportantDialog();
            }
        }.start();
    }

    public void launchImportantDialog () {
        hamburger.setBackgroundResource(R.drawable.close);
        importantDialog.setContentView(R.layout.popup_specialstrawberry);
        Objects.requireNonNull(importantDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        // Initialization of Variables + Find IDs in  New Bread Dialog
        Button accept;
        accept = importantDialog.findViewById(R.id.great);

        // Show Dialog
        importantDialog.show();
        // Prevent Dialog from Getting Dismissed
        importantDialog.setCancelable(false);
        importantDialog.setCanceledOnTouchOutside(false);

        // Hide Shadows
        importantDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
        importantDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        importantDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        importantDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                importantDialog.dismiss();
            }
        });

        importantDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                launchBreadDialog();
            }
        });
    }

    public void launchBreadDialog() {
        hamburger.setBackgroundResource(R.drawable.close);
        newBreadDialog.setContentView(R.layout.popup_twobreads);
        Objects.requireNonNull(newBreadDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        // Initialization of Variables + Find IDs in  New Bread Dialog
        ImageView newBread, newBread1;
        Button accept;

        newBread = newBreadDialog.findViewById(R.id.unlockedBread1);
        newBread1 = newBreadDialog.findViewById(R.id.unlockedBread2);
        accept = newBreadDialog.findViewById(R.id.great);

        newBread.setImageResource(R.drawable.bread_16);
        newBread1.setImageResource(R.drawable.bread_17);

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
            rbread15Y += 6;
            float rbread15CenterX = rbread15X + ((float) rbread15.getWidth()/2);
            float rbread15CenterY = rbread15Y + ((float) rbread15.getHeight()/2);
            if (hitCheck(rbread15CenterX, rbread15CenterY)) {
                rbread15X = (float)Math.floor(Math.random() * (screenWidth - rbread15.getWidth() - 100.0f));
                rbread15Y = -500.0f;
                currentScore -= 800;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            if (rbread15.getY() > screenHeight) {
                rbread15X = (float)Math.floor(Math.random() * (screenWidth - rbread15.getWidth() - 100.0f));
                rbread15Y = -500.0f;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
            }
            // Update Location of Bread 15
            rbread15.setX(rbread15X);
            rbread15.setY(rbread15Y);

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
            rbread16Y += 7;
            float rbread16CenterX = rbread16X + ((float) rbread16.getWidth()/2);
            float rbread16CenterY = rbread16Y + ((float) rbread16.getHeight()/2);
            if (hitCheck(rbread16CenterX, rbread16CenterY)) {
                rbread16X = (float)Math.floor(Math.random() * (screenWidth - rbread16.getWidth() - 100.0f));
                rbread16Y = -500.0f;
                currentScore -= 850;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            if (rbread16.getY() > screenHeight) {
                rbread16X = (float)Math.floor(Math.random() * (screenWidth - rbread16.getWidth() - 100.0f));
                rbread16Y = -500.0f;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
            }
            // Update Location of Bread 16
            rbread16.setX(rbread16X);
            rbread16.setY(rbread16Y);

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
            rbread17Y += 8;
            float rbread17CenterX = rbread17X + ((float) rbread17.getWidth()/2);
            float rbread17CenterY = rbread17Y + ((float) rbread17.getHeight()/2);
            if (hitCheck(rbread17CenterX, rbread17CenterY)) {
                rbread17X = (float)Math.floor(Math.random() * (screenWidth - rbread17.getWidth() - 100.0f));
                rbread17Y = -500.0f;
                currentScore -= 900;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            if (rbread17.getY() > screenHeight) {
                rbread17X = (float)Math.floor(Math.random() * (screenWidth - rbread17.getWidth() - 100.0f));
                rbread17Y = -500.0f;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
            }
            // Update Location of Bread 17
            rbread17.setX(rbread17X);
            rbread17.setY(rbread17Y);

            // SPECIAL STRAWBERRY
            if (!specialFlag && timeCount % 5000 == 0) {
                specialFlag = true;
                specialSbY = -500.0f;
            }

            if (specialFlag) {
                specialSbY += 11;
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

        }
        else {
            endGame();
        }

    }

    public boolean hitCheck(float x, float y) {
        return handX <= x && x <= handX + handSize && handY <= y && y <= screenHeight;
    }

    private int maxScore = 36400;
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
                hand.setX(event.getRawX() + dX);
                break;
            default:
                return false;
        }
        return true;
    }

    public void endGame() {
        timer.cancel();
        timer = null;

        hand.setVisibility(View.INVISIBLE);
        bread15.setVisibility(View.INVISIBLE);
        bread16.setVisibility(View.INVISIBLE);
        bread17.setVisibility(View.INVISIBLE);

        rbread15.setVisibility(View.INVISIBLE);
        rbread16.setVisibility(View.INVISIBLE);
        rbread17.setVisibility(View.INVISIBLE);

        specialSb.setVisibility(View.INVISIBLE);

        if (currentScore >= firstCut && (!failedFlag)) {
            soundPlayer.playLevelPassed();
            if (currentScore >= firstCut && currentScore < secondCut) {
                launchSuccessDialog(1);
                currentStrawberries = 1;
            }
            else if (currentScore >= secondCut && currentScore < thirdCut) {
                launchSuccessDialog(2);
                currentStrawberries = 2;
            }
            else if (currentScore >= thirdCut) {
                launchSuccessDialog(3);
                currentStrawberries = 3;
            }

            // MARK DAY AS COMPLETED
            SharedPreferences.Editor editor = dataLevel.edit();
            editor.putInt("LEVEL_13_STATUS", 1);
            editor.apply();
        }
        else if (currentScore < firstCut || (failedFlag)){
            soundPlayer.playLevelFailed();
            launchFailedDialog();
        }

        // UPDATE EARNED STRAWBERRIES
        if (currentStrawberries > earnedStrawberries) {
            earnedStrawberries = currentStrawberries;
            SharedPreferences.Editor editor = dataLevel.edit();
            editor.putInt("LEVEL_13_STRAWBERRIES", earnedStrawberries);
            editor.apply();
        }

        // UPDATE HIGH SCORE
        if (currentScore > highScore) {
            highScore = currentScore;
            SharedPreferences.Editor editor = dataLevel.edit();
            editor.putInt("LEVEL_13_HIGH_SCORE", highScore);
            editor.apply();
        }
    }

    public void launchSuccessDialog(int strawberries) {
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
        String strCurrentDay = getString(R.string.d13);
        String strTargetScore = getString(R.string.td13);

        // Convert Score to String
        String strCurrentScore = Integer.toString(currentScore);

        // Set Values
        currentDay.setText(strCurrentDay);
        targetScore.setText(strTargetScore);
        String currentScoreText = "Current Score: " + strCurrentScore;
        currentScoreLabel.setText(currentScoreText);

        //Show Strawberries
        if (strawberries == 1) {
            strawberry1.setImageResource(R.drawable.with_strawberry);
            strawberry2.setImageResource(R.drawable.without_strawberry);
            strawberry3.setImageResource(R.drawable.without_strawberry);
        }
        else if (strawberries == 2) {
            strawberry1.setImageResource(R.drawable.with_strawberry);
            strawberry2.setImageResource(R.drawable.with_strawberry);
            strawberry3.setImageResource(R.drawable.without_strawberry);
        }
        else if (strawberries == 3) {
            strawberry1.setImageResource(R.drawable.with_strawberry);
            strawberry2.setImageResource(R.drawable.with_strawberry);
            strawberry3.setImageResource(R.drawable.with_strawberry);
        }

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

        nextDay = successDialog.findViewById(R.id.btnNext);
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                presentLevel14(v);
                new CountDownTimer(1000, 1000) {
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

        backToMenu = successDialog.findViewById(R.id.btnMenu);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                presentChooseLevel(v);
                new CountDownTimer(1000, 1000) {
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
    }

    public void launchFailedDialog() {
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
        String strCurrentDay = getString(R.string.d13);
        String strTargetScore = getString(R.string.td13);

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

        backToMenu = failDialog.findViewById(R.id.btnRetryMenu);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                presentChooseLevel(v);
                new CountDownTimer(1000, 1000) {
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
    }

    public void presentLevel14(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, Level14.class);
        intent.putExtra(Level14.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(Level14.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void presentChooseLevel(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, ChooseLevelActivity.class);
        intent.putExtra(ChooseLevelActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(ChooseLevelActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
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
