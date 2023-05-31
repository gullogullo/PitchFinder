package com.example.pitchfinder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends Activity implements View.OnClickListener {

    private TextView currentScore, recordScore, newRecord;

    private Button playAgain, returnMainMenu;

    private int currentScoreInt = 0;
    //private int currentScorePitchInt = 0;

    public static final String SHARED_PREFS = "SHARED_PREFS";
    public static final String BEST_GAME_RECORD = "BEST_GAME_RECORD";

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.playAgainBtn) {
            Intent intent = new Intent(ResultActivity.this, PitchActivity2.class);
            startActivity(intent);
            //ResultActivity.this.startActivity(new Intent(ResultActivity.this, PitchActivity.class));
        }
        else if (id == R.id.goToMenuBtn) {
            ResultActivity.this.startActivity(new Intent(ResultActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        ResultActivity.this.startActivity(new Intent(ResultActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Disable landscape mode
        ResultActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initUiValues();

        getValuesFromPitchActivity();

        checkScore();
        setBestRecordValue();

        playAgain.setOnClickListener(this);
        returnMainMenu.setOnClickListener(this);
    }

    // INitialization of UI elements
    private void initUiValues(){
        currentScore = findViewById(R.id.currentGameRecordTxt);
        recordScore = findViewById(R.id.bestGameRecordTxt);
        newRecord = findViewById(R.id.newRecordTxt);
        playAgain = findViewById(R.id.playAgainBtn);
        returnMainMenu = findViewById(R.id.goToMenuBtn);
    }

    // Get game points from Game Activity
    @SuppressLint("SetTextI18n")
    private void getValuesFromPitchActivity(){
        Bundle bundle = getIntent().getExtras();
        currentScoreInt = Integer.parseInt(bundle.getString("FINAL_SCORE_VALUE_EXTRA"));
        currentScore.setText(currentScore.getText().toString() + currentScoreInt);
    }

    // Check if current score is the best record
    @SuppressLint("SetTextI18n")
    private void checkScore(){

        System.out.println("curren: " + currentScoreInt);
        System.out.println("prefs: " + loadSharedPreferences());

        if (loadSharedPreferences().equals("")) {
            newRecord.setVisibility(View.VISIBLE);
            saveSharedPreferences(String.valueOf(currentScoreInt));
            return;
        }

        if (currentScoreInt <= Integer.parseInt(loadSharedPreferences()))
            return;

        newRecord.setVisibility(View.VISIBLE);
        recordScore.setVisibility(View.VISIBLE);
        recordScore.setText(recordScore.getText().toString() + currentScoreInt);
        saveSharedPreferences(String.valueOf(currentScoreInt));

    }

    // Set best record value
    @SuppressLint("SetTextI18n")
    private void setBestRecordValue(){
        String bestRecord = loadSharedPreferences();

        if (loadSharedPreferences().equals("")) {
            recordScore.setVisibility(View.VISIBLE);
            recordScore.setText(recordScore.getText().toString() + "0");
            return;
        }
        recordScore.setVisibility(View.VISIBLE);
        recordScore.setText("Best: " + bestRecord);
    }

    // Save best record
    private void saveSharedPreferences(String bestRecord){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(BEST_GAME_RECORD, bestRecord);

        editor.apply();
    }

    // Load best game record from prefs
    private String loadSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(BEST_GAME_RECORD, "");
    }

}