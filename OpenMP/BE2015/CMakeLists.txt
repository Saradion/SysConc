cmake_minimum_required(VERSION 3.6)
project(BE2015)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    ConjugateGradient/common.h
    ConjugateGradient/ConjugateGradient.c
    ConjugateGradient/iohb.c
    ConjugateGradient/iohb.h
    ConjugateGradient/kernels.c
    ConjugateGradient/readmat.c
    MergeSort/mergesort.c
    PrefixScan/prefixscan.c
    Schedules/schedules.c
    tree_traversal/aux.c
    tree_traversal/aux.h
    tree_traversal/main.c
    tree_traversal/treetrav_par.c
    tree_traversal/treetrav_seq.c)

add_executable(BE2015 ${SOURCE_FILES})