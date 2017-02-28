cmake_minimum_required(VERSION 3.6)
project(BE2016)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    derivative_free/aux.c
    derivative_free/aux.h
    derivative_free/main.c
    linked_list/aux.c
    linked_list/aux.h
    linked_list/main.c
    matrix_multiplication/aux.c
    matrix_multiplication/aux.h
    matrix_multiplication/main.c
    reduction/aux.c
    reduction/aux.h
    reduction/main.c
    synchronizations/aux.c
    synchronizations/aux.h
    synchronizations/main.c)

add_executable(BE2016 ${SOURCE_FILES})