package example.patch.diff.app.appdiffgenerator;

import example.patch.diff.app.hdiffpatch.HDiffPatchUtils;

/**
 * @author : hongshen
 * @Date: 2018/8/29 0029
 */
public class SampleHDiffPatchGenerate {

    public static void main(String[] args) {
        String oldApkPath = "C:\\Users\\Administrator\\Desktop\\增量更新\\apk\\testmyself\\old.apk";
        String newApkPath = "C:\\Users\\Administrator\\Desktop\\增量更新\\apk\\testmyself\\new.apk";
        String diffPath = "C:\\Users\\Administrator\\Desktop\\增量更新\\apk\\testmyself\\patch.diff";
        new HDiffPatchUtils().hdiff(oldApkPath, newApkPath, diffPath);
    }
}
