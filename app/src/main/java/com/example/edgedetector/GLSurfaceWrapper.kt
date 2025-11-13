package com.example.edgedetector

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class GLSurfaceWrapper(context: Context, attrs: AttributeSet? = null)
    : GLSurfaceView(context, attrs) {

    val renderer: GLTextureRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = GLTextureRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}
