/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.device;

import org.mdpnp.devices.profile.data.identifierarray.IdentifierArray;
import org.mdpnp.devices.profile.data.identifierarray.IdentifierArrayImpl;
import org.mdpnp.devices.profile.data.text.Text;
import org.mdpnp.devices.profile.data.text.TextImpl;

public interface Device extends DeviceListener {
	Text NAME = new TextImpl(Device.class, "NAME");
	Text GUID = new TextImpl(Device.class, "GUID");
	
	IdentifierArray REQUEST_IDENTIFIED_UPDATES = new IdentifierArrayImpl(Device.class, "REQUEST_IDENTIFIED_UPDATES");
	
	Text REQUEST_AVAILABLE_IDENTIFIERS = new TextImpl(Device.class, "REQUEST_AVAILABLE_IDENTIFIERS");
	
	IdentifierArray GET_AVAILABLE_IDENTIFIERS = new IdentifierArrayImpl(Device.class, "GET_AVAILABLE_IDENTIFIERS");
}
