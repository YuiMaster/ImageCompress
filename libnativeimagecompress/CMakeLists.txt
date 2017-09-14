# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# 构建cmake 所需要的最小版本号
cmake_minimum_required(VERSION 3.4.1)

# 编译预配置
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

#设置生成的so动态库最后输出的路径(libnativeimagecompress:为module路径)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/../libnativeimagecompress/jniLibs/${ANDROID_ABI})

#设置变量  libJpeg_DIR
set(libJpeg_DIR ${CMAKE_SOURCE_DIR}/../libnativeimagecompress/libJpeg)

# 创建一个动态库 lib_jepeg 直接引用libjpeg.so
add_library(lib_jepeg SHARED IMPORTED)
set_target_properties(lib_jepeg PROPERTIES IMPORTED_LOCATION
    ${libJpeg_DIR}/lib/${ANDROID_ABI}/libjpeg.so)



#查找在某个路径下的所有源文件
aux_source_directory(${CMAKE_SOURCE_DIR}/../libnativeimagecompress/src/main/cpp SRC_LIST)
list(APPEND SRC_LIST)


# 创建命名一个库将其设置为 STATIC
# or SHARED, 并且提供源码的相对路径
# 你可以定义多个库 CMake 会为你创建
# Gradle 会自动将共享库和你的apk 关联
add_library( # Sets the name of the library.
              picture-compress

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             ${SRC_LIST}
             )

#搜索特定的预制的库并且存储他的路径,作为一个参数，默认cmake 包含系统库的路径，如果你想要用只需要在制定在ndk库中的名称。
#在构建完成之前camke 会检查库是否存在

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

find_library(graphics

             jnigraphics)


#配置包含的文件（头文件）
target_include_directories(picture-compress PRIVATE
                           ${libJpeg_DIR}/include)

#指定那些库Cmake应该链接到目标库，你可以链接多个库，可以是系统库也可以是第三方库

target_link_libraries( # Specifies the target library.
                       picture-compress

                       lib_jepeg
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}

                       ${graphics}
                       )