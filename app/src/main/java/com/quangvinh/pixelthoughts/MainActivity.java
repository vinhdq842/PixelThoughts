package com.quangvinh.pixelthoughts;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * @author ServantOfEvil
 */

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    static final int WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    static final int HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;

    static final String[] messages = {"Put a stressful thought in the star", "Relax and watch you thought", "Take a deep breath in....", "....and breathe out", "Everything is okay", "Your life is okay", "Life is much grander than this thought", "The universe is over 93 billion light-years in distance", "Our galaxy is small", "Our sun is tiny", "The earth is minuscule", "Our cities are insignificant....", "....and you are microscopic", "This thought.... does not matter", "It can easily disappear", "and life will go on...."};

    private int count = 0;
    static int index = 1;

    private TextView textViewMessage;

    private CountDownTimer countDownTimerRunning;
    private CountDownTimer countDownTimerStop;

    private float alpha = 1.0f;
    private float a = -0.2f;

    private LinearLayout linearLayoutAppLabel;
    private RelativeLayout relativeLayoutMainScreen;

    private boolean finished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mediaPlayer = MediaPlayer.create(this, R.raw.background);

        setContentView(R.layout.activity_main);

        relativeLayoutMainScreen = findViewById(R.id.mainScreen);
        linearLayoutAppLabel = findViewById(R.id.label);

        final LinearLayout linearLayoutInputForm = findViewById(R.id.inputForm);
        final Background background = new Background(this);
        textViewMessage = findViewById(R.id.textViewContentMessage);
        textViewMessage.setTypeface(Background.typeface);
        textViewMessage.setText(messages[0]);

        relativeLayoutMainScreen.removeAllViews();
        relativeLayoutMainScreen.addView(background);
        relativeLayoutMainScreen.addView(linearLayoutAppLabel);
        relativeLayoutMainScreen.addView(linearLayoutInputForm);
        relativeLayoutMainScreen.addView(textViewMessage);

        linearLayoutInputForm.setAlpha(0);
        linearLayoutInputForm.getChildAt(0).setEnabled(false);

        textViewMessage.setAlpha(0);
        Background.setMainStarAlpha(0);


        CountDownTimer countDownTimerStart = new CountDownTimer(6000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                count++;
                if (count > 40 && count <= 45) linearLayoutAppLabel.setAlpha(alpha += a);
                if (count == 45) a *= -1;
                if (count >= 45) {
                    linearLayoutInputForm.setAlpha(alpha += a);
                    textViewMessage.setAlpha(alpha);
                    Background.setMainStarAlpha(alpha * 255 > 255 ? 255 : alpha * 255);
                }
            }

            @Override
            public void onFinish() {
                linearLayoutInputForm.getChildAt(0).setEnabled(true);
                linearLayoutAppLabel.setAlpha(0);
                ((TextView) linearLayoutAppLabel.getChildAt(0)).setText(R.string.farewell);
                ((TextView) linearLayoutAppLabel.getChildAt(0)).setTextSize(25);
                ((TextView) linearLayoutAppLabel.getChildAt(1)).setText(R.string.my_word);
                Background.setMainStarAlpha(255);

                findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.start();
                        background.setMessage(((TextView) findViewById(R.id.inputText)).getText().toString());
                        relativeLayoutMainScreen.removeView(linearLayoutInputForm);
                        Background.startAnimation();

                        alpha = 1.0f;
                        a = -0.2f;
                        count = 25;

                        countDownTimerRunning = new CountDownTimer(100000, 100) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                count++;
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
            e.printStackTrace();
        }
    }

}

class Background extends View {

    private Paint paint = new Paint();

    private String message = "";

    private Random random = new Random();
    static Vector<Star> stars = new Vector<>();

    static final int NUM_STARS = 130;

    private static float radius = MainActivity.HEIGHT / 6f;
    static float down = radius / 93f;
    static Timer timer = new Timer();

    static int alpha = 255;

    static Typeface typeface;

    public Background(Context context) {
        super(context);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/tnr.ttf");
        paint.setTypeface(typeface);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

    }

    public static void setMainStarAlpha(float alp) {
        alpha = (int) alp;
    }

    public static void startAnimation() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (radius > 6) radius -= down;
                else {
                    stars.addElement(new Star(MainActivity.WIDTH / 2f, MainActivity.HEIGHT / 2f, radius));
                    radius--;
                    this.cancel();
                }
            }
        }, 1000, 500);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);
        if (radius > 5)
            drawTheMainStar(MainActivity.WIDTH / 2f, MainActivity.HEIGHT / 2f, radius, canvas, paint);
        paint.setColor(Color.WHITE);
        addStars();
        for (int i = 0; i < stars.size(); i++) (stars.elementAt(i)).render(canvas, paint);
        invalidate();
    }

    private void drawTheMainStar(float x, float y, float r, Canvas canvas, Paint paint) {
        paint.setARGB(alpha, 100, 50, 0);
        canvas.drawArc(x - r, y - r, x + r, y + r, 0, 360, true, paint);

        paint.setARGB(alpha, 221, 221, 221);
        canvas.drawArc(x - r + r / 10, y - r + r / 10, x + r - r / 10, y + r - r / 10, 0, 360, true, paint);

        paint.setARGB(alpha, 204, 204, 204);
        canvas.drawArc(x - r + r / 5, y - r + r / 5, x + r - r / 5, y + r - r / 5, 0, 360, true, paint);

        paint.setARGB(alpha, 221, 221, 221);
        canvas.drawArc(x - r + r / 2, y - r + r / 4, x + r - r / 5, y + r - r / 4, 0, 360, true, paint);

        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(r / 7);
        canvas.drawText(message, x, y, paint);
    }

    private void addStars() {
        while (stars.size() < NUM_STARS)
            stars.addElement(new Star(rand(0, MainActivity.WIDTH), rand(MainActivity.HEIGHT, 2 * MainActivity.HEIGHT), rand(2, 5)));
    }


    private int rand(int from, int to) {
        return from + Math.abs(random.nextInt() % (to - from));
    }


    private static class Star {
        private float x, v, y, size;

        Star(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.v = size / 2;
            this.size = size;
        }


        private void move() {
            y -= v;
        }

        void render(android.graphics.Canvas canvas, Paint paint) {
            move();
            canvas.drawRect(x, y, x + size, y + size, paint);
            if (y < 0) Background.stars.removeElement(this);
        }

    }

}