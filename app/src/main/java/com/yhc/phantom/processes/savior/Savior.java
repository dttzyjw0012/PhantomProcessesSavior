package com.yhc.phantom.processes.savior;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Savior implements IXposedHookLoadPackage {
    private static final String TAG = "Savior:";
    private static final String CLASS_PHANTOM_PROCESS_LIST =
            "com.android.server.am.PhantomProcessList";
    private static final String CLASS_PROCESS_CPU_TRACKER =
            "com.android.internal.os.ProcessCpuTracker";
    
    private static final boolean DEBUG = false;
    private static Class<?> mPhantomProcessListClass;

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
            mPhantomProcessListClass = XposedHelpers.findClass(CLASS_PHANTOM_PROCESS_LIST,
                    classLoader);

            XposedHelpers.findAndHookMethod(mPhantomProcessListClass,
                    "updateProcessCpuStatesLocked",
                    CLASS_PROCESS_CPU_TRACKER,
                    phantomProcessListClassUpdateProcessCpuStatesLockedHook);
            XposedHelpers.findAndHookMethod(mPhantomProcessListClass,
                    "trimPhantomProcessesIfNecessary",
                    phantomProcessListClassTrimPhantomProcessesIfNecessaryHook);
        } catch (Throwable t) {
            log(TAG, t);
        }
    }

    private static XC_MethodHook phantomProcessListClassUpdateProcessCpuStatesLockedHook =
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(final MethodHookParam param) {
                    if (DEBUG)
                        logInner(
                                "phantomProcessListClassUpdateProcessCpuStatesLockedHook start " +
                                        "initialized"
                        );

                    param.setResult(null);

                    if (DEBUG)
                        logInner(
                                "phantomProcessListClassUpdateProcessCpuStatesLockedHook " +
                                        "Hooked!"
                        );
                }
            };

    private static XC_MethodHook phantomProcessListClassTrimPhantomProcessesIfNecessaryHook =
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(final MethodHookParam param) {
                    if (DEBUG)
                        logInner(
                                "phantomProcessListClassTrimPhantomProcessesIfNecessaryHook" +
                                        " start initialized"
                        );

                    param.setResult(null);

                    if (DEBUG)
                        logInner(
                                "phantomProcessListClassTrimPhantomProcessesIfNecessaryHook " +
                                        "initialized"
                        );
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
