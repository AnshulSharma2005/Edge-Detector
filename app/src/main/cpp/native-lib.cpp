#include <jni.h>
#include <android/log.h>
#include <opencv2/opencv.hpp>

using namespace cv;

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_edgedetector_FrameDispatcher_processFrameRGBA(
        JNIEnv *env,
        jobject,
        jbyteArray input,
        jint width,
        jint height) {

    jbyte *bytes = env->GetByteArrayElements(input, nullptr);

    Mat rgba(height, width, CV_8UC4, (unsigned char *) bytes);
    Mat gray, edges, outRGBA;

    cvtColor(rgba, gray, COLOR_RGBA2GRAY);
    GaussianBlur(gray, gray, Size(5,5), 1.5);
    Canny(gray, edges, 50, 150);

    cvtColor(edges, outRGBA, COLOR_GRAY2RGBA);

    int size = width * height * 4;
    jbyteArray outArr = env->NewByteArray(size);
    env->SetByteArrayRegion(outArr, 0, size, (jbyte *) outRGBA.data);

    env->ReleaseByteArrayElements(input, bytes, JNI_ABORT);
    return outArr;
}
