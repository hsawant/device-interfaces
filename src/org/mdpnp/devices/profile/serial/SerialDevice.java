/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.serial;

import org.mdpnp.devices.profile.connected.ConnectedDevice;
import org.mdpnp.devices.profile.data.textarray.TextArray;
import org.mdpnp.devices.profile.data.textarray.TextArrayImpl;

public interface SerialDevice extends ConnectedDevice {

	TextArray SERIAL_PORTS = new TextArrayImpl(SerialDevice.class, "SERIAL_PORTS");

}
