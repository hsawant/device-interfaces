/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.connected;

import org.mdpnp.devices.profile.data.enumeration.Enumeration;
import org.mdpnp.devices.profile.data.enumeration.EnumerationImpl;
import org.mdpnp.devices.profile.data.text.Text;
import org.mdpnp.devices.profile.data.text.TextImpl;
import org.mdpnp.devices.profile.device.Device;

public interface ConnectedDevice extends Device {
	enum State {
		Connected,
		Connecting,
		Negotiating,
		Disconnecting,
		Disconnected,
	}
	
	Enumeration STATE = new EnumerationImpl(ConnectedDevice.class, "STATE");
	Text CONNECTION_INFO = new TextImpl(ConnectedDevice.class, "CONNECTION_INFO");
	enum ConnectionType {
		Serial,
		Simulated,
		Network
	}
	
	Enumeration CONNECTION_TYPE = new EnumerationImpl(ConnectedDevice.class, "CONNECTION_TYPE");
	
	Text CONNECT_TO = new TextImpl(ConnectedDevice.class, "CONNECT_TO");
	Text DISCONNECT = new TextImpl(ConnectedDevice.class, "DISCONNECT");

}
