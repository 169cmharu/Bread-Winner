package com.everyday.breadwinner;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Objects;

public class CreditsActivity extends AppCompatActivity {
    private View decorView;
    private View mainLayout;

    HomeWatcher mHomeWatcher;
    Dialog mainMenuDialog;
    Button hamburger;
    boolean musicFlag, soundFlag = true;
    private SoundPlayer soundPlayer;

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private int revealX;
    private int revealY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

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
        mainMenuDialog = new Dialog(this);
        hamburger = findViewById(R.id.btnHamburger);

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

        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Pulse)
                        .duration(400)
                        .playOn(hamburger);
                launchMenu();
                soundPlayer.playButtonClicked();
            }
        });

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

        creditsBtn.setVisibility(View.INVISIBLE);

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
                mainMenuDialog.dismiss();
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
                    music.setClass(CreditsActivity.this, HomeMusicService.class);
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
