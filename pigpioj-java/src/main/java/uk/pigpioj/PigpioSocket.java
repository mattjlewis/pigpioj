package uk.pigpioj;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

@SuppressWarnings("unused")
public class PigpioSocket implements PigpioInterface {
	private static final int DEFAULT_TIMEOUT_MS = 100;
	
	// Commands
	private static final int PI_CMD_MODES = 0;		// gpio mode 0 -
	private static final int PI_CMD_MODEG = 1;		// gpio 0 0 -
	private static final int PI_CMD_PUD = 2;		// gpio pud 0 -
	private static final int PI_CMD_READ = 3;		// gpio 0 0 -
	private static final int PI_CMD_WRITE = 4;		// gpio level 0 -
	private static final int PI_CMD_PWM = 5;		// gpio dutycycle 0 -
	private static final int PI_CMD_PRS = 6;		// gpio range 0 -
	private static final int PI_CMD_PFS = 7;		// gpio frequency 0 -
	private static final int PI_CMD_SERVO = 8;		// gpio pulsewidth 0 -
	private static final int PI_CMD_WDOG = 9;		// gpio timeout 0 -
	private static final int PI_CMD_BR1 = 10;		// 0 0 0 -
	private static final int PI_CMD_BR2 = 11;		// 0 0 0 -
	private static final int PI_CMD_BC1 = 12;		// bits 0 0 -
	private static final int PI_CMD_BC2 = 13;		// bits 0 0 -
	private static final int PI_CMD_BS1 = 14;		// bits 0 0 -
	private static final int PI_CMD_BS2 = 15;		// bits 0 0 -
	private static final int PI_CMD_TICK = 16;		// 0 0 0 -
	private static final int PI_CMD_HWVER = 17;		// 0 0 0 -
	private static final int PI_CMD_NO = 18;		// 0 0 0 -
	private static final int PI_CMD_NB = 19;		// handle bits 0 -
	private static final int PI_CMD_NP = 20;		// handle 0 0 -
	private static final int PI_CMD_NC = 21;		// handle 0 0 -
	private static final int PI_CMD_PRG = 22;		// gpio 0 0 -
	private static final int PI_CMD_PFG = 23;		// gpio 0 0 -
	private static final int PI_CMD_PRRG = 24;		// gpio 0 0 -
	private static final int PI_CMD_HELP = 25;		// N/A N/A N/A N/A
	private static final int PI_CMD_PIGPV = 26;		// 0 0 0 -
	private static final int PI_CMD_WVCLR = 27;		// 0 0 0 -
	private static final int PI_CMD_WVAG = 28;		// 0 0 12*X gpioPulse_t pulse[X]
	private static final int PI_CMD_WVAS = 29;		// gpio baud 12+X uint32_t databits uint32_t stophalfbits uint32_t offset uint8_t data[X]
	private static final int PI_CMD_WVGO = 30;		// 0 0 0 -
	private static final int PI_CMD_WVGOR = 31;		// 0 0 0 -
	private static final int PI_CMD_WVBSY = 32;		// 0 0 0 -
	private static final int PI_CMD_WVHLT = 33;		// 0 0 0 -
	private static final int PI_CMD_WVSM = 34;		// subcmd 0 0 -
	private static final int PI_CMD_WVSP = 35;		// subcmd 0 0 -
	private static final int PI_CMD_WVSC = 36;		// subcmd 0 0 -
	private static final int PI_CMD_TRIG = 37;		// gpio pulselen 4 uint32_t level
	private static final int PI_CMD_PROC = 38;		// 0 0 X uint8_t text[X]
	private static final int PI_CMD_PROCD = 39;		// script_id 0 0 -
	private static final int PI_CMD_PROCR = 40;		// script_id 0 4*X uint32_t pars[X]
	private static final int PI_CMD_PROCS = 41;		// script_id 0 0 -
	private static final int PI_CMD_SLRO = 42;		// gpio baud 4 uint32_t databits
	private static final int PI_CMD_SLR = 43;		// gpio count 0 -
	private static final int PI_CMD_SLRC = 44;		// gpio 0 0 -
	private static final int PI_CMD_PROCP = 45;		// script_id 0 0 -
	private static final int PI_CMD_MICS = 46;		// micros 0 0 -
	private static final int PI_CMD_MILS = 47;		// millis 0 0 -
	private static final int PI_CMD_PARSE = 48;		// N/A N/A N/A N/A
	private static final int PI_CMD_WVCRE = 49;		// 0 0 0
	private static final int PI_CMD_WVDEL = 50;		// wave_id 0 0
	private static final int PI_CMD_WVTX = 51;		// wave_id 0 0
	private static final int PI_CMD_WVTXR = 52;		// wave_id 0 0
	private static final int PI_CMD_WVNEW = 53;		// 0 0 0 -
	private static final int PI_CMD_I2CO = 54;		// bus device 4 uint32_t flags
	private static final int PI_CMD_I2CC = 55;		// handle 0 0 -
	private static final int PI_CMD_I2CRD = 56;		// handle count 0 -
	private static final int PI_CMD_I2CWD = 57;		// handle 0 X uint8_t data[X]
	private static final int PI_CMD_I2CWQ = 58;		// handle bit 0 -
	private static final int PI_CMD_I2CRS = 59; 	// handle 0 0 -
	private static final int PI_CMD_I2CWS = 60; 	// handle byte 0 -
	private static final int PI_CMD_I2CRB = 61; 	// handle register 0 -
	private static final int PI_CMD_I2CWB = 62; 	// handle register 4 uint32_t byte
	private static final int PI_CMD_I2CRW = 63; 	// handle register 0 -
	private static final int PI_CMD_I2CWW = 64; 	// handle register 4 uint32_t word
	private static final int PI_CMD_I2CRK = 65; 	// handle register 0 -
	private static final int PI_CMD_I2CWK = 66;		// handle register X uint8_t bvs[X]
	private static final int PI_CMD_I2CRI = 67; 	// handle register 4 uint32_t num
	private static final int PI_CMD_I2CWI = 68; 	// handle register X uint8_t bvs[X]
	private static final int PI_CMD_I2CPC = 69; 	// handle register 4 uint32_t word
	private static final int PI_CMD_I2CPK = 70; 	// handle register X uint8_t data[X]
	private static final int PI_CMD_SPIO = 71; 		// channel baud 4 uint32_t flags
	private static final int PI_CMD_SPIC = 72;		// handle 0 0 -
	private static final int PI_CMD_SPIR = 73;		// handle count 0 -
	private static final int PI_CMD_SPIW = 74;		// handle 0 X uint8_t data[X]
	private static final int PI_CMD_SPIX = 75;		// handle 0 X uint8_t data[X]
	private static final int PI_CMD_SERO = 76;		// baud flags X uint8_t device[X]
	private static final int PI_CMD_SERC = 77;		// handle 0 0 -
	private static final int PI_CMD_SERRB = 78;		// handle 0 0 -
	private static final int PI_CMD_SERWB = 79;		// handle byte 0 -
	private static final int PI_CMD_SERR = 80;		// handle count 0 -
	private static final int PI_CMD_SERW = 81;		// handle 0 X uint8_t data[X]
	private static final int PI_CMD_SERDA = 82;		// handle 0 0 -
	private static final int PI_CMD_GDC = 83;		// gpio 0 0 -
	private static final int PI_CMD_GPW = 84;		// gpio 0 0 -
	private static final int PI_CMD_HC = 85;		// gpio frequency 0 -
	private static final int PI_CMD_HP = 86;		// gpio frequency 4 uint32_t dutycycle
	private static final int PI_CMD_CF1 = 87;		// arg1 arg2 X uint8_t argx[X]
	private static final int PI_CMD_CF2 = 88;		// arg1 retMax X uint8_t argx[X]
	private static final int PI_CMD_BI2CC = 89;		// sda 0 0 -
	private static final int PI_CMD_BI2CO	= 90;	// sda scl 4 uint32_t baud
	private static final int PI_CMD_BI2CZ = 91;		// sda 0 X uint8_t data[X]
	private static final int PI_CMD_I2CZ	= 92;	// handle 0 X uint8_t data[X]
	private static final int PI_CMD_WVCHA	= 93;	// 0 0 X uint8_t data[X]
	private static final int PI_CMD_SLRI = 94;		// gpio invert 0 -
	private static final int PI_CMD_CGI = 95;		// 0 0 0 -
	private static final int PI_CMD_CSI = 96;		// config 0 0 -
	private static final int PI_CMD_FG = 97;		// gpio steady 0 -
	private static final int PI_CMD_FN = 98;		// gpio steady 4 uint32_t active
	private static final int PI_CMD_NOIB = 99;		// 0 0 0 -
	private static final int PI_CMD_WVTXM = 100;	// wave_id mode 0 -
	private static final int PI_CMD_WVTAT	= 101;	// - - 0 -
	private static final int PADS = 102;			// pad strength 0 -
	private static final int PADG = 103;			// pad 0 0 -
	private static final int PI_CMD_FO = 104;		// mode 0 X uint8_t file[X]
	private static final int PI_CMD_FC = 105;		// handle 0 0 -
	private static final int PI_CMD_FR = 106;		// handle count 0 -
	private static final int PI_CMD_FW = 107;		// handle 0 X uint8_t data[X]
	private static final int PI_CMD_FS = 108;		// handle offset 4 uint32_t from
	private static final int PI_CMD_FL = 109;		// count 0 X uint8_t pattern[X]
	private static final int PI_CMD_SHELL = 110;	// len(name) 0 len(name)+1+len(string) uint8_t name[len(name)] uint8_t null (0) uint8_t string[len(string)]
	private static final int PI_CMD_BSPIC = 111;	// CS 0 0 -
	private static final int PI_CMD_BSPIO = 112;	// CS 0 20 uint32_t MISO uint32_t MOSI uint32_t SCLK uint32_t baud uint32_t spi_flags
	private static final int PI_CMD_BSPIX = 113;	// CS 0 X uint8_t data[X]
	private static final int PI_CMD_BSCX = 114;		// control 0 X uint8_t data[X]
	private static final int PI_CMD_EVM = 115;		// handle bits 0 -
	private static final int PI_CMD_EVT = 116;		// event 0 0 -

	static final int DEFAULT_PORT = 8888;
	
	private Deque<Message> messageQueue;
	private Lock lock;
	private Condition condition;
	private EventLoopGroup workerGroup;
	private Channel channel;
	private ChannelFuture lastWriteFuture;
	private int timeoutMs;
	
	public PigpioSocket() {
		this(DEFAULT_TIMEOUT_MS);
	}
	
	public PigpioSocket(int timeoutMs) {
		messageQueue = new LinkedList<>();
		lock = new ReentrantLock();
		condition = lock.newCondition();
		this.timeoutMs = timeoutMs;
	}
	
	public void connect(String host) throws InterruptedException {
		connect(host, DEFAULT_PORT);
	}
	
	public void connect(String host, int port) throws InterruptedException {
		workerGroup = new NioEventLoopGroup();
		
		Bootstrap b = new Bootstrap();
		b.group(workerGroup)
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new Decoder(), new Encoder(), new Handler(PigpioSocket.this));
				}
			});
		
		// Connect
		channel = b.connect(host, port).sync().channel();
	}
	
	@Override
	public void close() {
		if (channel == null || ! channel.isOpen()) {
			return;
		}
		
		channel.close();
		
		try {
			channel.closeFuture().sync();
			
			// Wait until all messages are flushed before closing the channel.
			if (lastWriteFuture != null) {
				lastWriteFuture.sync();
			}
		} catch (InterruptedException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}

	void messageReceived(Message msg) {
		lock.lock();
		try {
			messageQueue.add(msg);
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	private Message waitForMessage(int command) {
		Message message = null;
		lock.lock();
		try {
			condition.await(timeoutMs, TimeUnit.MILLISECONDS);
			message = messageQueue.pop();
			
			if (message.cmd != command) {
				System.out.println("Unexpected message: " + message + ". Was expecting " + command);
				message = null;
			}
		} catch (InterruptedException e) {
			System.out.println("Interrupted: " + e);
		} finally {
			lock.unlock();
		}
		
		return message;
	}
	
	private synchronized Message sendMessage(Message message) {
		lastWriteFuture = channel.writeAndFlush(message);
		return waitForMessage(message.cmd);
	}
	
	@Override
	public int enableListener(int gpio, int edge, PigpioCallback callback) {
		// TODO Implement polling logic!
		return PigpioConstants.SUCCESS;
	}
	
	@Override
	public int disableListener(int gpio) {
		// TODO Implement polling logic!
		return PigpioConstants.SUCCESS;
	}
	
	@Override
	public int getVersion() {
		Message message = sendMessage(new Message(PI_CMD_PIGPV, 0, 0, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}
	
	@Override
	public int getHardwareRevision() {
		Message message = sendMessage(new Message(PI_CMD_HWVER, 0, 0, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}
	
	@Override
	public int getMode(int gpio) {
		Message message = sendMessage(new Message(PI_CMD_MODEG, gpio, 0, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}
	
	@Override
	public int setMode(int gpio, int mode) {
		if (sendMessage(new Message(PI_CMD_MODES, gpio, mode, 0)) == null) {
			return PigpioConstants.ERROR;
		}
		
		return PigpioConstants.SUCCESS;
	}
	
	@Override
	public int read(int gpio) {
		Message message = sendMessage(new Message(PI_CMD_READ, gpio, 0, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}
	
	@Override
	public int write(int gpio, boolean level) {
		if (sendMessage(
				new Message(PI_CMD_WRITE, gpio, level ? PigpioConstants.PI_ON : PigpioConstants.PI_OFF, 0)) == null) {
			return PigpioConstants.ERROR;
		}
		
		return PigpioConstants.SUCCESS;
	}
	
	@Override
	public int getPWMDutyCycle(int gpio) {
		Message message = sendMessage(new Message(PI_CMD_GDC, gpio, 0, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}
	
	@Override
	public int setPWMDutyCycle(int gpio, int dutyCycle) {
		if (sendMessage(new Message(PI_CMD_PWM, gpio, dutyCycle, 0)) == null) {
			return PigpioConstants.ERROR;
		}
		
		return PigpioConstants.SUCCESS;
	}
	
	@Override
	public int getPWMRange(int gpio) {
		Message message = sendMessage(new Message(PI_CMD_PRG, gpio, 0, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}
	
	@Override
	public int setPWMRange(int gpio, int range) {
		if (sendMessage(new Message(PI_CMD_PRS, gpio, range, 0)) == null) {
			return PigpioConstants.ERROR;
		}
		
		return PigpioConstants.SUCCESS;
	}
	
	@Override
	public int getPWMRealRange(int gpio) {
		Message message = sendMessage(new Message(PI_CMD_PRRG, gpio, 0, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int getPWMFrequency(int gpio) {
		Message message = sendMessage(new Message(PI_CMD_PFG, gpio, 0, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}
	
	@Override
	public int setPWMFrequency(int gpio, int frequency) {
		if (sendMessage(new Message(PI_CMD_PFS, gpio, frequency, 0)) == null) {
			return PigpioConstants.ERROR;
		}
		
		return PigpioConstants.SUCCESS;
	}

	@Override
	public int setPullUpDown(int gpio, int pud) {
		if (sendMessage(new Message(PI_CMD_PUD, gpio, pud, 0)) == null) {
			return PigpioConstants.ERROR;
		}
		
		return PigpioConstants.SUCCESS;
	}

	@Override
	public int getServoPulseWidth(int gpio) {
		Message message = sendMessage(new Message(PI_CMD_GPW, gpio, 0, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int setServoPulseWidth(int gpio, int pulseWidth) {
		if (sendMessage(new Message(PI_CMD_SERVO, gpio, pulseWidth, 0)) == null) {
			return PigpioConstants.ERROR;
		}
		
		return PigpioConstants.SUCCESS;
	}

	@Override
	public int noiseFilter(int gpio, int steadyMs, int activeMs) {
		if (sendMessage(new Message(PI_CMD_FN, gpio, steadyMs, 4, new UIntMessageExtension(activeMs))) == null) {
			return PigpioConstants.ERROR;
		}
		
		return PigpioConstants.SUCCESS;
	}

	@Override
	public int glitchFilter(int gpio, int steadyMs) {
		if (sendMessage(new Message(PI_CMD_FG, gpio, steadyMs, 0)) == null) {
			return PigpioConstants.ERROR;
		}
		
		return PigpioConstants.SUCCESS;
	}

	@Override
	public int hardwareClock(int gpio, int clockFreq) {
		if (sendMessage(new Message(PI_CMD_HC, gpio, clockFreq, 0)) == null) {
			return PigpioConstants.ERROR;
		}
		
		return PigpioConstants.SUCCESS;
	}

	@Override
	public int hardwarePwm(int gpio, int pwmFreq, int pwmDuty) {
		if (sendMessage(new Message(PI_CMD_HP, gpio, pwmFreq, 4, new UIntMessageExtension(pwmDuty))) == null) {
			return PigpioConstants.ERROR;
		}
		
		return PigpioConstants.SUCCESS;
	}
	
	// I2C

	@Override
	public int i2cOpen(int i2cBus, int i2cAddr, int i2cFlags) {
		Message message = sendMessage(new Message(PI_CMD_I2CO, i2cBus, i2cAddr, 4, new UIntMessageExtension(i2cFlags)));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int i2cClose(int handle) {
		Message message = sendMessage(new Message(PI_CMD_I2CC, handle, 0, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int i2cReadByte(int handle) {
		Message message = sendMessage(new Message(PI_CMD_I2CRS, handle, 0, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int i2cWriteByte(int handle, int bVal) {
		Message message = sendMessage(new Message(PI_CMD_I2CWS, handle, bVal, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int i2cReadByteData(int handle, int i2cReg) {
		Message message = sendMessage(new Message(PI_CMD_I2CRB, handle, i2cReg, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int i2cWriteByteData(int handle, int i2cReg, int bVal) {
		Message message = sendMessage(new Message(PI_CMD_I2CWB, handle, i2cReg, 4, new UIntMessageExtension(bVal)));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int i2cReadWordData(int handle, int i2cReg) {
		Message message = sendMessage(new Message(PI_CMD_I2CRW, handle, i2cReg, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int i2cWriteWordData(int handle, int i2cReg, int wVal) {
		Message message = sendMessage(new Message(PI_CMD_I2CWW, handle, i2cReg, 4, new UIntMessageExtension(wVal)));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int i2cProcessCall(int handle, int i2cReg, int wVal) {
		Message message = sendMessage(new Message(PI_CMD_I2CPC, handle, i2cReg, 4, new UIntMessageExtension(wVal)));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int i2cReadBlockData(int handle, int i2cReg, byte[] buf) {
		//Message message = sendMessage(new Message(PI_CMD_I2CRK, handle, i2cReg, 0));
		throw new UnsupportedOperationException("Method not yet implemented: i2cReadBlockData");
	}

	@Override
	public int i2cWriteBlockData(int handle, int i2cReg, byte[] buf, int count) {
		Message message = sendMessage(new Message(PI_CMD_I2CWK, handle, 0, count, new ByteArrayMessageExtension(buf)));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int i2cReadI2CBlockData(int handle, int i2cReg, byte[] buf, int count) {
		//Message message = sendMessage(new Message(PI_CMD_I2CRI, handle, i2cReg, 4, new UIntMessageExtension(count)));
		throw new UnsupportedOperationException("Method not yet implemented: i2cReadI2CBlockData");
	}

	@Override
	public int i2cWriteI2CBlockData(int handle, int i2cReg, byte[] buf, int count) {
		Message message = sendMessage(new Message(PI_CMD_I2CWI, handle, 0, count, new ByteArrayMessageExtension(buf)));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int i2cReadDevice(int handle, byte[] buffer, int count) {
		// PI_CMD_I2CRD
		throw new UnsupportedOperationException("Method not yet implemented: i2cReadDevice");
	}

	@Override
	public int i2cWriteDevice(int handle, byte[] buffer, int count) {
		// PI_CMD_I2CWD
		throw new UnsupportedOperationException("Method not yet implemented: i2cWriteDevice");
	}

	
	// SPI

	@Override
	public int spiOpen(int spiChan, int baud, int spiFlags) {
		Message message = sendMessage(new Message(PI_CMD_SPIO, spiChan, baud, 4, new UIntMessageExtension(spiFlags)));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int spiClose(int handle) {
		Message message = sendMessage(new Message(PI_CMD_SPIC, handle, 0, 0));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int spiRead(int handle, byte[] buf, int count) {
		//Message message = sendMessage(new Message(PI_CMD_SPIR, handle, count, 0));
		throw new UnsupportedOperationException("Method not yet implemented: spiRead");
	}

	@Override
	public int spiWrite(int handle, byte[] buf, int count) {
		Message message = sendMessage(new Message(PI_CMD_SPIW, handle, 0, count, new ByteArrayMessageExtension(buf)));
		if (message == null) {
			return PigpioConstants.ERROR;
		}
		
		return (int) message.p3;
	}

	@Override
	public int spiXfer(int handle, byte[] txBuf, byte[] rxBuf, int count) {
		//Message message = sendMessage(new Message(PI_CMD_SPIX, handle, 0, txBuf.length, new ByteArrayMessageExtension(txBuf)));
		throw new UnsupportedOperationException("Method not yet implemented: spiXfer");
	}

	/*
	 * typedef struct {
	 *   uint32_t cmd;
	 *   uint32_t p1;
	 *   uint32_t p2;
	 *   union {
	 *     uint32_t p3;
	 *     uint32_t ext_len;
	 *     uint32_t res;
	 *   };
	 * } cmdCmd_t;
	 */
	static class Message {
		int cmd;
		long p1;
		long p2;
		long p3;
		MessageExtension extension;
		
		Message(int cmd, long p1, long p2, long p3) {
			this.cmd = cmd;
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
		}
		
		Message(int cmd, long p1, long p2, long p3, MessageExtension extension) {
			this.cmd = cmd;
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.extension = extension;
		}

		@Override
		public String toString() {
			return "PigpioDMessage [cmd=" + cmd + ", p1=" + p1 + ", p2=" + p2 + ", p3=" + p3 + ", extension="
					+ extension + "]";
		}
	}
	
	static interface MessageExtension {
		void encode(ByteBuf out);
	}
	
	static class UByteMessageExtension implements MessageExtension {
		short val;

		public UByteMessageExtension(short val) {
			this.val = val;
		}

		@Override
		public void encode(ByteBuf out) {
			out.writeByte(val);
		}

		@Override
		public String toString() {
			return "UByteMessageExtension [val=" + val + "]";
		}
	}
	
	static class UIntMessageExtension implements MessageExtension {
		long val;

		public UIntMessageExtension(long val) {
			this.val = val;
		}

		@Override
		public void encode(ByteBuf out) {
			out.writeIntLE((int) val);
		}

		@Override
		public String toString() {
			return "UIntMessageExtension [val=" + val + "]";
		}
	}
	
	static class ByteArrayMessageExtension implements MessageExtension {
		byte[] data;

		public ByteArrayMessageExtension(byte[] data) {
			this.data = data;
		}

		@Override
		public void encode(ByteBuf out) {
			out.writeBytes(data);
		}

		@Override
		public String toString() {
			return "ByteArrayMessageExtension [data length=" + data.length + "]";
		}
	}
	
	static class Decoder extends ByteToMessageDecoder {
		@Override
		protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) {
			if (in.readableBytes() < 4 * 4) {
				return;
			}
			
			out.add(new Message((int) in.readUnsignedIntLE(), in.readUnsignedIntLE(), in.readUnsignedIntLE(), in.readUnsignedIntLE()));
		}
	}
	
	static class Encoder extends MessageToByteEncoder<Message> {
		@Override
		protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
			out.writeIntLE(msg.cmd);
			out.writeIntLE((int) msg.p1);
			out.writeIntLE((int) msg.p2);
			out.writeIntLE((int) msg.p3);
			if (msg.extension != null) {
				msg.extension.encode(out);
			}
		}
	}
	
	static class Handler extends SimpleChannelInboundHandler<Message> {
		private PigpioSocket pigpiod;
		
		Handler(PigpioSocket pigpiod) {
			this.pigpiod = pigpiod;
		}
		
		@Override
		protected void channelRead0(ChannelHandlerContext context, Message msg) {
			pigpiod.messageReceived(msg);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
			System.out.println("exceptionCaught: " + cause);
			cause.printStackTrace();
			context.close();
		}
	}
}
