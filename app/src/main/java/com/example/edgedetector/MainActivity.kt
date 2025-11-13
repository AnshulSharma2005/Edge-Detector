package com.example.edgedetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var glView: GLSurfaceWrapper
    private lateinit var btnToggle: Button
    private lateinit var tvFps: TextView

    private lateinit var camera: CameraController
    private var showingEdges = true

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
            if (ok) startCamera()
            else finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glView = findViewById(R.id.glView)
        btnToggle = findViewById(R.id.btnToggle)
        tvFps = findViewById(R.id.tvFps)

        System.loadLibrary("native-lib")

        btnToggle.setOnClickListener {
            showingEdges = !showingEdges
            glView.renderer.setShowEdges(showingEdges)
        }

        glView.renderer.onFpsUpdated = { fps ->
            runOnUiThread { tvFps.text = "FPS: $fps" }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else startCamera()
    }

    private fun startCamera() {
        camera = CameraController(this) { rgba, w, h ->
            val processed = FrameDispatcher.processFrameRGBA(rgba, w, h)
            glView.renderer.updateFrame(processed, w, h)
        }
        camera.open()
    }

    override fun onDestroy() {
        camera.close()
        super.onDestroy()
    }
}
