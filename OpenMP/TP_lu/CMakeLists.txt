cmake_minimum_required(VERSION 3.6)
project(TP_lu)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    aux.c
    common.h
    kernels.c
    lu_par_dag.c
    lu_par_loop.c
    lu_seq.c
    main.c
    trace.c
    trace.h)

add_executable(TP_lu ${SOURCE_FILES})