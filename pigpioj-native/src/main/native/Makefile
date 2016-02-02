# Makefile:
#	libpigpioprovider - Java (JNI) library wrapper for pigpio for the Raspberry Pi
#

TARGET=libpigpioprovider.so

CC	         = gcc
INCLUDE	     = -I. \
               -I/$(JAVA_HOME)/include                         \
               -I/$(JAVA_HOME)/include/linux                   \
               -I/usr/local/include -I/usr/local/include/linux

CFLAGS      := -Wall -pthread $(INCLUDE)

LIBS         =  -lpigpio -lrt

# Should not alter anything below this line
###############################################################################

SRC	=   org_diozero_internal_provider_pigpio_impl_PigpioGpio.c

OBJ =	$(SRC:.c=.o)

all:	lib

lib:	$(OBJ)
	@echo [LINK] $(TARGET)
	@$(CC) $(OBJ) -shared -o $(TARGET) $(LIBS)

.c.o:
	@echo [COMPILE] $<
	@$(CC) -c $(CFLAGS) $< -o $@

clean:
	rm -f $(OBJ) $(TARGET) *~ core tags Makefile.bak

tags:	$(SRC)
	@echo [ctags]
	@ctags $(SRC)

depend:
	makedepend -Y $(SRC)
