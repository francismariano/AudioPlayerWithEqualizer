#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jint JNICALL Java_me_francis_audioplayerwithequalizer_AudioEqualizer_applyEqualization(
        JNIEnv *env,
        jobject thisObj,
        jshortArray audioData,
        jintArray gains
) {

    jshort* audioDataPtr = env->GetShortArrayElements(audioData, 0);
    jint* gainsPtr = env->GetIntArrayElements(gains, 0);

    int numSample = env->GetArrayLength(audioData);
    int numBands = env->GetArrayLength(gains);
    double gainsSum = 0.0;

    for (int i = 0; i < numBands; i++) {
        gainsSum += gainsPtr[i] / 1000.0;
    }

    double averageGain = gainsSum / numBands;

    for (int i = 0; i < numSample; i++) {
        audioDataPtr[i] = static_cast<jshort>(audioDataPtr[i] * averageGain);
    }

    env->ReleaseShortArrayElements(audioData, audioDataPtr, 0);
    env->ReleaseIntArrayElements(gains, gainsPtr, 0);

    return numSample;
}
