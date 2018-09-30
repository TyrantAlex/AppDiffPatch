package example.patch.diff.app.appdiffpatch.utils;

/**
 * @author : hongshen
 * @Date: 2018/8/3 0003
 */
public class BsPatchUtil {
    static{
        System.loadLibrary("bspatch_util");
    }

    public static native int patch(String oldApkPath, String newApkPath, String patchPatch);
}
