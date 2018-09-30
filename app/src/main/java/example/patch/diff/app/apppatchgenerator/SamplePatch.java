package example.patch.diff.app.apppatchgenerator;

import android.os.Environment;

import com.google.archivepatcher.applier.FileByFileV1DeltaApplier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import example.patch.diff.app.appdiffpatch.utils.BsPatchUtil;

/**
 * patch 测试
 */
public class SamplePatch {

    private static final String oldApkPath = "C:\\Users\\Administrator\\Desktop\\增量更新\\apk\\testmyself\\patch\\old.apk";
    private static final String newApkPath = "C:\\Users\\Administrator\\Desktop\\增量更新\\apk\\testmyself\\patch\\new.apk";
    private static final String patchPath = "C:\\Users\\Administrator\\Desktop\\增量更新\\apk\\testmyself\\patch\\patch.diff";

    public static void main(String[] args) {
        new SamplePatch().startArchivePatch();
    }

    private void startArchivePatch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                System.out.println("start time :" + startTime);
                File oldFile = new File(oldApkPath);

                try {
                    Inflater uncompressor = new Inflater(true);
                    FileInputStream compressedPatchIn = new FileInputStream(new File(patchPath));
                    InflaterInputStream patchIn =
                            new InflaterInputStream(compressedPatchIn, uncompressor, 32768);

                    final File newFile = new File(newApkPath);

                    long startGeneratorTime = System.currentTimeMillis();
                    System.out.println("start startGeneratorTime :" + startGeneratorTime);
                    new FileByFileV1DeltaApplier().applyDelta(oldFile, patchIn, new FileOutputStream(newFile));
                    long endTime = System.currentTimeMillis();
                    System.out.println("end time :" + endTime);
                    System.out.println(" waste time :" + (endTime - startGeneratorTime));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
