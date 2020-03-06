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

public class Level6 extends AppCompatActivity implements View.OnTouchListener {
    private View decorView;

    // Frame
    private int screenHeight, screenWidth;

    // Dialogs
    Dialog successDialog, failDialog, mainMenuDialog, newBreadDialog;

    // Images
    // TODO: Step 1: Add New Bread ImageView
    private ImageView bread1, bread2, bread3, bread4, bread5, bread6, bread7, bread8;
    private ImageView sb1, sb2, sb3;
    private ImageView hand;

    // Button
    Button hamburger;

    // Hand Size
    private int handSize;

    // Positions
    // TODO: Step 2: Add New Bread X and Y
    private float handX, handY;
    private float bread1X, bread1Y;
    private float bread2X, bread2Y;
    private float bread3X, bread3Y;
    private float bread4X, bread4Y;
    private float bread5X, bread5Y;
    private float bread6X, bread6Y;
    private float bread7X, bread7Y;
    private float bread8X, bread8Y;

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
        bread1 = findViewById(R.id.bread_1);
        bread2 = findViewById(R.id.bread_2);
        bread3 = findViewById(R.id.bread_3);
        bread4 = findViewById(R.id.bread_4);
        bread5 = findViewById(R.id.bread_5);
        bread6 = findViewById(R.id.bread_6);
        bread7 = findViewById(R.id.bread_7);
        bread8 = findViewById(R.id.bread_8);

        // Dialog Initialization
        mainMenuDialog = new Dialog(this);
        successDialog = new Dialog(this);
        failDialog = new Dialog(this);
        newBreadDialog = new Dialog(this);

        // SET DAY
        // TODO: Step 4: Change Day
        currentDay.setText(R.string.d6);

        // GET HIGH SCORE
        // TODO: Step 5: Change dataLevel
        dataLevel = getSharedPreferences("LEVEL_DATA", Context.MODE_PRIVATE);
        highScore = dataLevel.getInt("LEVEL_6_HIGH_SCORE", 0);
        earnedStrawberries = dataLevel.getInt("LEVEL_6_STRAWBERRIES", 0);

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
        bread1.setX((float)Math.floor(Math.random() * (screenWidth - bread1.getWidth() - 100.0f)));
        bread2.setX((float)Math.floor(Math.random() * (screenWidth - bread2.getWidth() - 100.0f)));
        bread3.setX((float)Math.floor(Math.random() * (screenWidth - bread3.getWidth() - 100.0f)));
        bread4.setX((float)Math.floor(Math.random() * (screenWidth - bread4.getWidth() - 100.0f)));
        bread5.setX((float)Math.floor(Math.random() * (screenWidth - bread5.getWidth() - 100.0f)));
        bread6.setX((float)Math.floor(Math.random() * (screenWidth - bread6.getWidth() - 100.0f)));
        bread7.setX((float)Math.floor(Math.random() * (screenWidth - bread7.getWidth() - 100.0f)));
        bread8.setX((float)Math.floor(Math.random() * (screenWidth - bread8.getWidth() - 100.0f)));

        bread1.setY(-500.0f);
        bread2.setY(-500.0f);
        bread3.setY(-500.0f);
        bread4.setY(-500.0f);
        bread5.setY(-500.0f);
        bread6.setY(-500.0f);
        bread7.setY(-500.0f);
        bread8.setY(-500.0f);

        // TODO: Step 7: Get New Bread's Y
        bread1X = bread1.getX();
        bread2X = bread2.getX();
        bread3X = bread3.getX();
        bread4X = bread4.getX();
        bread5X = bread5.getX();
        bread6X = bread6.getX();
        bread7X = bread7.getX();
        bread8X = bread8.getX();

        bread1Y = bread1.getY();
        bread2Y = bread2.getY();
        bread3Y = bread3.getY();
        bread4Y = bread4.getY();
        bread5Y = bread5.getY();
        bread6Y = bread6.getY();
        bread7Y = bread7.getY();
        bread8Y = bread8.getY();

        // Make Bread Visible
        // TODO: Step 8: Make New Bread Visible
        bread1.setVisibility(View.VISIBLE);
        bread2.setVisibility(View.VISIBLE);
        bread3.setVisibility(View.VISIBLE);
        bread4.setVisibility(View.VISIBLE);
        bread5.setVisibility(View.VISIBLE);
        bread6.setVisibility(View.VISIBLE);
        bread7.setVisibility(View.VISIBLE);
        bread8.setVisibility(View.VISIBLE);
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
        newBread.setImageResource(R.drawable.bread_8);

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

            // BREAD 1
            bread1Y += 9;
            float bread1CenterX = bread1X + ((float) bread1.getWidth()/2);
            float bread1CenterY = bread1Y + ((float) bread1.getHeight()/2);
            if (hitCheck(bread1CenterX, bread1CenterY)) {
                bread1X = (float)Math.floor(Math.random() * (screenWidth - bread1.getWidth() - 100.0f));
                bread1Y = -500.0f;
                currentScore += 100;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread1.getY() > screenHeight) {
                bread1X = (float)Math.floor(Math.random() * (screenWidth - bread1.getWidth() - 100.0f));
                bread1Y = -500.0f;
                currentScore -= 25;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 1
            bread1.setX(bread1X);
            bread1.setY(bread1Y);

            // BREAD 2
            bread2Y += 10;
            float bread2CenterX = bread2X + ((float) bread2.getWidth()/2);
            float bread2CenterY = bread2Y + ((float) bread2.getHeight()/2);
            if (hitCheck(bread2CenterX, bread2CenterY)) {
                bread2X = (float)Math.floor(Math.random() * (screenWidth - bread2.getWidth() - 100.0f));
                bread2Y = -500.0f;
                currentScore += 150;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread2.getY() > screenHeight) {
                bread2X = (float)Math.floor(Math.random() * (screenWidth - bread2.getWidth() - 100.0f));
                bread2Y = -500.0f;
                currentScore -= 50;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 2
            bread2.setX(bread2X);
            bread2.setY(bread2Y);

            // BREAD 3
            bread3Y += 11;
            float bread3CenterX = bread3X + ((float) bread3.getWidth()/2);
            float bread3CenterY = bread3Y + ((float) bread3.getHeight()/2);
            if (hitCheck(bread3CenterX, bread3CenterY)) {
                bread3X = (float)Math.floor(Math.random() * (screenWidth - bread3.getWidth() - 100.0f));
                bread3Y = -500.0f;
                currentScore += 200;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread3.getY() > screenHeight) {
                bread3X = (float)Math.floor(Math.random() * (screenWidth - bread3.getWidth() - 100.0f));
                bread3Y = -500.0f;
                currentScore -= 75;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 3
            bread3.setX(bread3X);
            bread3.setY(bread3Y);

            // BREAD 4
            bread4Y += 12;
            float bread4CenterX = bread4X + ((float) bread4.getWidth()/2);
            float bread4CenterY = bread4Y + ((float) bread4.getHeight()/2);
            if (hitCheck(bread4CenterX, bread4CenterY)) {
                bread4X = (float)Math.floor(Math.random() * (screenWidth - bread4.getWidth() - 100.0f));
                bread4Y = -500.0f;
                currentScore += 250;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread4.getY() > screenHeight) {
                bread4X = (float)Math.floor(Math.random() * (screenWidth - bread4.getWidth() - 100.0f));
                bread4Y = -500.0f;
                currentScore -= 100;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 4
            bread4.setX(bread4X);
            bread4.setY(bread4Y);

            // BREAD 5
            bread5Y += 13;
            float bread5CenterX = bread5X + ((float) bread5.getWidth()/2);
            float bread5CenterY = bread5Y + ((float) bread5.getHeight()/2);
            if (hitCheck(bread5CenterX, bread5CenterY)) {
                bread5X = (float)Math.floor(Math.random() * (screenWidth - bread5.getWidth() - 100.0f));
                bread5Y = -500.0f;
                currentScore += 300;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread5.getY() > screenHeight) {
                bread5X = (float)Math.floor(Math.random() * (screenWidth - bread5.getWidth() - 100.0f));
                bread5Y = -500.0f;
                currentScore -= 125;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 5
            bread5.setX(bread5X);
            bread5.setY(bread5Y);

            // BREAD 6
            bread6Y += 14;
            float bread6CenterX = bread6X + ((float) bread6.getWidth()/2);
            float bread6CenterY = bread6Y + ((float) bread6.getHeight()/2);
            if (hitCheck(bread6CenterX, bread6CenterY)) {
                bread6X = (float)Math.floor(Math.random() * (screenWidth - bread6.getWidth() - 100.0f));
                bread6Y = -500.0f;
                currentScore += 350;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread6.getY() > screenHeight) {
                bread6X = (float)Math.floor(Math.random() * (screenWidth - bread6.getWidth() - 100.0f));
                bread6Y = -500.0f;
                currentScore -= 150;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 6
            bread6.setX(bread6X);
            bread6.setY(bread6Y);

            // BREAD 7
            bread7Y += 14;
            float bread7CenterX = bread7X + ((float) bread7.getWidth()/2);
            float bread7CenterY = bread7Y + ((float) bread7.getHeight()/2);
            if (hitCheck(bread7CenterX, bread7CenterY)) {
                bread7X = (float)Math.floor(Math.random() * (screenWidth - bread7.getWidth() - 100.0f));
                bread7Y = -500.0f;
                currentScore += 400;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread7.getY() > screenHeight) {
                bread7X = (float)Math.floor(Math.random() * (screenWidth - bread7.getWidth() - 100.0f));
                bread7Y = -500.0f;
                currentScore -= 200;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 7
            bread7.setX(bread7X);
            bread7.setY(bread7Y);

            // BREAD 8
            bread8Y += 14;
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

            // TODO: Step 9: Add New Bread

        }
        else {
            endGame();
        }

    }

    public boolean hitCheck(float x, float y) {
        return handX <= x && x <= handX + handSize && handY <= y && y <= screenHeight;
    }

    // TODO: Step 10: Max Score
    int maxScore = 28000;
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
        bread1.setVisibility(View.INVISIBLE);
        bread2.setVisibility(View.INVISIBLE);
        bread3.setVisibility(View.INVISIBLE);
        bread4.setVisibility(View.INVISIBLE);
        bread5.setVisibility(View.INVISIBLE);
        bread6.setVisibility(View.INVISIBLE);
        bread7.setVisibility(View.INVISIBLE);
        bread8.setVisibility(View.INVISIBLE);

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
            editor.putInt("LEVEL_6_STATUS", 1);
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
            editor.putInt("LEVEL_6_STRAWBERRIES", earnedStrawberries);
            editor.apply();
        }

        // UPDATE HIGH SCORE
        if (currentScore > highScore) {
            highScore = currentScore;
            SharedPreferences.Editor editor = dataLevel.edit();
            editor.putInt("LEVEL_6_HIGH_SCORE", highScore);
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
        String strCurrentDay = getString(R.string.d6);
        String strTargetScore = getString(R.string.td6);

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
                presentLevel7(v);
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
        String strCurrentDay = getString(R.string.d6);
        String strTargetScore = getString(R.string.td6);

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

    public void presentLevel7(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, Level7.class);
        intent.putExtra(Level7.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(Level7.EXTRA_CIRCULAR_REVEAL_Y, revealY);

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
