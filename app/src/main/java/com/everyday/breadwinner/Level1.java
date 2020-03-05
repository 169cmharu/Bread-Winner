package com.everyday.breadwinner;

import androidx.appcompat.app.AppCompatActivity;

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

public class Level1 extends AppCompatActivity implements View.OnTouchListener {
    private View decorView;

    // Frame
    private int screenHeight, screenWidth;

    // Dialogs
    Dialog instructionDialog, successDialog, failDialog, mainMenuDialog;

    // Images
    // TODO: Step 1: Add New Bread ImageView
    private ImageView bread1, bread2, bread3;
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
        bread1 = findViewById(R.id.bread_1);
        bread2 = findViewById(R.id.bread_2);
        bread3 = findViewById(R.id.bread_3);

        // Dialog Initialization
        instructionDialog = new Dialog(this);
        mainMenuDialog = new Dialog(this);
        successDialog = new Dialog(this);
        failDialog = new Dialog(this);
        // TODO: Initialized new Bread Dialog

        // SET DAY
        // TODO: Step 4: Change Day
        currentDay.setText(R.string.d1);

        // GET HIGH SCORE
        // TODO: Step 5: Change dataLevel
        dataLevel = getSharedPreferences("LEVEL_DATA", Context.MODE_PRIVATE);
        highScore = dataLevel.getInt("LEVEL_1_HIGH_SCORE", 0);

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
        bread1.setX((float)Math.floor(Math.random() * (screenWidth - bread1.getWidth() - 20.0f)));
        bread2.setX((float)Math.floor(Math.random() * (screenWidth - bread2.getWidth() - 20.0f)));
        bread3.setX((float)Math.floor(Math.random() * (screenWidth - bread3.getWidth() - 20.0f)));

        bread1.setY(-500.0f);
        bread2.setY(-500.0f);
        bread3.setY(-500.0f);

        // TODO: Step 7: Get New Bread's Y
        bread1X = bread1.getX();
        bread2X = bread2.getX();
        bread3X = bread3.getX();

        bread1Y = bread1.getY();
        bread2Y = bread2.getY();
        bread3Y = bread3.getY();

        // Make Bread Visible
        // TODO: Step 8: Make New Bread Visible
        bread1.setVisibility(View.VISIBLE);
        bread2.setVisibility(View.VISIBLE);
        bread3.setVisibility(View.VISIBLE);
        hand.setVisibility(View.VISIBLE);
        hand.setVisibility(View.INVISIBLE);

        // Set OnTouchListener
        hand.setOnTouchListener(this);

        // Set Score to 0
        currentScore = 0;
        scoreLabel.setText(String.valueOf(currentScore));

        // LAUNCH INSTRUCTION DIALOG
        launchInstructionDialog();
    }

    public void launchInstructionDialog() {
        hamburger.setBackgroundResource(R.drawable.close);
        instructionDialog.setContentView(R.layout.popup_instruction);
        Objects.requireNonNull(instructionDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        // Initialization of Variables + Find IDs in  New Bread Dialog
        Button accept;
        accept = instructionDialog.findViewById(R.id.great);

        // Show Dialog
        instructionDialog.show();
        // Prevent Dialog from Getting Dismissed
        instructionDialog.setCancelable(false);
        instructionDialog.setCanceledOnTouchOutside(false);

        // Hide Shadows
        instructionDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
        instructionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        instructionDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        instructionDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPlayer.playButtonClicked();
                instructionDialog.dismiss();
            }
        });

        instructionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
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
                bread1X = (float)Math.floor(Math.random() * (screenWidth - bread1.getWidth() - 20.0f));
                bread1Y = -500.0f;
                currentScore += 100;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread1.getY() > screenHeight) {
                bread1X = (float)Math.floor(Math.random() * (screenWidth - bread1.getWidth() - 20.0f));
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
                bread2X = (float)Math.floor(Math.random() * (screenWidth - bread2.getWidth() - 20.0f));
                bread2Y = -500.0f;
                currentScore += 150;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread2.getY() > screenHeight) {
                bread2X = (float)Math.floor(Math.random() * (screenWidth - bread2.getWidth() - 20.0f));
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
                bread3X = (float)Math.floor(Math.random() * (screenWidth - bread3.getWidth() - 20.0f));
                bread3Y = -500.0f;
                currentScore += 200;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitCorrectBasket();
            }
            if (bread3.getY() > screenHeight) {
                bread3X = (float)Math.floor(Math.random() * (screenWidth - bread3.getWidth() - 20.0f));
                bread3Y = -500.0f;
                currentScore -= 75;
                scoreLabel.setText(String.valueOf(currentScore));
                checkNumOfStrawberries();
                soundPlayer.playHitWrongBasket();
            }
            // Update Location of Bread 3
            bread3.setX(bread3X);
            bread3.setY(bread3Y);

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
    int maxScore = 4600;
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
                // TODO: Step 12: Change d & td
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

                // TODO: Step 13: Change Next Day
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

                // TODO: Step 14: Change packageContext
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
                // TODO: Step 15: Change d & td
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

                // TODO: Step 16: Change Next Day
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

                // TODO: Step 17: Change packageContext
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
                // TODO: Step 18: Change d & td
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

                // TODO: Step 19: Change Next Day
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

                // TODO: Step 20: Change packageContext
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

            // MARK DAY AS COMPLETED
            // TODO: Step 22: Change Data Level
            SharedPreferences.Editor editor = dataLevel.edit();
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
            // TODO: Step 23: Change d & td
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

            // TODO: Step 24: Change Next Day
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

            // TODO: Step 25: Change packageContext
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

        // UPDATE EARNED STRAWBERRIES
        if (currentStrawberries > earnedStrawberries) {
            earnedStrawberries = currentStrawberries;
            SharedPreferences.Editor editor = dataLevel.edit();
            editor.putInt("LEVEL_1_STRAWBERRIES", earnedStrawberries);
            editor.apply();
        }

        // UPDATE HIGH SCORE
        if (currentScore > highScore) {
            highScore = currentScore;
            SharedPreferences.Editor editor = dataLevel.edit();
            editor.putInt("LEVEL_1_HIGH_SCORE", highScore);
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
