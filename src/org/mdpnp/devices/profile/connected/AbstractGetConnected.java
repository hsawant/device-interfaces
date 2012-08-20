/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.connected;

import org.mdpnp.devices.profile.Gateway;
import org.mdpnp.devices.profile.IdentifiableUpdate;
import org.mdpnp.devices.profile.Identifier;
import org.mdpnp.devices.profile.connected.ConnectedDevice.ConnectionType;
import org.mdpnp.devices.profile.connected.ConnectedDevice.State;
import org.mdpnp.devices.profile.data.enumeration.EnumerationUpdate;
import org.mdpnp.devices.profile.data.identifierarray.MutableIdentifierArrayUpdate;
import org.mdpnp.devices.profile.data.identifierarray.MutableIdentifierArrayUpdateImpl;
import org.mdpnp.devices.profile.data.text.MutableTextUpdate;
import org.mdpnp.devices.profile.data.text.MutableTextUpdateImpl;
import org.mdpnp.devices.profile.data.textarray.TextArrayUpdate;
import org.mdpnp.devices.profile.device.Device;
import org.mdpnp.devices.profile.device.DeviceListener;
import org.mdpnp.devices.profile.serial.SerialDevice;

public abstract class AbstractGetConnected implements DeviceListener {
	private boolean closing = false;
	private ConnectedDevice.State deviceState;
	private ConnectedDevice.ConnectionType deviceType;
	private String[] serialPorts;
	private String connectTo = null;
	private final Gateway gateway;
	
	public AbstractGetConnected(Gateway gateway) {
		this.gateway = gateway;
		gateway.addListener(this);
		
	}

	@Override
	public void update(IdentifiableUpdate<?> update) {
		if(ConnectedDevice.STATE.equals(update.getIdentifier())) {
			synchronized(this) {
				deviceState = (State) ((EnumerationUpdate)update).getValue();
				this.notifyAll();
			}
			issueConnect();
		} else if(SerialDevice.SERIAL_PORTS.equals(update.getIdentifier())) {
			serialPorts = ((TextArrayUpdate)update).getValue();
			issueConnect();
		} else if(ConnectedDevice.CONNECTION_TYPE.equals(update.getIdentifier())) {
			deviceType = (ConnectionType) ((EnumerationUpdate)update).getValue();
			issueConnect();
		}	
	}
	
	public void connect() {
		MutableIdentifierArrayUpdate miau = new MutableIdentifierArrayUpdateImpl(Device.REQUEST_IDENTIFIED_UPDATES);
		miau.setValue(new Identifier[] { Device.NAME, Device.GUID, SerialDevice.SERIAL_PORTS, ConnectedDevice.CONNECTION_TYPE, ConnectedDevice.STATE, Device.GET_AVAILABLE_IDENTIFIERS });
		gateway.update(miau);
	}
	
	public void disconnect() {
		long start = System.currentTimeMillis();
		closing = true;
		MutableTextUpdate disconnect = new MutableTextUpdateImpl(ConnectedDevice.DISCONNECT);
		disconnect.setValue("APP IS CLOSING");
		
		boolean disconnected = false;
		
		while(!disconnected) {
			gateway.update(disconnect);
			synchronized(this) {
				disconnected = ConnectedDevice.State.Disconnected.equals(deviceState);
				if(!disconnected) {
					if( (System.currentTimeMillis()-start) >= 10000L) {
						return;
					}
					try {
						this.wait(1000L);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		}
	}
	
	protected abstract void abortConnect();
	protected abstract String addressFromUser();
	protected abstract String addressFromUserList(String[] list);
	protected abstract boolean isFixedAddress();
	
	private void issueConnect() {
//		System.err.println("connectTo="+connectTo+" deviceType="+deviceType+" serialPorts="+serialPorts+" deviceState="+deviceState);
		if(null == connectTo && !closing && deviceType != null && (isFixedAddress() || serialPorts != null || !ConnectedDevice.ConnectionType.Serial.equals(deviceType)) && ConnectedDevice.State.Disconnected.equals(deviceState)) {
			switch(deviceType) {
			case Network:
				connectTo = addressFromUser();
				if(null == connectTo) {
					abortConnect();
					return;
				}
				break;
			case Serial:
				connectTo = addressFromUserList(serialPorts);
				if(null == connectTo) {
					abortConnect();
					return;
				}
				break;
			default:
				connectTo = "";
			}
			MutableTextUpdate mtu = new MutableTextUpdateImpl(ConnectedDevice.CONNECT_TO);
			mtu.setValue(connectTo);
			gateway.update(mtu);
		}
		
	}

}
