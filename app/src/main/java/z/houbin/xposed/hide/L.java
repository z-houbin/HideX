package z.houbin.xposed.hide;

import android.util.Log;

import de.robv.android.xposed.XposedBridge;

public class L {
    private static final String t = "z.houbin";

    public static void i(String msg) {
        Log.d(t, msg);
        XposedBridge.log(msg);
    }

    public static void d(String msg) {
        Log.d(t, msg);
        XposedBridge.log(msg);
    }
}
