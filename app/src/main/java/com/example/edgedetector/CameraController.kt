package com.example.edgedetector

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader
import android.util.Size
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread

/**
 * Minimal Camera2 controller that uses ImageReader (YUV_420_888) and converts to RGBA bytes
 */
class CameraController(
    private val ctx: Context,
    private val onFrame: (ByteArray, Int, Int) -> Unit
) {
    private val cameraManager = ctx.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraDevice: CameraDevice? = null
    private var session: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null

    private val backgroundThread = HandlerThread("CameraBg").apply { start() }
    private val bgHandler = Handler(backgroundThread.looper)

    @SuppressLint("MissingPermission")
    fun open() {
        val camId = cameraManager.cameraIdList.first()
        val char = cameraManager.getCameraCharacteristics(camId)
        val streamMap = char.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val size = streamMap!!
            .getOutputSizes(ImageFormat.YUV_420_888)
            .firstOrNull { it.width <= 1280 } ?: Size(1280, 720)

        imageReader = ImageReader.newInstance(
            size.width,
            size.height,
            ImageFormat.YUV_420_888,
            3
        )

        imageReader!!.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            try {
                val rgba = yuvToRgba(image)
                onFrame(rgba, image.width, image.height)
            } finally {
                image.close()
            }
        }, bgHandler)

        cameraManager.openCamera(camId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera

                val surface = imageReader!!.surface
                val requestBuilder =
                    camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                requestBuilder.addTarget(surface)
                requestBuilder.set(
                    CaptureRequest.CONTROL_MODE,
                    CameraMetadata.CONTROL_MODE_AUTO
                )

                camera.createCaptureSession(
                    listOf(surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            this@CameraController.session = session
                            session.setRepeatingRequest(
                                requestBuilder.build(),
                                null,
                                bgHandler
                            )
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {}
                    },
                    bgHandler
                )
            }

            override fun onDisconnected(camera: CameraDevice) {
                camera.close()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                camera.close()
            }
        }, bgHandler)
    }

    fun close() {
        session?.close()
        cameraDevice?.close()
        imageReader?.close()
        backgroundThread.quitSafely()
    }

    private fun yuvToRgba(image: Image): ByteArray {
        val w = image.width
        val h = image.height

        val yPlane = image.planes[0]
        val uPlane = image.planes[1]
        val vPlane = image.planes[2]

        val yBuffer = yPlane.buffer
        val uBuffer = uPlane.buffer
        val vBuffer = vPlane.buffer

        val yRowStride = yPlane.rowStride
        val uvRowStride = uPlane.rowStride
        val uvPixelStride = uPlane.pixelStride

        val out = ByteArray(w * h * 4)
        var index = 0

        for (j in 0 until h) {
            for (i in 0 until w) {

                val y = 0xFF and yBuffer.get(j * yRowStride + i).toInt()
                val uvIndex = (j / 2) * uvRowStride + (i / 2) * uvPixelStride

                val u = 0xFF and uBuffer.get(uvIndex).toInt()
                val v = 0xFF and vBuffer.get(uvIndex).toInt()

                val c = y - 16
                val d = u - 128
                val e = v - 128

                var r = (1.164f * c + 1.596f * e).toInt()
                var g = (1.164f * c - 0.392f * d - 0.813f * e).toInt()
                var b = (1.164f * c + 2.017f * d).toInt()

                r = r.coerceIn(0, 255)
                g = g.coerceIn(0, 255)
                b = b.coerceIn(0, 255)

                out[index++] = r.toByte()
                out[index++] = g.toByte()
                out[index++] = b.toByte()
                out[index++] = 0xFF.toByte()
            }
        }
        return out
    }
}
