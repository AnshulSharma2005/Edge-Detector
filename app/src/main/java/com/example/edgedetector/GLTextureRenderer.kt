package com.example.edgedetector

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Log
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.concurrent.atomic.AtomicReference

class GLTextureRenderer : GLSurfaceView.Renderer {

    private val vertexData = floatArrayOf(
        -1f, -1f, 0f, 0f,
         1f, -1f, 1f, 0f,
        -1f,  1f, 0f, 1f,
         1f,  1f, 1f, 1f
    )

    private val buffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(vertexData)
                position(0)
            }

    private var program = 0
    private var textureId = 0
    private var aPos = 0
    private var aTex = 0
    private var uTexture = 0

    private val frame = AtomicReference<Frame?>(null)

    var onFpsUpdated: ((Int) -> Unit)? = null
    private var last = System.currentTimeMillis()
    private var fpsCount = 0

    var latestBitmap: Bitmap? = null

    private var showEdges = true
    fun setShowEdges(v: Boolean) { showEdges = v }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        program = buildProgram(VS, FS)
        aPos = GLES20.glGetAttribLocation(program, "aPos")
        aTex = GLES20.glGetAttribLocation(program, "aTex")
        uTexture = GLES20.glGetUniformLocation(program, "uTexture")

        textureId = createTexture()
    }

    override fun onSurfaceChanged(gl: GL10?, w: Int, h: Int) {
        GLES20.glViewport(0, 0, w, h)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        val frameData = frame.getAndSet(null) ?: return

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        val bb = ByteBuffer.wrap(frameData.data)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0,
            GLES20.GL_RGBA,
            frameData.width, frameData.height,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE, bb
        )

        GLES20.glUseProgram(program)

        buffer.position(0)
        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glVertexAttribPointer(aPos, 2, GLES20.GL_FLOAT, false, 16, buffer)

        buffer.position(2)
        GLES20.glEnableVertexAttribArray(aTex)
        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, 16, buffer)

        GLES20.glUniform1i(uTexture, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // FPS calculation
        fpsCount++
        val now = System.currentTimeMillis()
        if (now - last >= 1000) {
            onFpsUpdated?.invoke(fpsCount)
            fpsCount = 0
            last = now
        }
    }

    fun updateFrame(data: ByteArray, w: Int, h: Int) {
        frame.set(Frame(data, w, h))
    }

    private fun createTexture(): Int {
        val arr = IntArray(1)
        GLES20.glGenTextures(1, arr, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, arr[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        return arr[0]
    }

    private fun loadShader(type: Int, source: String): Int {
        val id = GLES20.glCreateShader(type)
        GLES20.glShaderSource(id, source)
        GLES20.glCompileShader(id)
        return id
    }

    private fun buildProgram(vs: String, fs: String): Int {
        val p = GLES20.glCreateProgram()
        GLES20.glAttachShader(p, loadShader(GLES20.GL_VERTEX_SHADER, vs))
        GLES20.glAttachShader(p, loadShader(GLES20.GL_FRAGMENT_SHADER, fs))
        GLES20.glLinkProgram(p)
        return p
    }

    companion object {
        private const val VS = """
            attribute vec2 aPos;
            attribute vec2 aTex;
            varying vec2 tex;
            void main() {
                gl_Position = vec4(aPos, 0.0, 1.0);
                tex = aTex;
            }
        """

        private const val FS = """
            precision mediump float;
            varying vec2 tex;
            uniform sampler2D uTexture;
            void main() {
                gl_FragColor = texture2D(uTexture, tex);
            }
        """
    }

    data class Frame(val data: ByteArray, val width : Int, val height : Int)
}
