//
// Created by hongshen on 2018/8/3 0003.
//
#include <jni.h>
#include <string>
#include "bspatch.h"


extern "C"
JNIEXPORT void JNICALL
Java_example_patch_diff_app_appdiffpatch_utils_BsPatchUtil_patch(JNIEnv *env, jobject instance, jstring oldpath_,
                                            jstring newpath_, jstring patch_) {
    const char *oldpath = env->GetStringUTFChars(oldpath_, 0);
    const char *newpath = env->GetStringUTFChars(newpath_, 0);
    const char *patch = env->GetStringUTFChars(patch_, 0);

    char  * argv[4];
    argv[0] = "bspatch";
    argv[1] = (char *)oldpath;
    argv[2] = (char *)newpath;
    argv[3] = (char *)patch;


    bspatch_main(4,argv);

    // TODO

    env->ReleaseStringUTFChars(oldpath_, oldpath);
    env->ReleaseStringUTFChars(newpath_, newpath);
    env->ReleaseStringUTFChars(patch_, patch);
}