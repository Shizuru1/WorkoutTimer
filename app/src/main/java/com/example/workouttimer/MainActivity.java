package com.example.workouttimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ProgressBar progress;
    TextView timerText, identifier;
    Button start, stop;
    EditText workout, rest, sets;
    CountDownTimer timer;
    long timerLength;
    int amount;
    String text;
    boolean started = false, stopped = false;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Elements
        progress = findViewById(R.id.progressBar);
        timerText = findViewById(R.id.textView2);
        identifier = findViewById(R.id.textView);
        start = findViewById(R.id.button);
        stop = findViewById(R.id.button2);
        workout = findViewById(R.id.editTextTime);
        rest = findViewById(R.id.editTextTime2);
        sets = findViewById(R.id.editTextNumber);
        text = "Start";
        start.setText(text);
        text = "Stop";
        stop.setText(text);
        sharedPref = getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start timer
                start.setClickable(false);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                startForegroundService(new Intent(MainActivity.this, ForegroundService.class));
                if (!started) {
                    amount = sets.getText().toString().length() != 0 ? Integer.parseInt(sets.getText().toString()) : 3;
                    started = true;
                }
                if (!stopped) {
                    timerLength = workout.getText().toString().length() != 0 ? Long.parseLong(workout.getText().toString()) : 60;
                    progress.setMax((int) timerLength);
                    progress.setProgress(0);
                    prefEditor.putInt("max", progress.getMax());
                    prefEditor.putInt("progress", progress.getProgress());
                    prefEditor.apply();
                } else {
                    stopped = false;
                    text = "Stop";
                    stop.setText(text);
                }
                text = "Workout";
                identifier.setText(text);
                prefEditor.putString("id", text);
                prefEditor.apply();
                timer = new CountDownTimer(timerLength * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        text = millisUntilFinished / 60000 + ":" + ((millisUntilFinished % 60000) > 9999 ? (millisUntilFinished % 60000) / 1000 : ("0" + (millisUntilFinished % 60000) / 1000));
                        timerText.setText(text);
                        progress.setProgress(progress.getProgress() + 1);
                        prefEditor.putString("timer", text);
                        prefEditor.putInt("progress", progress.getProgress());
                        prefEditor.apply();
                        timerLength = millisUntilFinished / 1000;
                    }

                    @Override
                    public void onFinish() {
                        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        // rest
                        timerLength = rest.getText().toString().length() != 0 ? Long.parseLong(rest.getText().toString()) : 20;
                        progress.setProgress(0);
                        progress.setMax((int) timerLength);
                        prefEditor.putInt("max", progress.getMax());
                        prefEditor.putInt("progress", progress.getProgress());
                        prefEditor.apply();
                        text = "Rest";
                        identifier.setText(text);
                        prefEditor.putString("id", text);
                        prefEditor.apply();
                        timer = new CountDownTimer(timerLength * 1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                text = millisUntilFinished / 60000 + ":" + ((millisUntilFinished % 60000) > 9999 ? (millisUntilFinished % 60000) / 1000 : ("0" + (millisUntilFinished % 60000) / 1000));
                                timerText.setText(text);
                                progress.setProgress(progress.getProgress() + 1);
                                prefEditor.putString("timer", text);
                                prefEditor.putInt("progress", progress.getProgress());
                                prefEditor.apply();
                                timerLength = millisUntilFinished / 1000;
                            }

                            @Override
                            public void onFinish() {
                                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                                // repeat if more than one set
                                if (amount > 1) {
                                    amount -= 1;
                                    start.performClick();
                                } else {
                                    text = "Finished";
                                    identifier.setText(text);
                                    prefEditor.putString("id", text);
                                    prefEditor.apply();
                                    text = "Restart";
                                    start.setText(text);
                                    start.setClickable(true);
                                    Intent i = new Intent(MainActivity.this, ForegroundService.class);
                                    stopService(i);
                                }
                            }
                        };
                        timer.start();
                    }
                };
                timer.start();
            }
        });

         stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // stop timer
                start.setClickable(true);
                text = "Continue";
                start.setText(text);
                text = "Reset";
                stop.setText(text);
                if (!stopped) {
                    stopped = true;
                    timer.cancel();
                } else {
                    Intent reset = new Intent(MainActivity.this, MainActivity.class);
                    reset.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(reset);
                }
                Intent i = new Intent(MainActivity.this, ForegroundService.class);
                stopService(i);
            }
        });
    }
}