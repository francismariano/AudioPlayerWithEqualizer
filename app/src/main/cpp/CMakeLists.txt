cmake_minimum_required (VERSION 3.4.1)

add_library(
    equalizer
    SHARED
    equalizer.cpp
)

find_library(
    log_lib
    log
)

target_link_libraries(
    equalizer
    ${log_lib}
)
