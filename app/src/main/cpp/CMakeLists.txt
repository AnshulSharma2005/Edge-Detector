cmake_minimum_required(VERSION 3.10.2)
project("edgedetector")

# -----------------------------
# 1. ABSOLUTE PATHS (COMMAND)
# -----------------------------
set(OPENCV_INCLUDE_DIR "D:/projects/Edge-Detector/app/src/main/cpp/include")
set(OPENCV_LIB_DIR     "D:/projects/Edge-Detector/app/src/main/jniLibs")

# -----------------------------
# 2. INCLUDE OPEN CV HEADERS
# -----------------------------
include_directories(${OPENCV_INCLUDE_DIR})

# -----------------------------
# 3. IMPORT libopencv_java4.so
# -----------------------------
add_library(opencv_java4 SHARED IMPORTED)

set_target_properties(
    opencv_java4
    PROPERTIES IMPORTED_LOCATION
    ${OPENCV_LIB_DIR}/${ANDROID_ABI}/libopencv_java4.so
)

# -----------------------------
# 4. BUILD NATIVE LIB
# -----------------------------
add_library(
    native-lib
    SHARED
    native-lib.cpp
)

# -----------------------------
# 5. LINK LIBRARIES
# -----------------------------
find_library(log-lib log)

target_link_libraries(
    native-lib
    opencv_java4
    ${log-lib}
)
