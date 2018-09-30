package example.patch.diff.app.hdiffpatch;

/**
 * @author : hongshen
 * @Date: 2018/8/28 0028
 */
public class HDiffPatchUtils {
    static{
        System.loadLibrary("hdiffpatch");
    }

    public native int hdiff(String oldFilePath, String newFilePath, String diffFilePath);

    public native int hpatch(String oldFilePath, String diffFilePath, String newFilePath);
}
