package z.houbin.xposed.hide;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    private static final String XPOSED_INSTALLER = "de.robv.android.xposed.installer";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        L.i("handleLoadPackage " + lpparam);
        ApplicationInfo appInfo = lpparam.appInfo;
        if (appInfo == null) {
            return;
        }

        // No system app
        // No updated system app
        if ((appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }

        // No Xposed self
        // No app self
        if (appInfo.packageName.equals(XPOSED_INSTALLER) ||
                appInfo.packageName.equals("z.houbin.xposed.hide")) {
            return;
        }

        if ((appInfo.packageName.equals("com.qiyi.video")) &&
                (lpparam.isFirstApplication)) {
            L.i("Hook com.qiyi.video start!");
            L.i("The package = " + lpparam.packageName + " has hook");
            L.i("The app target id = " + android.os.Process.myPid());
            Bundle bundle = appInfo.metaData;
            L.i("app metadata " + bundle);

            Class clazz = lpparam.classLoader.loadClass("android.support.v4.app.FragmentActivity");

            XposedHelpers.findAndHookMethod(clazz, "findViewById", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Context context = AndroidAppHelper.currentApplication().getApplicationContext();
                    int navi4 = context.getResources().getIdentifier("com.qiyi.video:id/navi4", "id", context.getPackageName());
                    L.i("findViewById current id " + param.args[0]);
                    L.i("findViewById navi4 id " + navi4);
                    if (Integer.parseInt(param.args[0].toString()) == navi4) {
                        View view = (View) param.getResult();
                        view.setVisibility(View.INVISIBLE);
                    }
                }
            });

            XposedHelpers.findAndHookMethod(ViewGroup.class, "addView", View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    L.i("addView beforeHookedMethod addview " + param.args[0]);
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    View v = (View) param.args[0];
                    int navi4 = v.getResources().getIdentifier("com.qiyi.video:id/navi4", "id", v.getContext().getPackageName());
                    L.i("addView current id " + v.getId());
                    if (v.getId() == navi4) {
                        L.i("addView navi4 id " + navi4);
                        ViewGroup parent = (ViewGroup) v.getParent();
                        parent.removeView(parent);
                    }
                }
            });
        }
    }

    public static <T> T getHookView(XC_MethodHook.MethodHookParam param, String name) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = param.thisObject.getClass();
        // 通过反射获取控件，无论parivate或者public
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(param.thisObject);
    }
}
