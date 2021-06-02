package uk.pigpioj;

public class PiI2CMessage {
	private int addr; // slave address
	private int flags;
	private int len; // msg length

	public PiI2CMessage(int addr, int flags, int len) {
		this.addr = addr;
		this.flags = flags;
		this.len = len;
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
}
