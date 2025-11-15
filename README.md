#  Edge Detector – Real-Time Android Edge Detection App
### Powered by Kotlin, C++, OpenCV, Camera2 API & GLSurfaceView

This Android application performs **real-time edge detection** directly from the device camera.
Built using **native OpenCV C++**, **NDK**, **Camera2 API**, and **OpenGL ES rendering**, the app provides smooth and fast edge detection on modern smartphones.

---

## Features
✔️ Real-time camera preview  
✔️ OpenCV C++ edge detection  
✔️ 30–60 FPS processing (device dependent)  
✔️ Toggle between original & edge view  
✔️ Uses Camera2 API for high-quality frames  
✔️ Optimized YUV → RGBA conversion  
✔️ Supports **arm64-v8a** and **armeabi-v7a** devices  
✔️ Clean & minimal UI  

---

##  Tech Stack

| Layer | Technology |
|-------|------------|
| Language | **Kotlin**, **C++** |
| Native Image Processing | **OpenCV 4.x (`libopencv_java4.so`)** |
| Android Framework | **Camera2 API**, **GLSurfaceView**, **TextureView** |
| Rendering | **OpenGL ES 2.0** |
| Build Tools | **NDK 26+**, **CMake**, **Gradle** |
| Supported Architectures | ARM64, ARMv7 |

---

##  Project Structure

```
Edge-Detector/
│── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/edgedetector/
│   │   │   ├── cpp/       # Native C++ edge detection
│   │   │   ├── jniLibs/   # Native OpenCV + libc++_shared.so
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle
│── README.md
│── LICENSE
│── .gitignore
```

---

##  How to Build & Run

### **1. Requirements**
- Android Studio (Hedgehog/Ladybug recommended)  
- NDK 26+  
- CMake 3.22+  
- Android SDK 33  
- Physical Android device (Camera2 required)

---

### **2. Clone the Repo**
```
git clone https://github.com/AnshulSharma2005/Edge-Detector.git
cd Edge-Detector
```

---

### **3. Open in Android Studio**
Open the project and allow Android Studio to download required components.

---

### **4. Required Native Libraries**
Inside:

```
app/src/main/jniLibs/
```

These files **must exist**:

```
arm64-v8a/
    libopencv_java4.so
    libc++_shared.so

armeabi-v7a/
    libopencv_java4.so
    libc++_shared.so
```

If missing, the app will crash with:

```
UnsatisfiedLinkError: libc++_shared.so not found
```

---

##  Running the App
1. Connect your Android phone  
2. Enable USB debugging  
3. Press **Run** in Android Studio  
4. Grant Camera permission  
5. You will see live real-time edge output  

---

##  How Processing Works (Simplified)

### 1. Camera2 → YUV Input  
Camera provides YUV_420_888 frames.

### 2. Kotlin converts to RGBA  
Fast pixel-by-pixel conversion.

### 3. JNI → C++ → OpenCV  
Native code does:

```cpp
cv::Mat rgba(height, width, CV_8UC4, input);
cv::Mat gray, edges;
cv::cvtColor(rgba, gray, cv::COLOR_RGBA2GRAY);
cv::Canny(gray, edges, 70, 150);
cv::cvtColor(edges, rgba, cv::COLOR_GRAY2RGBA);
```

### 4. GLSurfaceView renders output  
GPU-based texture upload gives **smooth FPS**.

---

##  Troubleshooting

### App Crashes on Launch  
**Reason:** Missing `libc++_shared.so`  
**Fix:** Copy from NDK → project `jniLibs` folder.

### Black Camera Screen  
- Permission not granted  
- Camera not supported  
- Try lowering resolution in `CameraController`

---

##  Improvements You Can Add
 Edge threshold slider  
 Multiple filters (Sobel, Laplacian, Sketch)  
 Record processed video  
 OpenGL shaders for GPU edge detection  

---

##  Author  
**Anshul Sharma**  
AI & Android Developer  

---

## License (MIT)
Open for personal & academic use.
