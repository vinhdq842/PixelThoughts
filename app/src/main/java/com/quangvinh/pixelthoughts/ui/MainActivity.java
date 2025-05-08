package com.quangvinh.pixelthoughts.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quangvinh.pixelthoughts.R;
import com.quangvinh.pixelthoughts.background.Background;

/**
 * @author ServantOfEvil
 */

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private static final String[] messages = {"Put a stressful thought in the star", "Relax and watch your thought", "Take a deep breath in....", "....and breathe out", "Everything is okay", "Your life is okay", "Life is much grander than this thought", "The universe is over 93 billion light-years in distance", "Our galaxy is small", "Our sun is tiny", "The earth is minuscule", "Our cities are insignificant....", "....and you are microscopic", "This thought.... does not matter", "It can easily disappear", "and life will go on...."};
    private int count = 0;
    private static int index = 1;
    private TextView textViewMessage;
    private CountDownTimer countDownTimerRunning;
    private CountDownTimer countDownTimerStop;
    private float alpha = 1.0f;
    private float a = -0.2f;
    private LinearLayout linearLayoutAppLabel;
    private RelativeLayout relativeLayoutMainScreen;
    private boolean finished;
    private Background background;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mediaPlayer = MediaPlayer.create(this, R.raw.background);
        background = new Background(this);

        setContentView(R.layout.activity_main);

        relativeLayoutMainScreen = findViewById(R.id.mainScreen);
        linearLayoutAppLabel = findViewById(R.id.label);

        final LinearLayout linearLayoutInputForm = findViewById(R.id.inputForm);

        textViewMessage = findViewById(R.id.textViewContentMessage);
        textViewMessage.setTypeface(background.typeface);
        textViewMessage.setText(messages[0]);


        relativeLayoutMainScreen.removeAllViews();
        relativeLayoutMainScreen.addView(background);
        relativeLayoutMainScreen.addView(linearLayoutAppLabel);
        relativeLayoutMainScreen.addView(linearLayoutInputForm);
        relativeLayoutMainScreen.addView(textViewMessage);

        linearLayoutInputForm.setAlpha(0);
        linearLayoutInputForm.getChildAt(0).setEnabled(false);
        ((TextView) linearLayoutAppLabel.getChildAt(0)).setTypeface(background.typeface);
        ((TextView) linearLayoutAppLabel.getChildAt(1)).setTypeface(background.typeface);

        textViewMessage.setAlpha(0);
        background.setMainStarAlpha(0);


        CountDownTimer countDownTimerStart = new CountDownTimer(6000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                count++;
                if (count > 40 && count <= 45) linearLayoutAppLabel.setAlpha(alpha += a);
                if (count == 45) a *= -1;
                if (count >= 45) {
                    linearLayoutInputForm.setAlpha(alpha += a);
                    textViewMessage.setAlpha(alpha);
                    background.setMainStarAlpha(alpha * 255 > 255 ? 255 : alpha * 255);
                }
            }

            @Override
            public void onFinish() {
                linearLayoutInputForm.getChildAt(0).setEnabled(true);
                linearLayoutAppLabel.setAlpha(0);
                ((TextView) linearLayoutAppLabel.getChildAt(0)).setText(R.string.farewell);
                ((TextView) linearLayoutAppLabel.getChildAt(0)).setTextSize(25);
                ((TextView) linearLayoutAppLabel.getChildAt(1)).setText(R.string.my_word);
                background.setMainStarAlpha(255);
                linearLayoutInputForm.setAlpha(1);
                textViewMessage.setAlpha(1);

                findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.start();
                        background.setMessage(((TextView) findViewById(R.id.inputText)).getText().toString());
                        background.startAnimation();
                        linearLayoutInputForm.setEnabled(false);

                        alpha = 1.0f;
                        a = -0.2f;
                        count = 6;

                        countDownTimerRunning = new CountDownTimer(100000, 100) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                count++;
                                if (linearLayoutInputForm.getAlpha() >= 0.2) {
                                    linearLayoutInputForm.setAlpha(1.0f - (count - 6) * 0.2f);
                                    if (linearLayoutInputForm.getAlpha() < 0.2)
                                        relativeLayoutMainScreen.removeView(linearLayoutInputForm);
                                }

                                if (index < messages.length && count == 30) {
                                    a *= -1;
                                    alpha = 0;
                                    textViewMessage.setText(messages[index++]);
                                    count = 0;
                                }

                                if (count <= 5 || count >= 25) {
                                    alpha += a;
                                    textViewMessage.setAlpha(alpha);
                                    if (count == 5) {
                                        a *= -1;
                                        alpha = 1.0f;
                                    }
                                    if (count == 30 && index == messages.length) {
                                        finished = true;
                                        this.onFinish();
                                        this.cancel();
                                    }
                                }
                            }

                            @Override
                            public void onFinish() {
                                if (!finished) this.start();
                                else {
                                    count = 0;
                                    a = 0.2f;
                                    alpha = 0.0f;
                                    relativeLayoutMainScreen.removeView(textViewMessage);

                                    countDownTimerStop = new CountDownTimer(1000, 100) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            if (count < 5) {
                                                linearLayoutAppLabel.setAlpha(alpha += a);
                                            }
                                            count++;
                                        }

                                        @Override
                                        public void onFinish() {

                                        }
                                    };

                                    countDownTimerStop.start();
                                }
                            }
                        };

                        countDownTimerRunning.start();
                    }
                });
            }
        };

        countDownTimerStart.start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }

}

