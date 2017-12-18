package cuhk.cse.cmsc5736project;

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

    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
