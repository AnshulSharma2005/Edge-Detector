# 🧠 Edge Detector

An Android-based **Edge Detection App** built using **Kotlin** and **OpenCV**.  
It captures live camera frames, processes them in real-time, and highlights edges using advanced computer vision algorithms.

---

## 🚀 Features

- 📸 Real-time edge detection using OpenCV  
- 🧩 Native library integration with JNI  
- ⚙️ Clean modular structure (Kotlin + C++)  
- 🎨 Simple and intuitive UI  
- 🔧 Gradle-based build configuration  

---

## 🗂️ Project Structure

Edge-Detector/
│
├── app/ # Main Android app module
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/example/edge_detector/
│ │ │ ├── cpp/
│ │ │ └── res/
│ └── build.gradle
│
├── jniLibs/ # Precompiled OpenCV native libraries
├── docs/ # Documentation and sample images
├── gradle/ # Gradle build configuration
├── build.gradle # Root build configuration
├── settings.gradle # Gradle settings
└── README.md # Project documentation


---

## ⚙️ Tech Stack

| Technology | Purpose |
|-------------|----------|
| **Kotlin** | Android app logic and UI |
| **OpenCV** | Image processing and edge detection |
| **JNI / C++** | Native library integration |
| **Gradle** | Build and dependency management |

---

## 🧩 Setup Instructions

### 1️. Clone the Repository
```bash
git clone https://github.com/AnshulSharma2005/Edge-Detector.git
cd Edge-Detector

2️. Open in Android Studio

Open Android Studio → Open an existing project → Select Edge-Detector

Wait for Gradle sync to complete.

3️. Add OpenCV SDK (if not included)

Place the OpenCV SDK in your local directory if missing.

Update local.properties with your SDK path if necessary:

sdk.dir=C:\\Users\\Anshul\\AppData\\Local\\Android\\Sdk

4️.Build and Run

Connect your Android device or use an emulator.

Click Run in Android Studio.

Sample Output

Contributing

Contributions are welcome!
If you’d like to improve features or fix issues:

Fork the repo

Create your feature branch (git checkout -b feature/your-feature)

Commit your changes

Push to the branch (git push origin feature/your-feature)

Open a Pull Request

Author

Anshul Sharma
GitHub
 • LinkedIn