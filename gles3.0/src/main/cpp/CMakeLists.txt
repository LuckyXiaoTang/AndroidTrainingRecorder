cmake_minimum_required(VERSION 3.4.1)

set(jnilibs "${CMAKE_SOURCE_DIR}/../jniLibs")

include_directories(
        render
        utils
        sample
)

set(src-files
        jniImpl.cpp
        render/EGLRender.cpp
        render/GLRenderContext.cpp
        sample/TriangleSample.cpp
        sample/TextureMapSample.cpp
        sample/NV21TextureMapSample.cpp
        utils/GLUtil.cpp
)

add_library(
        native-render

        SHARED

        ${src-files}
)

find_library(
        log-lib

        log
)

target_link_libraries(
        native-render

        ${log-lib}
        EGL
        GLESv3
)