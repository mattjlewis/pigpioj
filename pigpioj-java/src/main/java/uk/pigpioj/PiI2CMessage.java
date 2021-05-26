package uk.pigpioj;

public class PiI2CMessage {
	private int addr; // slave address
	private int flags;
	private int len; // msg length
	private byte[] buf; // pointer to msg data

	public PiI2CMessage(int addr, int flags, int len, byte[] buf) {
		this.addr = addr;
		this.flags = flags;
		this.len = len;
		this.buf = buf;
	}

	public int getAddr() {
		return addr;
	}

	public int getFlags() {
		return flags;
	}

	public int getLen() {
		return len;
	}

	public byte[] getBuf() {
		return buf;
	}
}
