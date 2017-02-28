CC       = gcc
FC       = gfortran
LINK     = $(FC)

CFLAGS   = -O3 -fopenmp 
LDFLAGS  = $(CFLAGS) 

OBJS = schedules.o
LBLAS    = /home/saradion/n7/2A/Concurrency/OpenMP/acml-4.4.0/gfortran64/lib/libacml.a -lm

LIBS = $(LBLAS) -lm

main: $(OBJS)
	$(LINK) -o $@  $(LDFLAGS) $(OBJS) $(LIBS)






clean:
	(rm -f *.o main)


%.o: %.c
	$(CC)  $(CFLAGS)  $(CINCLUDES) $(INCLUDES) $(CDEFS) $(PREC) -c $<





