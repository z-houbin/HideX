package z.houbin.xposed.hide;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.sohu.sohuvideo")) {
            return;
        }
        String hookUrl = "http://agn.aty.sohu.cn";
        //jb.c.class
        Class<?> jbcClass = XposedHelpers.findClass("jb.c", lpparam.classLoader);
        XposedHelpers.setStaticObjectField(jbcClass, "D", hookUrl);

        Class<?> constClass = XposedHelpers.findClass("com.sohu.app.ads.sdk.res.Const", lpparam.classLoader);
        XposedHelpers.setStaticObjectField(constClass, "AD_LIVE_URL", hookUrl);
    }

    public static <T> T getHookView(XC_MethodHook.MethodHookParam param, String name) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = param.thisObject.getClass();
        // 通过反射获取控件，无论parivate或者public
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(param.thisObject);
    }
}
