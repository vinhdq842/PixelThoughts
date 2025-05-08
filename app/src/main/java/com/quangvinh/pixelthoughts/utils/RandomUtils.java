package com.quangvinh.pixelthoughts.utils;

import java.util.Random;

public class RandomUtils {
    private static final Random random = new Random();

    public static int rand(int from, int to) {
        return from + Math.abs(random.nextInt() % (to - from));
    }
}