package com.example.edgedetector

object FrameDispatcher {

    init {
        System.loadLibrary("native-lib")
    }

    external fun processFrameRGBA(input: ByteArray, width : Int, height  : Int): ByteArray
}
