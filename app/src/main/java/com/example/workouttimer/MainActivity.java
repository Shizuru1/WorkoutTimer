package com.example.workouttimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Time;
import java.util.Objects;
import java.util.function.LongToIntFunction;

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

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start timer
                start.setClickable(false);
                if (!started) {
                    amount = sets.getText().toString().length() != 0 ? Integer.parseInt(sets.getText().toString()) : 3;
                    started = true;
                }
                if (!stopped) {
                    timerLength = workout.getText().toString().length() != 0 ? Long.parseLong(workout.getText().toString()) : 60;
                    progress.setMax((int) timerLength);
                    progress.setProgress(0);
                } else {
                    stopped = false;
                    text = "Stop";
                    stop.setText(text);
                }
                text = "Workout";
                identifier.setText(text);
                timer = new CountDownTimer(timerLength * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        text = millisUntilFinished / 60000 + ":" + ((millisUntilFinished % 60000) > 9999 ? (millisUntilFinished % 60000) / 1000 : ("0" + (millisUntilFinished % 60000) / 1000));
                        timerText.setText(text);
                        progress.setProgress(progress.getProgress() + 1);
                        timerLength = millisUntilFinished / 1000;
                    }

                    @Override
                    public void onFinish() {
                        // rest
                        timerLength = rest.getText().toString().length() != 0 ? Long.parseLong(rest.getText().toString()) : 20;
                        progress.setProgress(0);
                        progress.setMax((int) timerLength);
                        text = "Rest";
                        identifier.setText(text);
                        timer = new CountDownTimer(timerLength * 1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                text = millisUntilFinished / 60000 + ":" + ((millisUntilFinished % 60000) > 9999 ? (millisUntilFinished % 60000) / 1000 : ("0" + (millisUntilFinished % 60000) / 1000));
                                timerText.setText(text);
                                progress.setProgress(progress.getProgress() + 1);
                                timerLength = millisUntilFinished / 1000;
                            }

                            @Override
                            public void onFinish() {
                                // repeat if more than one set
                                if (amount > 1) {
                                    amount -= 1;
                                    start.performClick();
                                } else {
                                    text = "Finished";
                                    identifier.setText(text);
                                    text = "Restart";
                                    start.setText(text);
                                    start.setClickable(true);
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
                    timer.cancel();
                    text = "0:00";
                    timerText.setText(text);
                    text = "Finished";
                    identifier.setText(text);
                    text = "Restart";
                    start.setText(text);
                    text = "Stop";
                    stop.setText(text);
                    progress.setProgress(0);
                    start.setClickable(true);
                    started = false;
                }
            }
        });
    }
}