#include <jni.h>
#include <android/log.h>
#include <memory>

#define LOG_TAG "EqualizerJNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

class NativeEqualizer {
    public:
        NativeEqualizer() {
            LOGD("NativeEqualizer constructor");
            // Initialize your DSP components here
        }

        ~NativeEqualizer() {
            LOGD("NativeEqualizer destructor");
            // Cleanup resources
        }
//    NativeEqualizer() {
//        // Inicialize seu equalizador aqui
//        currentGain = 0.0f;
//        for (float &band: bands) {
//            band = 0.0f;
//        }
//        enabled = false;
//    }

//    void setGlobalGain(float gain) {
//        currentGain = gain;
//        // Implementação real do DSP aqui
//        LOGD("Global gain set to: %.2f", gain);
//    }
//
//    void setBandGain(int band, float gain) {
//        if (band >= 0 && band < 5) {
//            bands[band] = gain;
//            // Implementação real do DSP aqui
//            LOGD("Band %d gain set to: %.2f", band, gain);
//        }
//    }
//
//    void setEnabled(bool enable) {
//        enabled = enable;
//        LOGD("Equalizer %s", enable ? "enabled" : "disabled");
//    }
//
//    void reset() {
//        currentGain = 0.0f;
//        for (float &band: bands) {
//            band = 0.0f;
//        }
//        LOGD("Equalizer reset");
//    }

private:
    float currentGain;
    float bands[5];
    bool enabled;
};

extern "C" {

    JNIEXPORT jlong JNICALL
    Java_me_francis_equalizermodule_NativeEqualizerModule_nativeInit(JNIEnv* env, jobject thiz) {
        try {
            // Using unique_ptr for automatic memory management
            auto* equalizer = new NativeEqualizer();
            LOGD("Created new NativeEqualizer instance at %p", equalizer);
            return reinterpret_cast<jlong>(equalizer);
        } catch (const std::exception& e) {
            env->ThrowNew(env->FindClass("java/lang/RuntimeException"), e.what());
            return 0;
        }
    }

//JNIEXPORT jlong
//JNICALL
//Java_com_example_NativeEqualizerModule_nativeInit(JNIEnv *env, jobject thiz) {
//    auto *equalizer = new NativeEqualizer();
//    return reinterpret_cast<jlong>(equalizer);
//}
//
//JNIEXPORT void JNICALL
//Java_com_example_NativeEqualizerModule_nativeSetGlobalGain(JNIEnv *env, jobject thiz, jlong
//ptr, jfloat gain) {
//    auto *equalizer = reinterpret_cast<NativeEqualizer *>(ptr);
//    if (equalizer != nullptr) {
//        equalizer->setGlobalGain(gain);
//    }
//}
//
//JNIEXPORT void JNICALL
//Java_com_example_NativeEqualizerModule_nativeSetBandGain(JNIEnv *env, jobject thiz, jlong ptr,
//                                                         jint band, jfloat gain) {
//    auto *equalizer = reinterpret_cast<NativeEqualizer *>(ptr);
//    if (equalizer != nullptr) {
//        equalizer->setBandGain(band, gain);
//    }
//}
//
//JNIEXPORT void JNICALL
//Java_com_example_NativeEqualizerModule_nativeSetEnabled(JNIEnv *env, jobject thiz, jlong ptr,
//                                                        jboolean enabled) {
//    auto *equalizer = reinterpret_cast<NativeEqualizer *>(ptr);
//    if (equalizer != nullptr) {
//        equalizer->setEnabled(enabled);
//    }
//}
//
//JNIEXPORT void JNICALL
//Java_com_example_NativeEqualizerModule_nativeReset(JNIEnv *env, jobject thiz, jlong ptr) {
//    auto *equalizer = reinterpret_cast<NativeEqualizer *>(ptr);
//    if (equalizer != nullptr) {
//        equalizer->reset();
//    }
//}

} // extern "C"
