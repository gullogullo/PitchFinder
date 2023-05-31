package com.example.pitchfinder;
//import static java.lang.Math.log10;
//import static java.lang.Math.pow;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
//import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.greenrobot.eventbus.EventBus;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import com.gusakov.library.PulseCountDown;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends Activity implements View.OnClickListener {

    private TextView countDownTimerTxt, scoreTxt, startPauseMessage;

    private ImageButton resPauseBtn;

    private CountDownTimer mainCountDownTimer;

    private PulseCountDown startGameCountDownTimer;

    private int periodMeasure8;//, periodMeasure16;

    private int lengthArraySteps;

    int[] holeIdArrayL = {
            R.id.hole9thL,
            R.id.hole10thL,
            R.id.hole11thL,
            R.id.hole12thL,
            R.id.hole13thL,
            R.id.hole14thL,
            R.id.hole15thL,
            R.id.seventhMoleBtn};

    final int[][] rhythmsArrayL = {{3, 9, 12}, {3, 5, 12}, {3, 4, 13}, {8, 9, 13}};

    int[] holeIdArrayC = {
            R.id.hole9thC,
            R.id.hole10thC,
            R.id.hole11thC,
            R.id.hole12thC,
            R.id.hole13thC,
            R.id.hole14thC,
            R.id.hole15thC,
            R.id.eigthMoleBtn};

    final int[][] rhythmsArrayC = {{5, 13}, {7, 14}, {9, 10}, {5, 13}};

    int[] holeIdArrayR = {
            R.id.hole9thR,
            R.id.hole10thR,
            R.id.hole11thR,
            R.id.hole12thR,
            R.id.hole13thR,
            R.id.hole14thR,
            R.id.hole15thR,
            R.id.ninethMoleBtn};

    final int[][] rhythmsArrayR = {{1, 2, 10}, {1, 4, 14}, {1, 7, 15}, {1, 4, 7}};

    int[] idsRhythmL = {};
    int[] idsSoundL = {};
    int[] idsRhythmC = {};
    int[] idsSoundC = {};
    int[] idsRhythmR = {};
    int[] idsSoundR = {};

    int startPattern = 0;

    private static boolean moleIsActive = false, countDownWorks = false;

    private final float gainSnare = 0.3f;
    private final float gainCowbell = 0.3f;
    private final float gainKick = 0.3f;

    private final Handler handlerL0 = new Handler();
    //private final Handler handlerL1 = new Handler();
    //private final Handler handlerL2 = new Handler();
    //private final Handler handlerC0 = new Handler();
    //private final Handler handlerC1 = new Handler();
    //private final Handler handlerR0 = new Handler();
    //private final Handler handlerR1 = new Handler();
    //private final Handler handlerR2 = new Handler();
    private boolean firstL0, firstL1, firstL2, firstC0, firstC1, firstR0, firstR1, firstR2 = false;

    private Animation errorAnim;

    private ImageView errorIcon;
    private long curSeconds = 0, curMillies = 0;

    private static final int GAME_TIME = 45000;

    //public static String FINAL_SCORE_DRUM_VALUE_EXTRA = "FINAL_SCORE_DRUM_VALUE_EXTRA";
    public static String FINAL_SCORE_PITCH_VALUE_EXTRA = "FINAL_SCORE_PITCH_VALUE_EXTRA";
    public static int trialsCounter;

    public StringBuilder dataString = EventBus.getDefault().removeStickyEvent(PitchActivity2.SaveStringBuilderEvent.class).getSb();

    @Override
    public void onBackPressed() {
        pauseGame();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.pauseGameBtn) {
            rePauseTimer();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Disable landscape mode
        GameActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initUiElements();

        getValuesFromPitchActivity();

        resPauseBtn.setOnClickListener(this);

        startPulse(GAME_TIME);
        PdAudio.startAudio(this);


        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        ScreenReceiver screenReceiver = new ScreenReceiver();
        registerReceiver(screenReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseGame();
    }

    private void initUiElements(){

        ImageButton hole1L = findViewById(R.id.hole9thL);
        ImageButton hole2L = findViewById(R.id.hole10thL);
        ImageButton hole3L = findViewById(R.id.hole11thL);
        ImageButton hole4L = findViewById(R.id.hole12thL);
        ImageButton hole5L = findViewById(R.id.hole13thL);
        ImageButton hole6L = findViewById(R.id.hole14thL);
        ImageButton hole7L = findViewById(R.id.hole15thL);
        ImageButton hole8L = findViewById(R.id.seventhMoleBtn);
        ImageButton hole1C = findViewById(R.id.hole9thC);
        ImageButton hole2C = findViewById(R.id.hole10thC);
        ImageButton hole3C = findViewById(R.id.hole11thC);
        ImageButton hole4C = findViewById(R.id.hole12thC);
        ImageButton hole5C = findViewById(R.id.hole13thC);
        ImageButton hole6C = findViewById(R.id.hole14thC);
        ImageButton hole7C = findViewById(R.id.hole15thC);
        ImageButton hole8C = findViewById(R.id.eigthMoleBtn);
        ImageButton hole1R = findViewById(R.id.hole9thR);
        ImageButton hole2R = findViewById(R.id.hole10thR);
        ImageButton hole3R = findViewById(R.id.hole11thR);
        ImageButton hole4R = findViewById(R.id.hole12thR);
        ImageButton hole5R = findViewById(R.id.hole13thR);
        ImageButton hole6R = findViewById(R.id.hole14thR);
        ImageButton hole7R = findViewById(R.id.hole15thR);
        ImageButton hole8R = findViewById(R.id.ninethMoleBtn);

        hole1L.setEnabled(false);
        hole2L.setEnabled(false);
        hole3L.setEnabled(false);
        hole4L.setEnabled(false);
        hole5L.setEnabled(false);
        hole6L.setEnabled(false);
        hole7L.setEnabled(false);
        hole1C.setEnabled(false);
        hole2C.setEnabled(false);
        hole3C.setEnabled(false);
        hole4C.setEnabled(false);
        hole5C.setEnabled(false);
        hole6C.setEnabled(false);
        hole7C.setEnabled(false);
        hole1R.setEnabled(false);
        hole2R.setEnabled(false);
        hole3R.setEnabled(false);
        hole4R.setEnabled(false);
        hole5R.setEnabled(false);
        hole6R.setEnabled(false);
        hole7R.setEnabled(false);
        hole8L.setEnabled(true);
        hole8C.setEnabled(true);
        hole8R.setEnabled(true);

        /*
        hole1L.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole2L.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole3L.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole4L.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole5L.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole6L.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole7L.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole8L.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole1C.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole2C.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole3C.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole4C.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole5C.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole6C.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole7C.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole8C.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole1R.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole2R.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole3R.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole4R.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole5R.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole6R.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole7R.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        hole8R.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        */

        countDownTimerTxt = findViewById(R.id.countDownTimerTxt);
        scoreTxt = findViewById(R.id.scoreTxt);
        startPauseMessage = findViewById(R.id.startPauseMessageTxt);

        startGameCountDownTimer =  findViewById(R.id.pulseCountDown);

        resPauseBtn = findViewById(R.id.pauseGameBtn);

        Bundle bundle = getIntent().getExtras();
        int trials = bundle.getInt("trials");
        double bpm = 70.0 + 10 * trials;
        int periodMeasure4 = 60000 / (int) bpm;
        periodMeasure8 = periodMeasure4 / 2;
        //periodMeasure16 = periodMeasure8 / 2;
        lengthArraySteps = holeIdArrayL.length;
        Random random = new Random();
        int randomPattern = random.nextInt(3);
        startPattern = randomPattern;
        //idsRhythmL = rhythmsArrayL[randomPattern];
        //idsRhythmC = rhythmsArrayC[randomPattern];
        //idsRhythmR = rhythmsArrayR[randomPattern];
        int[] arrayL = new int[rhythmsArrayL[randomPattern].length];
        int[] arrayL8th = new int[rhythmsArrayL[randomPattern].length];
        for (int i = 0; i < rhythmsArrayL[randomPattern].length; i++) {
            arrayL[i] = rhythmsArrayL[randomPattern][i] * 2 - 1;
            arrayL8th[i] = rhythmsArrayL[randomPattern][i];
        }
        idsSoundL = arrayL;
        idsRhythmL = arrayL8th;
        int[] arrayC = new int[rhythmsArrayC[randomPattern].length];
        int[] arrayC8th = new int[rhythmsArrayC[randomPattern].length];
        for (int i = 0; i < rhythmsArrayC[randomPattern].length; i++) {
            arrayC[i] = rhythmsArrayC[randomPattern][i] * 2 - 1;
            arrayC8th[i] = rhythmsArrayC[randomPattern][i];
        }
        idsSoundC = arrayC;
        idsRhythmC = arrayC8th;
        int[] arrayR = new int[rhythmsArrayR[randomPattern].length];
        int[] arrayR8th = new int[rhythmsArrayR[randomPattern].length];
        for (int i = 0; i < rhythmsArrayR[randomPattern].length; i++) {
            arrayR[i] = rhythmsArrayR[randomPattern][i] * 2 - 1;
            arrayR8th[i] = rhythmsArrayR[randomPattern][i];
        }
        idsSoundR = arrayR;
        idsRhythmR = arrayR8th;
        errorAnim = AnimationUtils.loadAnimation(this, R.anim.error);
        errorIcon = findViewById(R.id.errorView);
        errorIcon.setVisibility(View.GONE);

    }

    @SuppressLint("SetTextI18n")
    private void getValuesFromPitchActivity(){
        Bundle bundle = getIntent().getExtras();
        //FINAL_SCORE_DRUM_VALUE_EXTRA = bundle.getString("FINAL_SCORE_DRUM_VALUE_EXTRA");
        //Log.e("FINAL_DRUM BEAT", FINAL_SCORE_DRUM_VALUE_EXTRA);
        //FINAL_SCORE_PITCH_VALUE_EXTRA = bundle.getString("FINAL_SCORE_PITCH_VALUE_EXTRA");
        //Log.e("FINAL_PITCH BEAT", FINAL_SCORE_PITCH_VALUE_EXTRA);
        trialsCounter = bundle.getInt("trials");
        //Log.e("dataString FROM DRUM", dataString.toString());
        //FINAL_SCORE_DRUM_VALUE_EXTRA = bundle.getString("FINAL_SCORE_DRUM_VALUE_EXTRA").toString()
        //int drumScoreFromPitch = 0;
        //if (trialsCounter != 1) {
        //    drumScoreFromPitch = Integer.parseInt(bundle.getString("FINAL_SCORE_DRUM_VALUE_EXTRA"));
        //}
        //Log.e("drumScoreFromPitch", String.valueOf(drumScoreFromPitch));
        FINAL_SCORE_PITCH_VALUE_EXTRA = bundle.getString("FINAL_SCORE_PITCH_VALUE_EXTRA");
        scoreTxt = findViewById(R.id.scoreTxt);
        scoreTxt.setText(String.valueOf(
                Integer.parseInt(scoreTxt.getText().toString()) +
                        Integer.parseInt(FINAL_SCORE_PITCH_VALUE_EXTRA)));
        //Log.e("FINAL_DRUM FROM PITCH", FINAL_SCORE_DRUM_VALUE_EXTRA);
        //Log.e("FINAL_PITCH FROM PITCH", FINAL_SCORE_PITCH_VALUE_EXTRA);
    }

    // Procedure process pause and resume functions
    private void rePauseTimer(){

        if (countDownWorks){
            pauseGame();
            return;
        }
        resumeGame();
    }

    // Pause game
    private void pauseGame(){
        countDownWorks = false;
        mainCountDownTimer.cancel();
        //showPauseMessage();
        resPauseBtn.setImageResource(R.drawable.play_selector);
        //PdAudio.stopAudio();
    }

    // Resume game
    private void resumeGame(){
        startPauseMessage.setVisibility(View.GONE);
        int time = (int) curMillies + (int) curSeconds*1000;
        startPulse(time);
        super.onResume();
        PdAudio.startAudio(this);
        resPauseBtn.setImageResource(R.drawable.pause_selector);
    }

    // Start pulse timer before game
    private void startPulse(int time){
        startGameCountDownTimer.start(() -> {
            //showStartMessage(time);
            GameActivity.this.startTimer(time);
        });
    }


    /*
    @SuppressLint("SetTextI18n")
    private void showPauseMessage(){
        startPauseMessage.setText("Pause");
        startPauseMessage.setVisibility(View.VISIBLE);
    }*/

    /*
    @SuppressLint("SetTextI18n")
    private void showStartMessage(){
        startPauseMessage.setText("Start");
        startPauseMessage.setVisibility(View.VISIBLE);
        startPauseMessage.performClick();

        final Handler handler = new Handler();
        final Runnable runnable = () -> {
            startPauseMessage.setEnabled(false);
            startPauseMessage.setVisibility(View.GONE);
            System.out.println("here");
        };

        handler.postDelayed(runnable, 1000);

    }*/

    // Start main game timer
    private void startTimer(int time){

        // Make resume pause Button visible
        resPauseBtn.setVisibility(View.VISIBLE);

        // Interval - 1 second
        // Timer - 30 seconds
        mainCountDownTimer = new CountDownTimer(time, 1) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                countDownWorks = true;
                long seconds = (millisUntilFinished / 1000) % 60;
                // Save current seconds remaining
                curSeconds = seconds;
                long millies = millisUntilFinished % 1000;
                // Save current millies remaining
                curMillies = millies;

                String stringSeconds = "", stringMillies = "";

                // Format seconds
                if (String.valueOf(seconds).length() < 2)
                    stringSeconds += "  " + seconds;
                else if (String.valueOf(seconds).length() == 2)
                    stringSeconds += seconds;

                // Format millies
                if (String.valueOf(millies).length() < 2)
                    stringMillies += "00" + millies;
                else if (String.valueOf(millies).length() < 3)
                    stringMillies += "0" + millies;
                else if (String.valueOf(millies).length() == 3)
                    stringMillies += millies;

                if (seconds < 10 && seconds > 3) {
                    countDownTimerTxt.setTextColor(getResources().getColor(R.color.dark_orange_color));
                    }
                else if (seconds <=3)
                    countDownTimerTxt.setTextColor(getResources().getColor(R.color.red_record_color));

                countDownTimerTxt.setText(stringSeconds + "." + stringMillies);
                if(!moleIsActive) {
                    showDrum();
                }
            }
            // When the game is over it will be 0.000
            @SuppressLint("SetTextI18n")
            public void onFinish() {
                countDownTimerTxt.setText("0.000");
                Bundle bundle = getIntent().getExtras();
                int trials = bundle.getInt("trials");
                if (trials == 5) {
                    startResultActivity();}
                else {
                    startPitchActivity();
                }
            }
        }.start();
    }

    // Start game
    private void showDrum(){
        moleIsActive = true;

        firstL0 = true;
        firstL1 = true;
        firstL2 = true;
        firstC0 = true;
        firstC1 = true;
        firstR0 = true;
        firstR1 = true;
        firstR2 = true;

        ImageButton activeL0 = findViewById(holeIdArrayL[idsRhythmL[0] % lengthArraySteps]);
        PdBase.sendBang("bangInit");
        PdBase.sendFloat("gainHat", 2.0f);
        PdBase.sendBang("bangClHat");
        if (idsRhythmL[0] < lengthArraySteps) {
            activeL0.setImageResource(R.drawable.cowbellhole);
        }
        if (idsRhythmL[0] == lengthArraySteps - 1) {
            PdBase.sendFloat("gainCowbell", gainCowbell);
            PdBase.sendBang("bangCowbell");
        }

        activeL0.setOnClickListener(view -> {
            //firstL0 = true;
            if (idsRhythmL[0] == lengthArraySteps - 1) {
                if (firstL0) {
                    //PdBase.sendFloat("gainCowbell", gainCowbell);
                    //PdBase.sendBang("bangCowbell");
                    scoreTxt.setText(String.valueOf(Integer.parseInt(scoreTxt.getText().toString()) + 1));
                    final String currentTime = new SimpleDateFormat("HH:mm:ss.SSS",
                            Locale.getDefault()).format(new Date());
                    dataString.append("Bang Cowbell, ").append(gainCowbell).append(", ").append(currentTime).append("\n");
                    firstL0 = false;
                }
            } else {
                activeL0.startAnimation(errorAnim);
                errorIcon.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(() -> errorIcon.setVisibility(View.GONE), 200);}
        });

        ImageButton activeL1 = findViewById(holeIdArrayL[idsRhythmL[1] % lengthArraySteps]);
        if (idsRhythmL[1] < lengthArraySteps) {
            activeL1.setImageResource(R.drawable.cowbellhole);
        }
        if (idsRhythmL[1] == lengthArraySteps - 1) {
            PdBase.sendFloat("gainCowbell", gainCowbell);
            PdBase.sendBang("bangCowbell");
        }

        activeL1.setOnClickListener(view -> {
            if (idsRhythmL[1] == lengthArraySteps - 1) {
                if (firstL1) {
                    //PdBase.sendFloat("gainCowbell", gainCowbell);
                    //PdBase.sendBang("bangCowbell");
                    scoreTxt.setText(String.valueOf(Integer.parseInt(scoreTxt.getText().toString()) + 1));
                    final String currentTime = new SimpleDateFormat("HH:mm:ss.SSS",
                            Locale.getDefault()).format(new Date());
                    dataString.append("Bang Cowbell, ").append(gainCowbell).append(", ").append(currentTime).append("\n");
                    firstL1 = false;
                }
            } else {
                activeL1.startAnimation(errorAnim);
                errorIcon.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(() -> errorIcon.setVisibility(View.GONE), 200);}
        });

        ImageButton activeL2 = findViewById(holeIdArrayL[idsRhythmL[2] % lengthArraySteps]);
        if (idsRhythmL[2] < lengthArraySteps) {
            activeL2.setImageResource(R.drawable.cowbellhole);
        }
        if (idsRhythmL[2] == lengthArraySteps - 1) {
            PdBase.sendFloat("gainCowbell", gainCowbell);
            PdBase.sendBang("bangCowbell");
        }

        activeL2.setOnClickListener(view -> {
            //firstL2 = true;
            if (idsRhythmL[2] == lengthArraySteps - 1) {
                if (firstL2) {
                    //PdBase.sendFloat("gainCowbell", gainCowbell);
                    //PdBase.sendBang("bangCowbell");
                    scoreTxt.setText(String.valueOf(Integer.parseInt(scoreTxt.getText().toString()) + 1));
                    final String currentTime = new SimpleDateFormat("HH:mm:ss.SSS",
                            Locale.getDefault()).format(new Date());
                    dataString.append("Bang Cowbell, ").append(gainCowbell).append(", ").append(currentTime).append("\n");
                    firstL2 = false;
                }
            } else {
                activeL2.startAnimation(errorAnim);
                errorIcon.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(() -> errorIcon.setVisibility(View.GONE), 200);}
        });

        /*
        final Runnable runnableL0 = () -> {
            activeL0.setImageResource(R.drawable.hole_mole);
            //activeL0.setEnabled(false);
            moleIsActive = false;
            //float gain = equalLoudness(4000,60);
            //PdBase.sendBang("bangInit");
            //PdBase.sendFloat("gainHat", 2.0f);
            //PdBase.sendBang("bangClHat");
            idsRhythmL[0]++;
            idsRhythmL[0] = idsRhythmL[0] % (lengthArraySteps * 2);
            //Log.e("idsRhythmL0 8th", String.valueOf(idsRhythmL[0]));
        };*/
        //handlerL0.postDelayed(runnableL0, periodMeasure8);

        /*
        final Runnable runnableL1 = () -> {
            activeL1.setImageResource(R.drawable.hole_mole);
            //activeL1.setEnabled(false);
            idsRhythmL[1]++;
            idsRhythmL[1] = idsRhythmL[1] % (lengthArraySteps * 2);
            //Log.e("idsRhythmL1", String.valueOf(idsRhythmL[1]));
        };*/
        //handlerL1.postDelayed(runnableL1, periodMeasure8);

        /*
        final Runnable runnableL2 = () -> {
            activeL2.setImageResource(R.drawable.hole_mole);
            //activeL2.setEnabled(false);
            idsRhythmL[2]++;
            idsRhythmL[2] = idsRhythmL[2] % (lengthArraySteps * 2);
            //Log.e("idsRhythmL2", String.valueOf(idsRhythmL[2]));
        };*/
        //handlerL2.postDelayed(runnableL2, periodMeasure8);

        ImageButton activeC0 = findViewById(holeIdArrayC[idsRhythmC[0] % lengthArraySteps]);
        if (idsRhythmC[0] < lengthArraySteps) {
            activeC0.setImageResource(R.drawable.snarehole);
        }
        if (idsRhythmC[0] == lengthArraySteps - 1) {
            PdBase.sendFloat("gainSnare", gainSnare);
            PdBase.sendBang("bangSnare");
        }

        activeC0.setOnClickListener(view -> {
            //firstC0 = true;
            if (idsRhythmC[0] == lengthArraySteps - 1) {
                if (firstC0) {
                    //PdBase.sendFloat("gainSnare", gainSnare);
                    //PdBase.sendBang("bangSnare");
                    scoreTxt.setText(String.valueOf(Integer.parseInt(scoreTxt.getText().toString()) + 1));
                    final String currentTime = new SimpleDateFormat("HH:mm:ss.SSS",
                            Locale.getDefault()).format(new Date());
                    dataString.append("Bang Snare, ").append(gainSnare).append(", ").append(currentTime).append("\n");
                    firstC0 = false;
                }
            } else {
                activeC0.startAnimation(errorAnim);
                errorIcon.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(() -> errorIcon.setVisibility(View.GONE), 200);}
        });

        ImageButton activeC1 = findViewById(holeIdArrayC[idsRhythmC[1] % lengthArraySteps]);
        if (idsRhythmC[1] < lengthArraySteps) {
            activeC1.setImageResource(R.drawable.snarehole);
        }
        if (idsRhythmC[1] == lengthArraySteps - 1) {
            PdBase.sendFloat("gainSnare", gainSnare);
            PdBase.sendBang("bangSnare");
        }

        activeC1.setOnClickListener(view -> {
            //firstC1 = true;
            if (idsRhythmC[1] == lengthArraySteps - 1) {
                if (firstC1) {
                    //PdBase.sendFloat("gainSnare", gainSnare);
                    //PdBase.sendBang("bangSnare");
                    scoreTxt.setText(String.valueOf(Integer.parseInt(scoreTxt.getText().toString()) + 1));
                    final String currentTime = new SimpleDateFormat("HH:mm:ss.SSS",
                            Locale.getDefault()).format(new Date());
                    dataString.append("Bang Snare, ").append(gainSnare).append(", ").append(currentTime).append("\n");
                    firstC1 = false;
                }
            } else {
                activeC1.startAnimation(errorAnim);
                errorIcon.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(() -> errorIcon.setVisibility(View.GONE), 200);}
        });

        /*
        final Runnable runnableC0 = () -> {
            activeC0.setImageResource(R.drawable.hole_mole);
            //activeC0.setEnabled(false);
            idsRhythmC[0]++;
            idsRhythmC[0] = idsRhythmC[0] % (lengthArraySteps * 2);
            //Log.e("idsRhythmC0", String.valueOf(idsRhythmC[0]));
        };*/
        //handlerC0.postDelayed(runnableC0, periodMeasure8);

        /*
        final Runnable runnableC1 = () -> {
            activeC1.setImageResource(R.drawable.hole_mole);
            //activeC1.setEnabled(false);
            idsRhythmC[1]++;
            idsRhythmC[1] = idsRhythmC[1] % (lengthArraySteps * 2);
            //Log.e("idsRhythmC1", String.valueOf(idsRhythmC[1]));
        };*/
        //handlerC1.postDelayed(runnableC1, periodMeasure8);

        ImageButton activeR0 = findViewById(holeIdArrayR[idsRhythmR[0] % lengthArraySteps]);
        if (idsRhythmR[0] < lengthArraySteps) {
            activeR0.setImageResource(R.drawable.kickhole);
        }

        if (idsRhythmR[0] == lengthArraySteps - 1) {
            PdBase.sendFloat("gainKick", gainKick);
            PdBase.sendBang("bangKick");
        }

        activeR0.setOnClickListener(view -> {
            //firstR0 = true;
            if (idsRhythmR[0] == lengthArraySteps - 1) {
                if (firstR0) {
                    //PdBase.sendFloat("gainKick", gainKick);
                    //PdBase.sendBang("bangKick");
                    scoreTxt.setText(String.valueOf(Integer.parseInt(scoreTxt.getText().toString()) + 1));
                    final String currentTime = new SimpleDateFormat("HH:mm:ss.SSS",
                            Locale.getDefault()).format(new Date());
                    dataString.append("Bang Kick, ").append(gainKick).append(", ").append(currentTime).append("\n");
                    firstR0 = false;
                }
            } else {
                activeR0.startAnimation(errorAnim);
                errorIcon.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(() -> errorIcon.setVisibility(View.GONE), 200);}
        });

        ImageButton activeR1 = findViewById(holeIdArrayR[idsRhythmR[1] % lengthArraySteps]);
        if (idsRhythmR[1] < lengthArraySteps) {
            activeR1.setImageResource(R.drawable.kickhole);
        }

        if (idsRhythmR[1] == lengthArraySteps - 1) {
            PdBase.sendFloat("gainKick", gainKick);
            PdBase.sendBang("bangKick");
        }

        activeR1.setOnClickListener(view -> {
            //firstR1 = true;
            if (idsRhythmR[1] == lengthArraySteps - 1) {
                if (firstR1) {
                    //PdBase.sendFloat("gainKick", gainKick);
                    //PdBase.sendBang("bangKick");
                    scoreTxt.setText(String.valueOf(Integer.parseInt(scoreTxt.getText().toString()) + 1));
                    final String currentTime = new SimpleDateFormat("HH:mm:ss.SSS",
                            Locale.getDefault()).format(new Date());
                    dataString.append("Bang Kick, ").append(gainKick).append(", ").append(currentTime).append("\n");
                    firstR1 = false;
                }
            } else {
                activeR1.startAnimation(errorAnim);
                errorIcon.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(() -> errorIcon.setVisibility(View.GONE), 200);}
        });

        ImageButton activeR2 = findViewById(holeIdArrayR[idsRhythmR[2] % lengthArraySteps]);
        if (idsRhythmR[2] < lengthArraySteps) {
            activeR2.setImageResource(R.drawable.kickhole);
        }

        if (idsRhythmR[2] == lengthArraySteps - 1) {
            PdBase.sendFloat("gainKick", gainKick);
            PdBase.sendBang("bangKick");
        }

        activeR2.setOnClickListener(view -> {
            //firstR2 = true;
            if (idsRhythmR[2] == lengthArraySteps - 1) {
                if (firstR2) {
                    //PdBase.sendFloat("gainKick", gainKick);
                    //PdBase.sendBang("bangKick");
                    scoreTxt.setText(String.valueOf(Integer.parseInt(scoreTxt.getText().toString()) + 1));
                    final String currentTime = new SimpleDateFormat("HH:mm:ss.SSS",
                            Locale.getDefault()).format(new Date());
                    dataString.append("Bang Kick, ").append(gainKick).append(", ").append(currentTime).append("\n");
                    firstR2 = false;
                }
            } else {
                activeR2.startAnimation(errorAnim);
                errorIcon.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(() -> errorIcon.setVisibility(View.GONE), 200);}
        });

        /*
        final Runnable runnableR0 = () -> {
            activeR0.setImageResource(R.drawable.hole_mole);
            //activeR0.setEnabled(false);
            idsRhythmR[0]++;
            idsRhythmR[0] = idsRhythmR[0] % (lengthArraySteps * 2);
            //Log.e("idsRhythmR0", String.valueOf(idsRhythmR[0]));
        };*/
        //handlerR0.postDelayed(runnableR0, periodMeasure8);

        /*
        final Runnable runnableR1 = () -> {
            activeR1.setImageResource(R.drawable.hole_mole);
            //activeR1.setEnabled(false);
            idsRhythmR[1]++;
            idsRhythmR[1] = idsRhythmR[1] % (lengthArraySteps * 2);
            //Log.e("idsRhythmR1", String.valueOf(idsRhythmR[1]));
        };*/
        //handlerR1.postDelayed(runnableR1, periodMeasure8);

        /*
        final Runnable runnableR2 = () -> {
            activeR2.setImageResource(R.drawable.hole_mole);
            //activeR2.setEnabled(false);
            idsRhythmR[2]++;
            idsRhythmR[2] = idsRhythmR[2] % (lengthArraySteps * 2);
            //Log.e("idsRhythmR2", String.valueOf(idsRhythmR[2]));
        };*/
        //handlerR2.postDelayed(runnableR2, periodMeasure8);

        class mJob implements Runnable{
            public void run(){
                while(System.currentTimeMillis() < periodMeasure8 - 50){ //poll for your time
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                activeL0.setImageResource(R.drawable.hole_mole);
                moleIsActive = false;
                idsRhythmL[0]++;
                idsRhythmL[0] = idsRhythmL[0] % (lengthArraySteps * 2);

                activeL1.setImageResource(R.drawable.hole_mole);
                idsRhythmL[1]++;
                idsRhythmL[1] = idsRhythmL[1] % (lengthArraySteps * 2);

                activeL2.setImageResource(R.drawable.hole_mole);
                idsRhythmL[2]++;
                idsRhythmL[2] = idsRhythmL[2] % (lengthArraySteps * 2);

                activeC0.setImageResource(R.drawable.hole_mole);
                idsRhythmC[0]++;
                idsRhythmC[0] = idsRhythmC[0] % (lengthArraySteps * 2);

                activeC1.setImageResource(R.drawable.hole_mole);
                idsRhythmC[1]++;
                idsRhythmC[1] = idsRhythmC[1] % (lengthArraySteps * 2);

                activeR0.setImageResource(R.drawable.hole_mole);
                idsRhythmR[0]++;
                idsRhythmR[0] = idsRhythmR[0] % (lengthArraySteps * 2);

                activeR1.setImageResource(R.drawable.hole_mole);
                idsRhythmR[1]++;
                idsRhythmR[1] = idsRhythmR[1] % (lengthArraySteps * 2);

                activeR2.setImageResource(R.drawable.hole_mole);
                idsRhythmR[2]++;
                idsRhythmR[2] = idsRhythmR[2] % (lengthArraySteps * 2);
            }
        }

        handlerL0.postDelayed(new mJob(),periodMeasure8);
    }

    // Start Result Activity when game is finished
    private void startResultActivity(){
        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
        intent.putExtra("FINAL_SCORE_DRUM_VALUE_EXTRA", scoreTxt.getText().toString());
        intent.putExtra("FINAL_SCORE_PITCH_VALUE_EXTRA", FINAL_SCORE_PITCH_VALUE_EXTRA);
        GameActivity.this.startActivity(intent);
    }

    private void startPitchActivity(){
        Intent intent = new Intent(GameActivity.this, PitchActivity2.class);
        intent.putExtra("FINAL_SCORE_DRUM_VALUE_EXTRA", scoreTxt.getText().toString());
        intent.putExtra("FINAL_SCORE_PITCH_VALUE_EXTRA", FINAL_SCORE_PITCH_VALUE_EXTRA);
        intent.putExtra("trials", trialsCounter);
        EventBus.getDefault().postSticky(new PitchActivity2.SaveStringBuilderEvent(dataString));
        GameActivity.this.startActivity(intent);
    }


    public class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                pauseGame();
            }
        }

    }

    /*
    public float equalLoudness(float freq, float phon) {
        float[][] iso226Table = new float[][]{{20f, 0.532f, -31.6f, 78.5f},
                {25f, 0.506f, -27.2f, 68.7f}, {31.5f, 0.480f, -23.0f, 59.5f},
                {40f, 0.455f, -19.1f, 51.1f}, {50f, 0.432f, -15.9f, 44.0f},
                {63f, 0.409f, -13.0f, 37.5f}, {80f, 0.387f, -10.3f, 31.5f},
                {100f, 0.367f, -8.1f, 26.5f}, {125f, 0.349f, -6.2f, 22.1f},
                {160f, 0.330f, -4.5f, 17.9f}, {200f, 0.315f, -3.1f, 14.4f},
                {250f, 0.301f, -2.0f, 11.4f}, {315f, 0.288f, -1.1f, 8.6f},
                {400f, 0.276f, -0.4f, 6.2f}, {500f, 0.267f, 0.0f, 4.4f},
                {630f, 0.259f, 0.3f, 3.0f}, {800f, 0.253f, 0.5f, 2.2f},
                {1000f, 0.250f, 0.0f, 2.4f}, {1250f, 0.246f, -2.7f, 3.5f},
                {1600f, 0.244f, -4.1f, 1.7f}, {2000f, 0.243f, -1.0f, -1.3f},
                {2500f, 0.243f, 1.7f, -4.2f}, {3150f, 0.243f, 2.5f, -6.0f},
                {4000f, 0.242f, 1.2f, -5.4f}, {5000f, 0.242f, -2.1f, -1.5f},
                {6300f, 0.245f, -7.1f, 6.0f}, {8000f, 0.254f, -11.2f, 12.6f},
                {10000f, 0.271f, -10.7f, 13.9f}, {12500f, 0.301f, -3.1f, 12.3f}};
        float distance = Math.abs(iso226Table[0][0] - freq);
        int freqIndex = 0;
        for(int c = 0; c < 29; c++){
            float cdistance = Math.abs(iso226Table[c][0] - freq);
            if(cdistance < distance){
                freqIndex = c;
                distance = cdistance;
            }
        }
        float freqApprox = iso226Table[freqIndex][0];
        float t_f = iso226Table[freqIndex][3];
        float l_u = iso226Table[freqIndex][2];
        float alpha_f = iso226Table[freqIndex][1];
        float af = 4.47f*(float)pow(10,-3)*((float)(pow(10,0.025*phon))-1.14f)+
                (float)(pow(0.4*pow(10,(t_f+l_u)/10-9),alpha_f));
        return(10/alpha_f*(float)log10(af)-l_u+94+15);
    }*/

}