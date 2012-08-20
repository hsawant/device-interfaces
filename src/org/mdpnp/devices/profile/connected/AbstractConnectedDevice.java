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
import org.mdpnp.devices.profile.data.enumeration.MutableEnumerationUpdate;
import org.mdpnp.devices.profile.data.enumeration.MutableEnumerationUpdateImpl;
import org.mdpnp.devices.profile.data.text.MutableTextUpdate;
import org.mdpnp.devices.profile.data.text.MutableTextUpdateImpl;
import org.mdpnp.devices.profile.data.text.TextUpdate;
import org.mdpnp.devices.profile.device.AbstractDevice;

public abstract class AbstractConnectedDevice extends AbstractDevice implements ConnectedDevice {
	protected MutableTextUpdate connectionInfoUpdate = new MutableTextUpdateImpl(CONNECTION_INFO);
	protected MutableEnumerationUpdate stateUpdate = new MutableEnumerationUpdateImpl(STATE);
	protected MutableEnumerationUpdate connectionTypeUpdate = new MutableEnumerationUpdateImpl(CONNECTION_TYPE);
	
	public AbstractConnectedDevice(Gateway gateway) {
		super(gateway);
		add(connectionInfoUpdate);
		add(stateUpdate);
		add(connectionTypeUpdate);
		connectionTypeUpdate.setValue(getConnectionType());
		stateUpdate.setValue(State.Disconnected);
	}
	
	protected abstract void connect(String str);
	protected abstract void disconnect();
	protected abstract ConnectionType getConnectionType();
	
	@Override
	public void update(IdentifiableUpdate<?> command) {	
		if(ConnectedDevice.CONNECT_TO.equals(command.getIdentifier())) {
			connect(( (TextUpdate)command).getValue());
		} else if(ConnectedDevice.DISCONNECT.equals(command.getIdentifier())) {
			disconnect();
		} else {
			super.update(command);
		}
	}
}
