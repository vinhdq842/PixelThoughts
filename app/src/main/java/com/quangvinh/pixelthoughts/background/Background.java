package com.quangvinh.pixelthoughts.background;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.View;

import com.quangvinh.pixelthoughts.R;
import com.quangvinh.pixelthoughts.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Background extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String message = "";
    static List<Star> stars = new ArrayList<>();
    static final int NUM_STARS = 130;
    static Timer timer = new Timer();
    static int alpha = 255;
    public Typeface typeface;
    private final Bitmap background;
    private final Rect rect;
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
    private final int width;
    private final int height;
    private float radius;
    private final float downUnit;

    public Background(Context context) {
        super(context);
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        radius = height / 6f;
        downUnit = radius / 96.5f;

        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        rect = new Rect(0, 0, width, height);

        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ComingSoon.ttf");
        paint.setTypeface(typeface);
        shadowPaint.setTypeface(typeface);
        shadowPaint.setShadowLayer(30, 0, 0, 0xffff6347);
        setLayerType(1, shadowPaint);
    }

    public void setMainStarAlpha(float alp) {
        alpha = (int) alp;
    }

    public void startAnimation() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (radius > 6) radius -= downUnit;
                else {
                    stars.add(new Star(width / 2f, height / 2f, radius, 5));
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
        canvas.drawBitmap(background, null, rect, paint);
        paint.setColor(Color.WHITE);
        addStars();
        for (int i = 0; i < stars.size(); i++) {
            Star star = stars.get(i);
            star.render(canvas, paint);
            if (star.getY() < 0) stars.remove(star);
        }
        if (radius > 5)
            drawTheMainStar(width / 2f, height / 2f, radius, canvas, shadowPaint);
        handler.postDelayed(runnable, 30);
    }

    private void drawTheMainStar(float x, float y, float r, Canvas canvas, Paint paint) {
        paint.setARGB(alpha, 221, 221, 221);
        canvas.drawArc(x - r, y - r, x + r, y + r, 0, 360, true, paint);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(r / 7);
        canvas.drawText(message, x, y, paint);
    }

    private void addStars() {
        while (stars.size() < NUM_STARS)
            stars.add(new Star(RandomUtils.rand(0, width), RandomUtils.rand(height, 2 * height), RandomUtils.rand(2, 5)));
    }

}
