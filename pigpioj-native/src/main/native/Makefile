# Makefile:
#	libpigpioj - Java (JNI) library wrapper of pigpio for the Raspberry Pi
#

# See this regarding signal chaining: http://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/signals003.html#CIHFCAIG
# Set LD_PRELOAD=$JAVA_HOME/jre/lib/arm/libjsig.so

RM           := rm -f
CC           := $(CROSS_PREFIX)gcc
AR           := $(CROSS_PREFIX)ar
SIZE         := $(CROSS_PREFIX)size
STRIP        := $(CROSS_PREFIX)strip
SHLIB        := $(CC) -shared
STRIP_FLAGS  :=

LIB_NAME     := pigpioj

SRCS := pigpioj_util.c \
	uk_pigpioj_PigpioGpio.c \
	uk_pigpioj_PigpioI2C.c \
	uk_pigpioj_PigpioSPI.c \
	uk_pigpioj_PigpioBitBangI2C.c \
	uk_pigpioj_PigpioWaveform.c \
	uk_pigpioj_PigpioSerial.c

#LIBS := -Wl,-Bstatic -lpigpio -Wl,-Bdynamic
LDLIBS := -lpigpio

ifeq ($(OS),Windows_NT)
    uname_S := Windows
else
    uname_S := $(shell uname -s)
endif

ifeq ($(uname_S), Windows)
    OS_JAVA_INC := win32
    LIB_EXT     := dll
endif
ifeq ($(uname_S), Linux)
    OS_JAVA_INC := linux
    LIB_EXT     := so
    LDLIBS      += -Wl,-Bstatic -li2c -Wl,-Bdynamic
    STRIP_FLAGS += --strip-unneeded
endif
ifeq ($(uname_S), Darwin)
    OS_JAVA_INC := darwin
    LIB_EXT     := dylib
    STRIP_FLAGS += -x
endif

override CFLAGS := -O3 -Wall -pthread -fPIC $(CFLAGS)

INCLUDES   := -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/$(OS_JAVA_INC)
LIB_PREFIX ?= lib
LIB_TARGET := $(LIB_PREFIX)$(LIB_NAME).$(LIB_EXT)
OBJS       := $(SRCS:.c=.o)
LDFLAGS    :=

all:	$(LIB_TARGET)

.c.o:
	@echo [COMPILE] $<
	@$(CC) $(CFLAGS) $(INCLUDES) -c $< -o $@

$(LIB_TARGET): $(OBJS)
	@echo [LINK] $(LIB_TARGET)
	@$(SHLIB) -o $(LIB_TARGET) $(OBJS) $(LDFLAGS) $(LDLIBS)
	@$(STRIP) ${STRIPFLAGS} $(LIB_TARGET)

clean:
	$(RM) $(OBJS) $(LIB_TARGET) *~ core tags Makefile.bak

tags:	$(SRC)
	@echo [ctags]
	@ctags $(SRC)

depend:
	makedepend -Y $(SRCS)
