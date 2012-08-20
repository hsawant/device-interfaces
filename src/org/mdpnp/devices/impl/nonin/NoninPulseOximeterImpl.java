/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.impl.nonin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.mdpnp.devices.profile.Gateway;
import org.mdpnp.devices.profile.data.numeric.MutableNumericUpdate;
import org.mdpnp.devices.profile.data.numeric.MutableNumericUpdateImpl;
import org.mdpnp.devices.profile.data.waveform.MutableWaveformUpdate;
import org.mdpnp.devices.profile.data.waveform.MutableWaveformUpdateImpl;
import org.mdpnp.devices.profile.serial.AbstractSerialDevice;
import org.mdpnp.devices.profile.technique.PulseOximeter;

public class NoninPulseOximeterImpl extends AbstractSerialDevice implements NoninPulseOximeter {

	public NoninPulseOximeterImpl(Gateway gateway) {
		super(gateway);
		add(pulseUpdate, spo2Update, plethUpdate);
		this.plethUpdate.setMillisecondsPerSample(1000.0 / (3.0 * Packet.FRAMES));
		this.plethUpdate.setValues(new Number[Packet.FRAMES]);

		nextArrival = new Arrival();
		Arrival current = null, previous = nextArrival;
		for (int i = 1; i < SAMPLE_SIZE; i++) {
			current = new Arrival();
			previous.setNext(current);
			previous = current;
		}
		current.setNext(nextArrival);
	
	}

	@Override
	public Boolean isArtifact() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getCurrentStatus().isArtifact();
	}

	@Override
	public Boolean isRedPerfusion() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getCurrentStatus().isRedPerfusion();
	}

	@Override
	public Boolean isGreenPerfusion() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getCurrentStatus().isGreenPerfusion();
	}

	@Override
	public Boolean isYellowPerfusion() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getCurrentStatus().isYellowPerfusion();
	}

	@Override
	public Integer getAvgHeartRateFourBeat() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getAvgHeartRateFourBeat();
	}

	@Override
	public Short getFirmwareRevision() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getFirmwareRevision();
	}

	@Override
	public Integer getTimer() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getTimer();
	}

	@Override
	public Boolean isSmartPoint() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.isSmartPoint();
	}

	@Override
	public Boolean isLowBattery() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.isLowBattery();
	}

	@Override
	public Short getAvgSpO2FourBeat() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getAvgSpO2FourBeat();
	}

	@Override
	public Short getAvgSpO2FourBeatFast() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getAvgSpO2FourBeatFast();
	}

	@Override
	public Short getSpO2BeatToBeat() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getSpO2BeatToBeat();
	}

	@Override
	public Integer getAvgHeartRateEightBeat() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getAvgHeartRateEightBeat();
	}

	@Override
	public Short getAvgSpO2EightBeat() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getAvgSpO2EightBeat();
	}

	@Override
	public Short getAvgSpO2EightBeatForDisplay() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getAvgSpO2EightBeatForDisplay();
	}

	@Override
	public Integer getAvgHeartRateFourBeatForDisplay() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getAvgHeartRateFourBeatForDisplay();
	}

	@Override
	public Integer getAvgHeartRateEightBeatForDisplay() {
		Packet packet = getCurrentPacket();
		return null == packet ? null : packet.getAvgHeartRateEightBeatForDisplay();
	}
	
	private final double MILLISECONDS_PER_SAMPLE = 1000.0 / 75.0;

	public Double getMillisecondsPerSample() {
		return MILLISECONDS_PER_SAMPLE;
	}

	private final Packet currentPacket = new Packet();

	private Arrival nextArrival;
	private static final int SAMPLE_SIZE = 30;

	private double packetsPerSecond;
	private final Status status = new Status();
	
	private final MutableNumericUpdate pulseUpdate = new MutableNumericUpdateImpl(PulseOximeter.PULSE);
	private final MutableNumericUpdate spo2Update = new MutableNumericUpdateImpl(PulseOximeter.SPO2);
	private final MutableWaveformUpdate plethUpdate = new MutableWaveformUpdateImpl(PulseOximeter.PLETH);
	
	private final Date date = new Date();
	public Date getTimestamp() {
		date.setTime(currentPacket.getFrameTime());
		return date;
	}
	
	protected static final byte OPCODE_SETFORMAT = 0x70;
	protected static final byte OPCODE_GETSERIAL = 0x74;
	protected static final byte OPCODE_RECVSERIAL = (byte) 0xF4;
	
	protected static void sendOperation(OutputStream outputStream, byte opCode, byte[] data) throws IOException {
		sendOperation(outputStream, opCode, data, 0, data.length);
	}
	
	protected static void sendOperation(OutputStream outputStream, byte opCode, byte[] data, int off, int len) throws IOException {
		byte[] buf = new byte[4 + len];
		buf[0] = 0x02;
		buf[1] = opCode;
		buf[2] = (byte) len;
		System.arraycopy(data, off, buf, 3, len);
		buf[3+len] = 0x03;
		
		outputStream.write(buf);
		outputStream.flush();
	}
	
	protected static void sendGetSerial(OutputStream outputStream) throws IOException {
		sendGetSerial(outputStream, 2);
	}
	
	protected static void sendGetSerial(OutputStream outputStream, int id) throws IOException {
		sendOperation(outputStream, OPCODE_GETSERIAL, new byte[] { (byte)id, (byte)id});
	}
	
	protected static void sendSetFormat(OutputStream outputStream, int format, boolean spotCheckMode, boolean bluetoothEnabledAtPowerOn) throws IOException {
		byte[] msg = new byte[] { 0x02, (byte)format, 0x01, (byte)(OPCODE_SETFORMAT + 4 + 2 + format) };
		msg[2] |= spotCheckMode ? 0x40 : 0x00;
		msg[2] |= bluetoothEnabledAtPowerOn ? 0x20 : 0x00;
		msg[3] += msg[2];
		sendOperation(outputStream, OPCODE_SETFORMAT, msg);
	}
	
	protected static void sendSetFormat(OutputStream outputStream, int format) throws IOException {
		byte[] msg = new byte[] { 0x02, (byte)format };
		sendOperation(outputStream, OPCODE_SETFORMAT, msg);
	}
	
	
	private static class Operation {
		byte opCode;
		byte[] msg;
	}
	
	private Boolean ackFlag;
	private Operation operationFlag;
	private boolean readyFlag = false;
	
	private synchronized String fetchSerial(OutputStream outputStream) throws IOException {
		String guid;
		this.operationFlag = null;
		long start = System.currentTimeMillis();
		
		while( (operationFlag == null) || (operationFlag.opCode != OPCODE_RECVSERIAL)) {
			this.operationFlag = null;
			if( (System.currentTimeMillis()-start) >= 10000L) {
				return null;
			}
			sendGetSerial(outputStream);
			try {
				this.wait(2000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		guid = new String(operationFlag.msg, 1, 9);
		this.operationFlag = null;
		this.notifyAll();
		return guid;
	}
	
	interface Formatter {
		void sendSetFormat(OutputStream outputStream) throws IOException;
	}
	
	private synchronized boolean setDataFormat(OutputStream outputStream, Formatter formatter) throws IOException {
		Boolean ack;
		
		this.ackFlag = null;
		while(null == ackFlag) {
			formatter.sendSetFormat(outputStream);
			try {
				this.wait(5000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ack = ackFlag;
		this.ackFlag = null;
		this.notifyAll();
		return ack;
	}

	private final Formatter onyxFormat = new Formatter() {
		@Override
		public void sendSetFormat(OutputStream outputStream) throws IOException {
			NoninPulseOximeterImpl.sendSetFormat(outputStream, 0x07);
		}
	};
	private final Formatter wristOxFormat = new Formatter() {
		public void sendSetFormat(OutputStream outputStream) throws IOException {
			NoninPulseOximeterImpl.sendSetFormat(outputStream, 0x07, true, true);
		};
	};
	
	public boolean doInitCommands(OutputStream outputStream) throws IOException {
		String guid = fetchSerial(outputStream);
		
		if(null == guid) {
			return false;
		}
		boolean ack;
		
	 	if(ack = setDataFormat(outputStream, onyxFormat)) {
	 		nameUpdate.setValue("Nonin Onyx II");
	 	} else if(ack = setDataFormat(outputStream, wristOxFormat)) {
	 		nameUpdate.setValue("Nonin WristOx2");
		}
	 	guidUpdate.setValue(guid);
	 	gateway.update(nameUpdate, guidUpdate);
	 	
		readyFlag = ack;
		return ack;
		
	}

	private static class Arrival {
		private Arrival next;
		private long tm;

		public void setNext(Arrival next) {
			this.next = next;
		}

		public void setTm(long tm) {
			this.tm = tm;
		}

		public Arrival getNext() {
			return next;
		}

		public long getTm() {
			return tm;
		}
	}
	
	protected synchronized void recvAcknowledged(boolean success) {
		ackFlag = success;
		this.notifyAll();
	}
	
	protected synchronized void recvOperation(byte opCode, byte[] source, int off, int len) {
//		System.out.println("Operation " + Integer.toHexString(0xFF&opCode) + " " + Util.bytesString(source, off, len));
		Operation op = new Operation();
		op.opCode = opCode;
		op.msg = new byte[len];
		System.arraycopy(source, off, op.msg, 0, len);
		this.operationFlag = op;
		this.notifyAll();
		
	}
	
	protected void frameError(String msg) {
		if(readyFlag) {
			System.err.println("frameError:"+msg);
		}
	}

	private final boolean consumeControl(byte[] buffer, int[] len) throws IOException {
		// Special control characters
		switch(buffer[0]) {
		case 0x02:
			// we don't yet know the size
			if(len[0] < 3) {
				return true;
			}
			int oplen = buffer[2] + 4;
			// we know the size and need more bytes
			if(len[0] < oplen) {
				return true;
			}
			// receive the operation
			recvOperation(buffer[1], buffer, 3, buffer[2]);
			
			System.arraycopy(buffer, oplen, buffer, 0, len[0] - oplen);
			len[0] -= oplen;
			return false;
		case 0x06:
			recvAcknowledged(true);
			System.arraycopy(buffer, 1, buffer, 0, len[0] - 1);
			len[0]--;
			return false;
		case 0x15:
			System.arraycopy(buffer, 1, buffer, 0, len[0] - 1);
			len[0]--;
			recvAcknowledged(false);
			return false;
		default:
			return false;
		}
	}
	
	private final boolean consumeFrame(byte[] buffer, int[] len) throws IOException {
		if(len[0] < Packet.FRAME_LENGTH) {
			return true;
		}

		if (expectNewPacket && !status.set(buffer[0]).isSync()) {
			frameError("RESYNC");
			System.arraycopy(buffer, Packet.FRAME_LENGTH, buffer, 0, len[0] - Packet.FRAME_LENGTH);
			len[0] -= Packet.FRAME_LENGTH;
			return false;
		}

		
		if(readyFlag) {
			if(Packet.validChecksum(buffer, 0)) {
		
				boolean packetComplete = currentPacket.setFrame(buffer, 0);
		
				if(packetComplete) {
					expectNewPacket = true;
					Number[] values = plethUpdate.getValues();
					for(int i = 0; i < Packet.FRAMES; i++) {
						values[i] = currentPacket.getPleth(i);
					}
					
					pulseUpdate.set(getHeartRate(), getTimestamp());
					spo2Update.set(getSpO2(), getTimestamp());
					
					gateway.update(pulseUpdate, spo2Update, plethUpdate);
					
					long now = System.currentTimeMillis();
		
					nextArrival.setTm(now);
		
					Arrival next = nextArrival.getNext();
					long nextTime = next.getTm();
					if (0L != nextTime) {
						double elapsed = (now - nextTime) / 1000.0;
						packetsPerSecond = 1.0 * SAMPLE_SIZE / elapsed;
					} else {
						packetsPerSecond = 0;
					}
		
					nextArrival = nextArrival.getNext();
				} else {
					expectNewPacket = false;
				}
			} else {
				frameError("invalid checksum");
			}
		}
		
		len[0] -= Packet.FRAME_LENGTH;
		System.arraycopy(buffer, Packet.FRAME_LENGTH, buffer, 0, len[0]);
		
		return false;

	}
	
	private final void consume(byte[] buffer, int[] len) throws IOException {
		while(len[0] > 0) {
			switch(buffer[0]) { 
			case 0x02:
			case 0x06:
			case 0x15:
				// return true to indicate more data are needed!
				if(consumeControl(buffer, len)) {
					return;
				}
				break;
			default:
				if(consumeFrame(buffer, len)) {
					return;
				}
				break;
			}
		}
	}
	
	private boolean expectNewPacket = false;

	protected void process(InputStream inputStream) throws IOException {
		
		byte[] buffer = new byte[Packet.LENGTH*3];
		int b;
		int[] len = new int[] {0};

		try {
		while (true) {
			b = inputStream.read(buffer, len[0], buffer.length - len[0]);
			
			// Read EOF, we're done
			if(b < 0) {
				return;
			} else {
				len[0] += b;
			}
			
			consume(buffer, len);
		}
		} finally {
			readyFlag = false;
		}
	}


	public double getPacketsPerSecond() {
		return packetsPerSecond;
	}

	public Integer getHeartRate() {
		Boolean sensorDetached = isSensorAlarm();
		if(null == sensorDetached) {
			return null; 
		} else {
			return sensorDetached ? null : currentPacket.getAvgHeartRateFourBeat();
		}
	}

	public Integer getSpO2() {
		Boolean sensorDetached = isSensorAlarm();
		if(null == sensorDetached) {
			return null;
		} else {
			return sensorDetached ? null : (int) currentPacket.getAvgSpO2FourBeat();
		}
	}

	public Boolean isOutOfTrack() {
		Packet packet = currentPacket;
		return null == packet ? null : packet.getCurrentStatus().isOutOfTrack();
	}

	public Boolean isSensorAlarm() {
		Packet packet = currentPacket;
		return null == packet ? null : packet.getCurrentStatus().isSensorAlarm();
	}

	public Packet getCurrentPacket() {
		return currentPacket;
	}
	

}
