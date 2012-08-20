/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mdpnp.devices.profile.Gateway;
import org.mdpnp.devices.profile.connected.AbstractConnectedDevice;
import org.mdpnp.devices.profile.data.textarray.MutableTextArrayUpdate;
import org.mdpnp.devices.profile.data.textarray.MutableTextArrayUpdateImpl;


public abstract class AbstractSerialDevice extends AbstractConnectedDevice implements SerialDevice, Runnable {

	protected final MutableTextArrayUpdate serialPortsUpdate = new MutableTextArrayUpdateImpl(SerialDevice.SERIAL_PORTS);
	
	protected abstract boolean doInitCommands(OutputStream outputStream) throws IOException;
	protected abstract void process(InputStream inputStream) throws IOException;
	
	protected State state = State.Disconnected;
	protected SerialSocket socket;
	protected Throwable lastError;
	
	protected SerialProvider serialProvider;
	
	public AbstractSerialDevice(Gateway gateway) {
		super(gateway);
		serialPortsUpdate.setValue(getSerialProvider().getPortNames().toArray(new String[0]));
		add(serialPortsUpdate);
	}
	
	public AbstractSerialDevice(Gateway gateway, SerialSocket sock) {
		super(gateway);
		this.socket = sock;
		this.portIdentifier = sock.getPortIdentifier();
	}
	
	public void setSerialProvider(SerialProvider serialProvider) {
		this.serialProvider = serialProvider;
	}
	
	public SerialProvider getSerialProvider() {
		if(null == serialProvider) {
			this.serialProvider = SerialProviderFactory.getDefaultProvider();
		}
		return serialProvider;
	}
	
	protected void setLastError(Throwable lastError) {
		this.lastError = lastError;
		lastError.printStackTrace();
	}
	
	public Throwable getLastError() {
		return lastError;
	}
	
	public synchronized State getState() {
		return state;
	};
	
	protected synchronized void setState(State state) {
		this.state = state;
		this.notifyAll();
	}
	
	public SerialSocket getSocket() {
		return this.socket;
	}
	
	@Override
	public void disconnect() {
		Thread t = currentThread;
		boolean close = false;
		synchronized(this) {
			switch(state){
			case Disconnected:
			case Disconnecting:
				return;
			case Connecting:
			case Connected:
			case Negotiating:
				this.state = State.Disconnecting;
				this.notifyAll();
				if(t != null) {
					t.interrupt();
				}
				close = true;
				
				break;
			}
		}
		if(close) {
			close();
		}
		stateUpdate.setValue(getState());
		gateway.update(stateUpdate);
	}
	private void close() {
		SerialSocket socket = this.socket;
		this.socket = null;
		if(socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				setLastError(e);
			}
		}
	}
	private Thread currentThread;
	
	private String portIdentifier;
	
	@Override
	public void connect(String portIdentifier) {
		synchronized(this) {
			this.portIdentifier = portIdentifier;
			
			while(State.Disconnecting.equals(state)) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			switch(state) {
			case Connected:
			case Negotiating:
			case Connecting:
				return;
			case Disconnected:
				this.state = State.Connecting;
				notifyAll();
				break;
			}
			currentThread = new Thread(this, "AbstractSerialDevice Processing");
			currentThread.setDaemon(true);
			currentThread.start();
		}

		stateUpdate.setValue(getState());
		gateway.update(stateUpdate);

	}
	
	public void run() {
		long previousAttempt = 0L;

		State state = State.Connecting;
		SerialSocket socket = null;
		
		while(State.Connecting.equals(state)) {
			long now = System.currentTimeMillis();
			
			// Read transientally
			state = this.state;
			
			// Not holding this lock 
			if(State.Connecting.equals(state) && now >= (previousAttempt+getConnectInterval())) {
				socket = getSerialProvider().connect(portIdentifier);
			}
			
			now = System.currentTimeMillis();
			
			synchronized(this) {
				state = this.state;
				if(socket != null) {
					if(State.Connecting.equals(state)) {
						// New successful socket in the Connecting state
						this.socket = socket;
						this.state = State.Negotiating;
						this.notifyAll();
						break;
					} else {
						// New socket, no longer Connecting
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						socket = null;
					}
				} else {
					if(State.Connecting.equals(state) && !Thread.interrupted() && now < (previousAttempt+getConnectInterval())) {
						try {
							this.wait( (previousAttempt+getConnectInterval()) - now);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
			
			
			previousAttempt = now;
		}
		
		
		synchronized(this) {
			while(State.Connecting.equals(state)) {
				long now = System.currentTimeMillis();
				
				if(now >= (previousAttempt+getConnectInterval())) {
					this.socket = getSerialProvider().connect(portIdentifier);
					if(socket != null) {
						this.state = State.Negotiating;
						notifyAll();
						break;
					}
				}
				previousAttempt = now;
	
				try {
					if(!Thread.interrupted()) {
						this.wait( (previousAttempt+getConnectInterval()) - now);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		stateUpdate.setValue(getState());
		gateway.update(stateUpdate);

		if(State.Negotiating.equals(getState())) {
			try {
				Thread t = new Thread(new Negotiation(socket.getOutputStream()), "Connection Parameters");
				t.setDaemon(true);
				t.start();
				process(socket.getInputStream());
			} catch(Exception e) {
				setLastError(e);
			}
		}		
		boolean reconnect = false;

		synchronized(this) {
			switch(state) {
			case Disconnecting:
				this.state = State.Disconnected;
				notifyAll();
				break;
			case Connected:
			case Connecting:
			case Negotiating:
				reconnect = true;
				this.state = State.Disconnected;
				notifyAll();
				break;
			}
		}
		close();
		stateUpdate.setValue(getState());
		gateway.update(stateUpdate);

		if(reconnect) {
			connect(portIdentifier);
		}
	}
	private class Negotiation implements Runnable {
		private OutputStream outputStream;
		Negotiation(OutputStream outputStream) {
			this.outputStream = outputStream;
		}
		public void run() {
			boolean inited;
			try {
				inited = doInitCommands(outputStream);
			} catch (IOException e) {
				setLastError(e);
				inited = false;
			}
			boolean close = false;
			synchronized(AbstractSerialDevice.this) {
				switch(AbstractSerialDevice.this.state) {
				case Negotiating:
					if(inited) {
						AbstractSerialDevice.this.state = State.Connected;
						AbstractSerialDevice.this.notifyAll();
					} else {
						AbstractSerialDevice.this.state = State.Connecting;
						AbstractSerialDevice.this.notifyAll();
						close = true;
					}
					
					break;
				default:
				}
			}
			if(close) {
				close();
			}
			stateUpdate.setValue(getState());
			gateway.update(stateUpdate);
		}
	}
	
	protected long getConnectInterval() {
		return 10000L;
	}
	private String connectionInfo;

	public String getConnectionInfo() {
		return connectionInfo;
	}
	@Override
	protected ConnectionType getConnectionType() {
		return ConnectionType.Serial;
	}
}
