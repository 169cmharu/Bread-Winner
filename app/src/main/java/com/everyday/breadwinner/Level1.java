package com.everyday.breadwinner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Level1 extends AppCompatActivity implements View.OnTouchListener {
    private View decorView;

    // Frame
    private int screenHeight, screenWidth;

    // Dialogs
    Dialog successDialog, failDialog, mainMenuDialog;

    // Images
    private ImageView bread1, bread2, bread3;
    private ImageView sb1, sb2, sb3;
    private ImageView hand;

    // Button
    Button hamburger;

    // Hand Size
    private int handSize;

    // Positions
    private float handX, handY;
    private float bread1X, bread1Y;
    private float bread2X, bread2Y;
    private float bread3X, bread3Y;

    // Day
    private TextView currentDay;

    // Score
    private TextView scoreLabel;
    private int currentScore, highScore, currentStrawberries, earnedStrawberries, timeCount;
    private SharedPreferences dataLevel1;

    // Class
    private Timer timer;
    private Handler handler = new Handler();
    private SoundPlayer soundPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_1);

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

        // FIND IDs
        hamburger = findViewById(R.id.btnHamburger);
        hand = findViewById(R.id.hand);
        scoreLabel = findViewById(R.id.score);
        currentDay = findViewById(R.id.c_day);
        sb1 = findViewById(R.id.straw_1);
        sb2 = findViewById(R.id.straw_2);
        sb3 = findViewById(R.id.straw_3);

        bread1 = findViewById(R.id.bread_1);
        bread2 = findViewById(R.id.bread_2);
        bread3 = findViewById(R.id.bread_3);

        // Dialog Initialization
        mainMenuDialog = new Dialog(this);
        successDialog = new Dialog(this);
        failDialog = new Dialog(this);

        // SET DAY
        currentDay.setText(R.string.d1);

        // GET HIGH SCORE
        dataLevel1 = getSharedPreferences("LEVEL_1_DATA", Context.MODE_PRIVATE);
        highScore = dataLevel1.getInt("LEVEL_1_HIGH_SCORE", 0);

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
        bread1.setY(3000.0f);
        bread2.setY(3000.0f);
        bread3.setY(3000.0f);

        bread1Y = bread1.getY();
        bread2Y = bread2.getY();
        bread3Y = bread3.getY();

        // Make Bread Visible
        bread1.setVisibility(View.VISIBLE);
        bread2.setVisibility(View.VISIBLE);
        bread3.setVisibility(View.VISIBLE);

        // Set OnTouchListener
        hand.setOnTouchListener(this);

        // Set Score to 0
        currentScore = 0;
        scoreLabel.setText(String.valueOf(currentScore));

        currentScore = 150;

        // START TIMER
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
            bread1Y += 10;
            float bread1CenterX = bread1X + ((float) bread1.getWidth()/2);
            float bread1CenterY = bread1Y + ((float) bread1.getHeight()/2);
            if (hitCheck(bread1CenterX, bread1CenterY)) {
                currentScore += 100;
                scoreLabel.setText(String.valueOf(currentScore));
                bread1Y = -500.0f;
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread1.getY() > screenHeight) {
                bread1X = (float)Math.floor(Math.random() * (screenWidth - bread1.getWidth()));
                bread1Y = -500.0f;
                currentScore -= 50;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 1
            bread1.setX(bread1X);
            bread1.setY(bread1Y);

            // BREAD 2
            bread2Y += 12;
            float bread2CenterX = bread2X + ((float) bread2.getWidth()/2);
            float bread2CenterY = bread2Y + ((float) bread2.getHeight()/2);
            if (hitCheck(bread2CenterX, bread2CenterY)) {
                currentScore += 150;
                scoreLabel.setText(String.valueOf(currentScore));
                bread2Y = -500.0f;
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread2.getY() > screenHeight) {
                bread2X = (float)Math.floor(Math.random() * (screenWidth - bread2.getWidth()));
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
            bread3Y += 14;
            float bread3CenterX = bread3X + ((float) bread3.getWidth()/2);
            float bread3CenterY = bread3Y + ((float) bread3.getHeight()/2);
            if (hitCheck(bread3CenterX, bread3CenterY)) {
                currentScore += 200;
                scoreLabel.setText(String.valueOf(currentScore));
                bread3Y = -500.0f;
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread3.getY() > screenHeight) {
                bread3X = (float)Math.floor(Math.random() * (screenWidth - bread3.getWidth()));
                bread3Y = -500.0f;
                currentScore -= 50;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 3
            bread3.setX(bread3X);
            bread3.setY(bread3Y);
        }
        else {
            endGame();
        }

    }

    public boolean hitCheck(float x, float y) {
        return handX <= x && x <= handX + handSize && handY <= y && y <= screenHeight;
    }

    int maxScore = 5500;
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

        hand.setVisibility(View.INVISIBLE);
        bread1.setVisibility(View.INVISIBLE);
        bread2.setVisibility(View.INVISIBLE);
        bread3.setVisibility(View.INVISIBLE);

        if (currentScore >= firstCut) {
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
                // TODO: Step 10: Change d & td
                String strCurrentDay = getString(R.string.d1);
                String strTargetScore = getString(R.string.td1);

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

                // TODO: Step 11: Change Next Day
                nextDay = successDialog.findViewById(R.id.btnNext);
                nextDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPlayer.playButtonClicked();
                        finish();
                        Intent startDay = new Intent(Level1.this, Level2.class);
                        startActivity(startDay);
                    }
                });

                // TODO: Step 12: Change packageContext
                backToMenu = successDialog.findViewById(R.id.btnMenu);
                backToMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPlayer.playButtonClicked();
                        finish();
                        Intent startDay = new Intent(Level1.this, ChooseLevelActivity.class);
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
                // TODO: Step 14: Change d & td
                String strCurrentDay = getString(R.string.d1);
                String strTargetScore = getString(R.string.td1);

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

                // TODO: Step 15: Change Next Day
                nextDay = successDialog.findViewById(R.id.btnNext);
                nextDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        soundPlayer.playButtonClicked();
                        Intent startDay = new Intent(Level1.this, Level2.class);
                        startActivity(startDay);
                    }
                });

                // TODO: Step 16: Change packageContext
                backToMenu = successDialog.findViewById(R.id.btnMenu);
                backToMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        soundPlayer.playButtonClicked();
                        Intent startDay = new Intent(Level1.this, ChooseLevelActivity.class);
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
                // TODO: Step 17: Change d & td
                String strCurrentDay = getString(R.string.d1);
                String strTargetScore = getString(R.string.td1);

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

                // TODO: Step 18: Change Next Day
                nextDay = successDialog.findViewById(R.id.btnNext);
                nextDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        soundPlayer.playButtonClicked();
                        Intent startDay = new Intent(Level1.this, Level2.class);
                        startActivity(startDay);
                    }
                });

                // TODO: Step 19: Change packageContext
                backToMenu = successDialog.findViewById(R.id.btnMenu);
                backToMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        soundPlayer.playButtonClicked();
                        Intent startDay = new Intent(Level1.this, ChooseLevelActivity.class);
                        startActivity(startDay);
                    }
                });
                currentStrawberries = 3;
            }

            // UPDATE EARNED STRAWBERRIES
            if (currentStrawberries > earnedStrawberries) {
                earnedStrawberries = currentStrawberries;
                SharedPreferences.Editor editor = dataLevel1.edit();
                editor.putInt("LEVEL_1_STRAWBERRIES", earnedStrawberries);
                editor.apply();
            }

            // MARK DAY AS COMPLETED
            SharedPreferences.Editor editor = dataLevel1.edit();
            editor.putInt("LEVEL_1_STATUS", 1);
            editor.apply();
        }
        else if (currentScore < firstCut){
            soundPlayer.playLevelFailed();

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
            // TODO: Step 20: Change d & td
            String strCurrentDay = getString(R.string.d1);
            String strTargetScore = getString(R.string.td1);

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

            // TODO: Step 21: Change Next Day
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

            // TODO: Step 22: Change packageContext
            backToMenu = failDialog.findViewById(R.id.btnRetryMenu);
            backToMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    soundPlayer.playButtonClicked();
                    Intent backMenu = new Intent(Level1.this, ChooseLevelActivity.class);
                    startActivity(backMenu);
                }
            });
        }

        // UPDATE HIGH SCORE
        if (currentScore > highScore) {
            highScore = currentScore;
            SharedPreferences.Editor editor = dataLevel1.edit();
            editor.putInt("LEVEL_1_HIGH_SCORE", highScore);
            editor.apply();
        }
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
