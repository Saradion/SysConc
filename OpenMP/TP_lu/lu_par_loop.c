#include "trace.h"
#include "common.h"


/* This routine performs the LU factorization of a square matrix by
   block-columns */

void lu_par_loop(Matrix A){


  int i, j;

  /* Initialize the tracing system */
  trace_init();

  for(i=0; i<A.NB; i++){
    
    /* Do the Panel operation on column i */
    panel(A, i);

    /* Parallelize this loop     */

#pragma omp parallel for
    for(j=i+1; j<A.NB; j++){
      /* Update column j with respect to the result of panel(A, i) */
      update(A, i, j);
    }
  }
  

  /* This routine applies permutations resulting from numerical
     pivoting. It has to be executed sequentially. */
  backperm(A);
  
  /* Write the trace in file (ignore) */
  trace_dump("trace_par_loop.svg");
  
  return;
  
}




