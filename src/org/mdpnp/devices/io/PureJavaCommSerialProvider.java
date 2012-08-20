/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.mdpnp.devices.profile.serial.SerialProvider;
import org.mdpnp.devices.profile.serial.SerialSocket;

import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class PureJavaCommSerialProvider implements SerialProvider {
	private static class SocketImpl implements SerialSocket {
		private final SerialPort serialPort;
		private final String portIdentifier;
		public SocketImpl(SerialPort serialPort, String portIdentifier) {
			this.portIdentifier = portIdentifier;
			this.serialPort = serialPort;
		}
		@Override
		public InputStream getInputStream() throws IOException {
			return serialPort.getInputStream();
		}
		@Override
		public OutputStream getOutputStream() throws IOException {
			return serialPort.getOutputStream();
		}
		@Override
		public void close() throws IOException {
			serialPort.close();
		}
		
		@Override
		public String getPortIdentifier() {
			return portIdentifier;
		}
	}
	
	public List<String> getPortNames() {
		List<String> list = new ArrayList<String>();
		Enumeration<?> e = purejavacomm.CommPortIdentifier.getPortIdentifiers();
		while(e.hasMoreElements()) {
			Object o = e.nextElement();
			if(o instanceof purejavacomm.CommPortIdentifier) {
				list.add( ((purejavacomm.CommPortIdentifier)o).getName() );
			}
		}
		Collections.sort(list);
		return list;
	}
	
	protected void doConfigurePort(SerialPort serialPort) throws UnsupportedCommOperationException {
		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		// Ensures no returns of 0 bytes
		serialPort.enableReceiveThreshold(1);
	}
	
	protected int getConnectTimeout() {
		return 10000;
	}
	
	public SerialSocket connect(String portIdentifier) {
		try {
			Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
			while(e.hasMoreElements()) {
				CommPortIdentifier cpi = (CommPortIdentifier) e.nextElement();
				if(cpi.getName().equals(portIdentifier)) {
					SerialPort serialPort = (SerialPort) cpi.open("", getConnectTimeout());
					doConfigurePort(serialPort);
					return new SocketImpl(serialPort, portIdentifier);
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
}
