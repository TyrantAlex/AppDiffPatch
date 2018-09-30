package example.patch.diff.app.appdiffpatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimingLogger;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.archivepatcher.applier.FileByFileV1DeltaApplier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import example.patch.diff.app.appdiffpatch.utils.BsPatchUtil;
import example.patch.diff.app.hdiffpatch.HDiffPatchUtils;

public class MainActivity extends Activity {

    private static final String patchPath = Environment.getExternalStorageDirectory() + "/patch.patch";
    private static final String newApkPath = Environment.getExternalStorageDirectory() + "/newApk.apk";

    File newApkFile;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MainActivity.this, "patch完毕准备开始安装...", 1).show();
//            installAPK(MainActivity.this, newApkFile);
        }
    };

    // Used to load the 'native-lib' library on application startup.
//    static {
////        System.loadLibrary("native-lib");
////    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        TextView tvAppId = findViewById(R.id.tv_app_id);
//        tv.setText(stringFromJNI());

//        tvAppId.setText("This is Old App");
//        Toast.makeText(MainActivity.this, "This is old App", 1).show();

        Toast.makeText(MainActivity.this, "This is new App", 1).show();
        tvAppId.setText("This is a new App");

        TextView tvStart = findViewById(R.id.tv_start_match);
        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBsPatch();
            }
        });

        TextView tvArchivePatch = findViewById(R.id.tv_start_archive_match);
        tvArchivePatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startArchivePatch();
            }
        });

        TextView tv_hdiifpatch = findViewById(R.id.tv_hdiifpatch);
        tv_hdiifpatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHDiffPatch();
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();


    public String getCurrentApkInstallPath() {
        String sourceDir = getApplicationContext().getApplicationInfo().sourceDir;
        return sourceDir;
    }

    private void startBsPatch() {
//        newApkFile = new File(Environment.getExternalStorageDirectory(), "newApk.apk");
        final String patchPath = Environment.getExternalStorageDirectory() + "/bspatch.diff";
        final String newApkPath = Environment.getExternalStorageDirectory() + "/newApk.apk";
        final String oldApkPath = Environment.getExternalStorageDirectory() + "/old.apk";
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startGeneratorTime = System.currentTimeMillis();
                Log.d("sqs", "start startGeneratorTime :" + startGeneratorTime);

//                BsPatchUtil.patch(getCurrentApkInstallPath(), newApkPath, patchPath);
                BsPatchUtil.patch(oldApkPath, newApkPath, patchPath);

                long endTime = System.currentTimeMillis();
                Log.d("sqs", "end time :" + endTime);
                Log.d("sqs", " waste time :" + (endTime - startGeneratorTime));
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    private void startArchivePatch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File oldFile = new File(getCurrentApkInstallPath());

                try {
                    Inflater uncompressor = new Inflater(true);
                    FileInputStream compressedPatchIn = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "patch.diff"));
                    InflaterInputStream patchIn =
                            new InflaterInputStream(compressedPatchIn, uncompressor, 32768);

                    final File newFile = new File(newApkPath);
                    new FileByFileV1DeltaApplier().applyDelta(oldFile, patchIn, new FileOutputStream(newFile));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)findViewById(R.id.sample_text)).setText("Patch Success!");
                            installAPK(getApplicationContext(), newFile);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startHDiffPatch() {
        final String newApkPath = Environment.getExternalStorageDirectory() + "newApk.apk";
        final String diffPath = Environment.getExternalStorageDirectory() + "patch.diff";
        new Thread(new Runnable() {
            @Override
            public void run() {
                TimingLogger timings = new TimingLogger("SQS", "UpdateTask");
                final File newFile = new File(newApkPath);
                HDiffPatchUtils hDiffPatchUtils = new HDiffPatchUtils();
                hDiffPatchUtils.hdiff(getCurrentApkInstallPath(), newApkPath, diffPath);
                timings.addSplit("hDiffPatch.diff");
                hDiffPatchUtils.hpatch(getCurrentApkInstallPath(), diffPath, newApkPath);
                timings.addSplit("hDiffPatch.patch");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.sample_text)).setText("Patch Success!");
                        installAPK(getApplicationContext(), newFile);
                    }
                });
            }
        }).start();
    }

    public static void installAPK(Context context, File file) {
        if (!file.exists())return;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
