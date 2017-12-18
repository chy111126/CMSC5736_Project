package cuhk.cse.cmsc5736project.utils;

import android.graphics.Color;

/**
 * Created by alexchung on 16/12/2017.
 */

public class Utility {
    static private Utility instance;

    public static Utility getInstance() {
        if(instance == null) {
            instance = new Utility();
        }
        return instance;
    }

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int getLighterColor(int color) {
        // Lighter color = 10% of given color hex
        return Color.argb(10,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    public static int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }

}
