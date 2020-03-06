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

public class Level9 extends AppCompatActivity implements View.OnTouchListener {
    private View decorView;

    // Frame
    private int screenHeight, screenWidth;

    // Dialogs
    Dialog successDialog, failDialog, mainMenuDialog, newBreadDialog;

    // Images
    // TODO: Step 1: Add New Bread ImageView
    private ImageView bread8, bread9, bread10, bread11, bread12;
    private ImageView rbread8, rbread9, rbread10, rbread11, rbread12;
    private ImageView sb1, sb2, sb3;
    private ImageView hand;

    // Button
    Button hamburger;

    // Hand Size
    private int handSize;

    // Positions
    // TODO: Step 2: Add New Bread X and Y
    private float handX, handY;
    private float bread8X, bread8Y;
    private float bread9X, bread9Y;
    private float bread10X, bread10Y;
    private float bread11X, bread11Y;
    private float bread12X, bread12Y;

    private float rbread8X, rbread8Y;
    private float rbread9X, rbread9Y;
    private float rbread10X, rbread10Y;
    private float rbread11X, rbread11Y;
    private float rbread12X, rbread12Y;

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
    boolean musicFlag;

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
        currentDay = findViewById(R.id.c_day);
        sb1 = findViewById(R.id.straw_1);
        sb2 = findViewById(R.id.straw_2);
        sb3 = findViewById(R.id.straw_3);

        // TODO: Step 3: Find New Bread ID
        bread8 = findViewById(R.id.bread_8);
        bread9 = findViewById(R.id.bread_9);
        bread10 = findViewById(R.id.bread_10);
        bread11 = findViewById(R.id.bread_11);
        bread12 = findViewById(R.id.bread_12);

        rbread8 = findViewById(R.id.rbread_8);
        rbread9 = findViewById(R.id.rbread_9);
        rbread10 = findViewById(R.id.rbread_10);
        rbread11 = findViewById(R.id.rbread_11);
        rbread12 = findViewById(R.id.rbread_12);

        // Dialog Initialization
        mainMenuDialog = new Dialog(this);
        successDialog = new Dialog(this);
        failDialog = new Dialog(this);
        newBreadDialog = new Dialog(this);

        // SET DAY
        // TODO: Step 4: Change Day
        currentDay.setText(R.string.d9);

        // GET HIGH SCORE
        // TODO: Step 5: Change dataLevel
        dataLevel = getSharedPreferences("LEVEL_DATA", Context.MODE_PRIVATE);
        highScore = dataLevel.getInt("LEVEL_9_HIGH_SCORE", 0);
        earnedStrawberries = dataLevel.getInt("LEVEL_9_STRAWBERRIES", 0);

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

    public void startGame() {
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size  = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y - 100;

        // Move Bread Out of Screen
        // TODO: Step 6: Move New Bread Out of Screen
        bread8.setX((float)Math.floor(Math.random() * (screenWidth - bread8.getWidth() - 100.0f)));
        bread9.setX((float)Math.floor(Math.random() * (screenWidth - bread9.getWidth() - 100.0f)));
        bread10.setX((float)Math.floor(Math.random() * (screenWidth - bread10.getWidth() - 100.0f)));
        bread11.setX((float)Math.floor(Math.random() * (screenWidth - bread11.getWidth() - 100.0f)));
        bread12.setX((float)Math.floor(Math.random() * (screenWidth - bread12.getWidth() - 100.0f)));

        bread8.setY(-500.0f);
        bread9.setY(-500.0f);
        bread10.setY(-500.0f);
        bread11.setY(-500.0f);
        bread12.setY(-500.0f);

        // Rotten Breads
        rbread8.setX((float)Math.floor(Math.random() * (screenWidth - rbread8.getWidth() - 100.0f)));
        rbread9.setX((float)Math.floor(Math.random() * (screenWidth - rbread9.getWidth() - 100.0f)));
        rbread10.setX((float)Math.floor(Math.random() * (screenWidth - rbread10.getWidth() - 100.0f)));
        rbread11.setX((float)Math.floor(Math.random() * (screenWidth - rbread11.getWidth() - 100.0f)));
        rbread12.setX((float)Math.floor(Math.random() * (screenWidth - rbread12.getWidth() - 100.0f)));

        rbread8.setY(-500.0f);
        rbread9.setY(-500.0f);
        rbread10.setY(-500.0f);
        rbread11.setY(-500.0f);
        rbread12.setY(-500.0f);

        // TODO: Step 7: Get New Bread's Y
        bread8X = bread8.getX();
        bread9X = bread9.getX();
        bread10X = bread10.getX();
        bread11X = bread11.getX();
        bread12X = bread12.getX();

        bread8Y = bread8.getY();
        bread9Y = bread9.getY();
        bread10Y = bread10.getY();
        bread11Y = bread11.getY();
        bread12Y = bread12.getY();

        // Rotten Breads
        rbread8X = rbread8.getX();
        rbread9X = rbread9.getX();
        rbread10X = rbread10.getX();
        rbread11X = rbread11.getX();
        rbread12X = rbread12.getX();

        rbread8Y = rbread8.getY();
        rbread9Y = rbread9.getY();
        rbread10Y = rbread10.getY();
        rbread11Y = rbread11.getY();
        rbread12Y = rbread12.getY();


        // Make Bread Visible
        // TODO: Step 8: Make New Bread Visible
        bread8.setVisibility(View.VISIBLE);
        bread9.setVisibility(View.VISIBLE);
        bread10.setVisibility(View.VISIBLE);
        bread11.setVisibility(View.VISIBLE);
        bread12.setVisibility(View.VISIBLE);

        // Rotten Breads
        rbread8.setVisibility(View.VISIBLE);
        rbread9.setVisibility(View.VISIBLE);
        rbread10.setVisibility(View.VISIBLE);
        rbread11.setVisibility(View.VISIBLE);
        rbread12.setVisibility(View.VISIBLE);

        hand.setVisibility(View.INVISIBLE);

        // Set OnTouchListener
        hand.setOnTouchListener(this);

        // Set Score to 0
        currentScore = 0;
        scoreLabel.setText(String.valueOf(currentScore));

        // LAUNCH NEW BREAD DIALOG
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                launchBreadDialog();
            }
        }.start();

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
        newBread.setImageResource(R.drawable.bread_12);

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

            // BREAD 8
            bread8Y += 9;
            float bread8CenterX = bread8X + ((float) bread8.getWidth()/2);
            float bread8CenterY = bread8Y + ((float) bread8.getHeight()/2);
            if (hitCheck(bread8CenterX, bread8CenterY)) {
                bread8X = (float)Math.floor(Math.random() * (screenWidth - bread8.getWidth() - 100.0f));
                bread8Y = -500.0f;
                currentScore += 450;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread8.getY() > screenHeight) {
                bread8X = (float)Math.floor(Math.random() * (screenWidth - bread8.getWidth() - 100.0f));
                bread8Y = -500.0f;
                currentScore -= 225;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 8
            bread8.setX(bread8X);
            bread8.setY(bread8Y);

            // ROTTEN BREAD 8
//            rbread8Y += 6;
//            float rbread8CenterX = rbread8X + ((float) rbread8.getWidth()/2);
//            float rbread8CenterY = rbread8Y + ((float) rbread8.getHeight()/2);
//            if (hitCheck(rbread8CenterX, rbread8CenterY)) {
//                rbread8Y = -500.0f;
//                currentScore -= 450;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//                soundPlayer.playHitWrongBasket();
//            }
//            if (rbread8.getY() > screenHeight) {
//                rbread8X = (float)Math.floor(Math.random() * (screenWidth - rbread8.getWidth()));
//                rbread8Y = -500.0f;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//            }
//            // Update Location of Bread 8
//            rbread8.setX(rbread8X);
//            rbread8.setY(rbread8Y);

            // BREAD 9
            bread9Y += 10;
            float bread9CenterX = bread9X + ((float) bread9.getWidth()/2);
            float bread9CenterY = bread9Y + ((float) bread9.getHeight()/2);
            if (hitCheck(bread9CenterX, bread9CenterY)) {
                bread9X = (float)Math.floor(Math.random() * (screenWidth - bread9.getWidth() - 100.0f));
                bread9Y = -500.0f;
                currentScore += 500;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread9.getY() > screenHeight) {
                bread9X = (float)Math.floor(Math.random() * (screenWidth - bread9.getWidth() - 100.0f));
                bread9Y = -500.0f;
                currentScore -= 250;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 9
            bread9.setX(bread9X);
            bread9.setY(bread9Y);

            // ROTTEN BREAD 9
//            rbread9Y += 7;
//            float rbread9CenterX = rbread9X + ((float) rbread9.getWidth()/2);
//            float rbread9CenterY = rbread9Y + ((float) rbread9.getHeight()/2);
//            if (hitCheck(rbread9CenterX, rbread9CenterY)) {
//                rbread9Y = -500.0f;
//                currentScore -= 500;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//                soundPlayer.playHitWrongBasket();
//            }
//            if (rbread9.getY() > screenHeight) {
//                rbread9X = (float)Math.floor(Math.random() * (screenWidth - rbread9.getWidth()));
//                rbread9Y = -500.0f;
//                scoreLabel.setText(String.valueOf(currentScore));
//                checkNumOfStrawberries();
//            }
//            // Update Location of Bread 9
//            rbread9.setX(rbread9X);
//            rbread9.setY(rbread9Y);

            // BREAD 10
            bread10Y += 11;
            float bread10CenterX = bread10X + ((float) bread10.getWidth()/2);
            float bread10CenterY = bread10Y + ((float) bread10.getHeight()/2);
            if (hitCheck(bread10CenterX, bread10CenterY)) {
                bread10X = (float)Math.floor(Math.random() * (screenWidth - bread10.getWidth() - 100.0f));
                bread10Y = -500.0f;
                currentScore += 550;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread10.getY() > screenHeight) {
                bread10X = (float)Math.floor(Math.random() * (screenWidth - bread10.getWidth() - 100.0f));
                bread10Y = -500.0f;
                currentScore -= 275;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 10
            bread10.setX(bread10X);
            bread10.setY(bread10Y);

            // ROTTEN BREAD 10
            rbread10Y += 8;
            float rbread10CenterX = rbread10X + ((float) rbread10.getWidth()/2);
            float rbread10CenterY = rbread10Y + ((float) rbread10.getHeight()/2);
            if (hitCheck(rbread10CenterX, rbread10CenterY)) {
                rbread10X = (float)Math.floor(Math.random() * (screenWidth - rbread10.getWidth() - 100.0f));
                rbread10Y = -500.0f;
                currentScore -= 550;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            if (rbread10.getY() > screenHeight) {
                rbread10X = (float)Math.floor(Math.random() * (screenWidth - rbread10.getWidth() - 100.0f));
                rbread10Y = -500.0f;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
            }
            // Update Location of Bread 10
            rbread10.setX(rbread10X);
            rbread10.setY(rbread10Y);

            // BREAD 11
            bread11Y += 12;
            float bread11CenterX = bread11X + ((float) bread11.getWidth()/2);
            float bread11CenterY = bread11Y + ((float) bread11.getHeight()/2);
            if (hitCheck(bread11CenterX, bread11CenterY)) {
                bread11X = (float)Math.floor(Math.random() * (screenWidth - bread11.getWidth() - 100.0f));
                bread11Y = -500.0f;
                currentScore += 600;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread11.getY() > screenHeight) {
                bread11X = (float)Math.floor(Math.random() * (screenWidth - bread11.getWidth() - 100.0f));
                bread11Y = -500.0f;
                currentScore -= 300;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 11
            bread11.setX(bread11X);
            bread11.setY(bread11Y);

            // ROTTEN BREAD 11
            rbread11Y += 9;
            float rbread11CenterX = rbread11X + ((float) rbread11.getWidth()/2);
            float rbread11CenterY = rbread11Y + ((float) rbread11.getHeight()/2);
            if (hitCheck(rbread11CenterX, rbread11CenterY)) {
                rbread11X = (float)Math.floor(Math.random() * (screenWidth - rbread11.getWidth() - 100.0f));
                rbread11Y = -500.0f;
                currentScore -= 600;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            if (rbread11.getY() > screenHeight) {
                rbread11X = (float)Math.floor(Math.random() * (screenWidth - rbread11.getWidth() - 100.0f));
                rbread11Y = -500.0f;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
            }
            // Update Location of Bread 11
            rbread11.setX(rbread11X);
            rbread11.setY(rbread11Y);

            // BREAD 12
            bread12Y += 13;
            float bread12CenterX = bread12X + ((float) bread12.getWidth()/2);
            float bread12CenterY = bread12Y + ((float) bread12.getHeight()/2);
            if (hitCheck(bread12CenterX, bread12CenterY)) {
                bread12X = (float)Math.floor(Math.random() * (screenWidth - bread12.getWidth() - 100.0f));
                bread12Y = -500.0f;
                currentScore += 650;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread12.getY() > screenHeight) {
                bread12X = (float)Math.floor(Math.random() * (screenWidth - bread12.getWidth() - 100.0f));
                bread12Y = -500.0f;
                currentScore -= 325;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 12
            bread12.setX(bread12X);
            bread12.setY(bread12Y);

            // ROTTEN BREAD 12
            rbread12Y += 10;
            float rbread12CenterX = rbread12X + ((float) rbread12.getWidth()/2);
            float rbread12CenterY = rbread12Y + ((float) rbread12.getHeight()/2);
            if (hitCheck(rbread12CenterX, rbread12CenterY)) {
                rbread12X = (float)Math.floor(Math.random() * (screenWidth - rbread12.getWidth() - 100.0f));
                rbread12Y = -500.0f;
                currentScore -= 650;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            if (rbread12.getY() > screenHeight) {
                rbread12X = (float)Math.floor(Math.random() * (screenWidth - rbread12.getWidth() - 100.0f));
                rbread12Y = -500.0f;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
            }
            // Update Location of Bread 12
            rbread12.setX(rbread12X);
            rbread12.setY(rbread12Y);


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
    int maxScore = 30750;
    double firstCut = maxScore * 0.5;
    double secondCut = maxScore * 0.75;
    double thirdCut = maxScore * 0.95;

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

    public void endGame() {
        timer.cancel();
        timer = null;

        // TODO: Step 11: Hide New Bread
        hand.setVisibility(View.INVISIBLE);
        bread8.setVisibility(View.INVISIBLE);
        bread9.setVisibility(View.INVISIBLE);
        bread10.setVisibility(View.INVISIBLE);
        bread11.setVisibility(View.INVISIBLE);
        bread12.setVisibility(View.INVISIBLE);

        rbread8.setVisibility(View.INVISIBLE);
        rbread9.setVisibility(View.INVISIBLE);
        rbread10.setVisibility(View.INVISIBLE);
        rbread11.setVisibility(View.INVISIBLE);
        rbread12.setVisibility(View.INVISIBLE);

        if (currentScore >= firstCut) {
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
            // TODO: Step 22: Change Data Level
            SharedPreferences.Editor editor = dataLevel.edit();
            editor.putInt("LEVEL_9_STATUS", 1);
            editor.apply();
        }
        else if (currentScore < firstCut){
            soundPlayer.playLevelFailed();
            launchFailedDialog();
        }

        // UPDATE EARNED STRAWBERRIES
        if (currentStrawberries > earnedStrawberries) {
            earnedStrawberries = currentStrawberries;
            SharedPreferences.Editor editor = dataLevel.edit();
            editor.putInt("LEVEL_9_STRAWBERRIES", earnedStrawberries);
            editor.apply();
        }

        // UPDATE HIGH SCORE
        if (currentScore > highScore) {
            highScore = currentScore;
            SharedPreferences.Editor editor = dataLevel.edit();
            editor.putInt("LEVEL_9_HIGH_SCORE", highScore);
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
        // TODO: Step 12: Change d & td
        String strCurrentDay = getString(R.string.d9);
        String strTargetScore = getString(R.string.td9);

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

        // TODO: Step 13: Change Next Day
        nextDay = successDialog.findViewById(R.id.btnNext);
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                presentLevel10(v);
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

        // TODO: Step 14: Change packageContext
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
        Button retryDay, backToMenu;

        currentFailedDay = failDialog.findViewById(R.id.currentFailedDay);
        targetScoreOnly = failDialog.findViewById(R.id.targetScoreOnly);
        currentScoreOnly = failDialog.findViewById(R.id.currentScoreOnly);

        // Get Values from string.xml
        // TODO: Step 23: Change d & td
        String strCurrentDay = getString(R.string.d9);
        String strTargetScore = getString(R.string.td9);

        // Convert Score to String
        String strCurrentScore = Integer.toString(currentScore);

        // Set Values
        currentFailedDay.setText(strCurrentDay);
        targetScoreOnly.setText(strTargetScore);
        String currentScoreText = "Current Score: " + strCurrentScore;
        currentScoreOnly.setText(currentScoreText);

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

    public void presentLevel10(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, Level10.class);
        intent.putExtra(Level10.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(Level10.EXTRA_CIRCULAR_REVEAL_Y, revealY);

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
