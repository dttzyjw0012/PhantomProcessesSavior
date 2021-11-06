package com.yhc.phantom.processes.savior;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Savior implements IXposedHookLoadPackage {
    private static final String TAG = "Savior:";
    private static final String CLASS_PHANTOM_PROCESS_RECORD =
            "com.android.server.am.PhantomProcessRecord";
    private static final boolean DEBUG = true;
    private static Class<?> mPhantomProcessRecordClass;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("android") &&
                lpparam.processName.equals("android")) {
            initAndroid(lpparam.classLoader);
        }

        if (DEBUG) logInner("packageName: " + lpparam.packageName);
    }

    public static void logInner(String message) {
        XposedBridge.log(TAG + ": " + message);
    }

    public static void initAndroid(final ClassLoader classLoader) {
        try {
            mPhantomProcessRecordClass = XposedHelpers.findClass(CLASS_PHANTOM_PROCESS_RECORD,
                    classLoader);

            XposedHelpers.findAndHookMethod(mPhantomProcessRecordClass, "killLocked",
                    String.class, "boolean",
                    phantomProcessRecordKillLockedHook);
        } catch (Throwable t) {
            log(TAG, t);
        }
    }

    private static XC_MethodHook phantomProcessRecordKillLockedHook =
            new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam)
                        throws Throwable {
                    if (DEBUG) logInner("phantomProcessRecordListKillLockedHook start initialized");
                    try {
                        if (DEBUG) logInner("phantomProcessRecordListKillLockedHook Hooked!");
                        return null;
                    } catch (Throwable t) {
                        if (DEBUG) logInner("phantomProcessRecordListKillLockedHook initialized");
                        return null;
                    }
                }
            };

    public static void log(String tag, Throwable t) {
        log(tag, null, t);
    }

    public static void log(String tag, String message, Throwable t) {
        if (message != null) {
            XposedBridge.log(tag + ": " + message);
        }
        if (t != null) {
            XposedBridge.log(t);
        }
    }
}
