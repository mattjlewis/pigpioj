# Makefile:
#	libpigpioj - Java (JNI) library wrapper of pigpio for the Raspberry Pi
#

# See this regarding signal chaining: http://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/signals003.html#CIHFCAIG
# Set LD_PRELOAD=$JAVA_HOME/jre/lib/arm/libjsig.so

RM = rm -f

CC           = $(CROSS_PREFIX)gcc
AR           = $(CROSS_PREFIX)ar
SIZE         = $(CROSS_PREFIX)size
STRIP        = $(CROSS_PREFIX)strip
SHLIB        = $(CC) -shared
STRIPLIB     = $(STRIP) --strip-unneeded

CFLAGS       := -O3 -Wall -pthread -fPIC -march=$(ARCH) $(CC_CFLAGS)

INCLUDES	   = -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(HOME)/pigpio

LFLAGS       = -L$(HOME)/pigpio/$(ARCH)

#LIBS         = -Wl,-Bstatic -lpigpio -Wl,-Bdynamic
LIBS         = -lpigpio

SRCS	       = pigpioj_util.c \
	uk_pigpioj_PigpioGpio.c \
	uk_pigpioj_PigpioI2C.c \
	uk_pigpioj_PigpioSPI.c \
	uk_pigpioj_PigpioBitBangI2C.c \
	uk_pigpioj_PigpioWaveform.c \
	uk_pigpioj_PigpioSerial.c

OBJS         = $(SRCS:.c=.o)

LIB_TARGET       = libpigpioj.so

all:	$(LIB_TARGET)

.c.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c $< -o $@

$(LIB_TARGET): $(OBJS)
	$(SHLIB) -o $(LIB_TARGET) $(OBJS) $(LFLAGS) $(LIBS)
	$(STRIPLIB) $(LIB_TARGET)

clean:
	$(RM) $(OBJS) $(LIB_TARGET) *~ core tags Makefile.bak

tags:	$(SRC)
	@echo [ctags]
	@ctags $(SRC)

depend:
	makedepend -Y $(SRCS)
