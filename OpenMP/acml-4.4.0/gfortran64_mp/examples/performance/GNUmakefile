
# GNUmakefile to build and execute ACML example programs

# Fortran compiler and flags (modify if necessary)
#
# Location of ACML installation
ACMLDIR := /opt/acml4.4.0/gfortran64_mp

F90 := gfortran
F90LINK := $(F90)
F90FLAGS :=  
F90LINKFLAGS :=  -fopenmp
F90LINKLIBS := -lrt

OMP_NUM_THREADS := 1


STACKSIZELINKSWITCH :=
EXPORT_PATH := export LD_LIBRARY_PATH='$(ACMLDIR)/lib:$(LD_LIBRARY_PATH)'

# Override with doplot=0 to avoid using gnuplot
doplot := 1

obj := o

.SUFFIXES: .f90 .c .$(obj) .exe .res

LIBACML := $(ACMLDIR)/lib/libacml_mp.a

# Source code of Fortran performance examples
FSOURCE := time_drandbeta.f90 time_dranduniform.f90 time_zfft1d.f90
THREADFSOURCE := time_cfft2d.f90 time_dgemm.f90 time_dgetrf.f90

FEXE := $(FSOURCE:.f90=.exe)
THREADFEXE := $(THREADFSOURCE:.f90=.exe)

RESULTS := $(FSOURCE:.f90=.res)
THREADRESULTS := $(THREADFSOURCE:.f90=.res)

all: $(RESULTS) $(THREADRESULTS)

.f90.res:
	@echo ""
	@echo "Compiling program $*.f90:"
	$(F90) -c $(F90FLAGS) $*.f90 -o $*.$(obj)
	@echo "Linking program $*.exe:"
	$(F90LINK) $(F90LINKFLAGS) $*.$(obj) $(LIBACML) $(F90LINKLIBS) -o $*.exe $(STACKSIZELINKSWITCH)
	@echo "Running program $*.exe. This may take a few minutes."
	@rm -f $@
	@(nt=1; \
	  mtexample=`echo $(THREADFSOURCE) | grep -c $*.f90 | cat`; \
	  while [ "$$nt" -le "$(OMP_NUM_THREADS)" ]; do \
	    if [ "$$nt" != "1" ]; then \
	      echo "Running with $${nt} threads ..."; \
	    else \
	      echo "Running with 1 thread ..."; \
	    fi; \
	    echo "" >> $@; \
	    echo "# Results with $${nt} threads" >> $@; \
	    echo "(OMP_NUM_THREADS=$${nt}; export OMP_NUM_THREADS; $(EXPORT_PATH); ./$*.exe >> $@ 2>&1)"; \
	    (OMP_NUM_THREADS=$${nt}; export OMP_NUM_THREADS; $(EXPORT_PATH); ./$*.exe >> $@ 2>&1); \
	    if [ "$${mtexample}" = "1" ]; then \
	      nt=`echo "$${nt} 2 * p" | dc`; \
	    else \
	      nt=99999; \
	    fi; \
	  done; \
	  nt=`echo "$${nt} 2 / p" | dc`; \
	  if [ "$${mtexample}" = "1" -a "$(OMP_NUM_THREADS)" -gt "1" ]; then \
	    if [ "$$nt" -ne "$(OMP_NUM_THREADS)" ]; then \
	      echo "Running with $(OMP_NUM_THREADS) threads ..."; \
	      echo "" >> $@; \
	      echo "# Results with $(OMP_NUM_THREADS) threads" >> $@; \
	      echo "(OMP_NUM_THREADS=$(OMP_NUM_THREADS); export OMP_NUM_THREADS; $(EXPORT_PATH); ./$*.exe >> $@ 2>&1)"; \
	      (OMP_NUM_THREADS=$(OMP_NUM_THREADS); export OMP_NUM_THREADS; $(EXPORT_PATH); ./$*.exe >> $@ 2>&1); \
	    fi; \
	  fi)
#       Show the results
	@cat $@
#       Plot the results
	@if [ "$(doplot)" = "1" ]; then \
	  echo "./plot_one.sh $@"; \
	  ./plot_one.sh $@; \
	fi

# Rule to plot all time_?.res data from current directory
plots:
	./plot_all.sh

clean:
	rm -f $(FEXE) $(THREADFEXE) $(CEXE) $(FSOURCE:.f90=.$(obj)) $(RESULTS) \
	$(THREADFSOURCE:.f90=.$(obj)) $(THREADRESULTS)
