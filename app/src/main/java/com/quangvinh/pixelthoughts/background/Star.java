package com.quangvinh.pixelthoughts.background;

import android.graphics.Canvas;
import android.graphics.Paint;

class Star {
    private final float x;
    private final float v;
    private float y;
    private final float size;

    Star(float x, float y, float size) {
        this(x, y, size, size / 2);
    }

    Star(float x, float y, float size, float v) {
        this.x = x;
        this.setY(y);
        this.v = v;
        this.size = size;
    }

    private void move() {
        setY(getY() - getV());
    }

    void render(Canvas canvas, Paint paint) {
        move();
        canvas.drawRect(getX(), getY(), getX() + getSize(), getY() + getSize(), paint);
    }

    public float getX() {
        return x;
    }

    public float getV() {
        return v;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSize() {
        return size;
    }
}
