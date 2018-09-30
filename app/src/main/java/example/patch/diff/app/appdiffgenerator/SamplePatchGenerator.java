// Copyright 2016 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package example.patch.diff.app.appdiffgenerator;

import android.util.TimingLogger;

import com.google.archivepatcher.generator.FileByFileV1DeltaGenerator;
import com.google.archivepatcher.shared.DefaultDeflateCompatibilityWindow;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/** Generate a patch; args are old file path, new file path, and patch file path. */
public class SamplePatchGenerator {
  protected static final int LEVEL = 9;
  protected static final boolean NO_WRAP = true;
  protected static final int BUFFER_SIZE = 32768;

  public static final String oldPath = "C:\\Users\\Administrator\\Desktop\\增量更新\\apk\\testmyself\\old.apk";
  public static final String newPath = "C:\\Users\\Administrator\\Desktop\\增量更新\\apk\\testmyself\\new.apk";
  public static final String patchPath = "C:\\Users\\Administrator\\Desktop\\增量更新\\apk\\testmyself\\patch.diff";

  public static void main(String... args){
    new Thread(new Runnable() {
      @Override
      public void run() {
        new SamplePatchGenerator().start();
      }
    }).start();
  }

  private void start() {
    long startTime = System.currentTimeMillis();
    System.out.println("start time :" + startTime);
    if (!new DefaultDeflateCompatibilityWindow().isCompatible()) {
      System.err.println("zlib not compatible on this system");
      System.exit(-1);
    }

    File oldFile = new File(oldPath); // must be a zip archive
    File newFile = new File(newPath); // must be a zip archive
    File patchFile = new File(patchPath);
//    File patchFile = new File("C:\\Users\\Administrator\\Desktop\\增量更新\\apk\\testcorepatch/archive-patch-out-to-stream.diff");
//    File oldFile = new File("C:\\Users\\Administrator\\Desktop\\增量更新\\apk\\testcorepatch/2.2.4.apk");
//    File newFile = new File("C:\\Users\\Administrator\\Desktop\\增量更新\\apk\\testcorepatch/2.3.0.apk");
    try {
      OutputStream patchOutputStream = new FileOutputStream(patchFile);
      Deflater compressor = null;
      DeflaterOutputStream compressedPatchOutputStream = null;
      if (patchOutputStream instanceof DeflaterOutputStream) {
        compressedPatchOutputStream = (DeflaterOutputStream) patchOutputStream;
      } else {
        compressor = new Deflater(LEVEL, NO_WRAP); // to compress the patch
        compressedPatchOutputStream =
                new DeflaterOutputStream(patchOutputStream, compressor, BUFFER_SIZE);
      }
      try {
        long startGeneratorTime = System.currentTimeMillis();
        System.out.println("start startGeneratorTime :" + startGeneratorTime);
        new FileByFileV1DeltaGenerator().generateDelta(oldFile, newFile, compressedPatchOutputStream);
        long endTime = System.currentTimeMillis();
        System.out.println("end time :" + endTime);
        System.out.println(" waste time :" + (endTime - startGeneratorTime));
        compressedPatchOutputStream.finish();
        compressedPatchOutputStream.flush();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        if (compressor != null) {
          compressor.end();
        }
        try {
          compressedPatchOutputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}

