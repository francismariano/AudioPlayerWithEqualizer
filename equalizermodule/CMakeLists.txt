cmake_minimum_required(VERSION 3.10.2)
project("equalizer-jni")

add_library(
        equalizer-jni
        SHARED
        src/main/cpp/equalizer-jni.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        equalizer-jni
        ${log-lib}
)