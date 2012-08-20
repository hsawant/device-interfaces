/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.device;

import java.util.HashMap;
import java.util.Map;

import org.mdpnp.devices.profile.Gateway;
import org.mdpnp.devices.profile.IdentifiableUpdate;
import org.mdpnp.devices.profile.Identifier;
import org.mdpnp.devices.profile.data.identifierarray.IdentifierArrayUpdate;
import org.mdpnp.devices.profile.data.identifierarray.MutableIdentifierArrayUpdate;
import org.mdpnp.devices.profile.data.identifierarray.MutableIdentifierArrayUpdateImpl;
import org.mdpnp.devices.profile.data.text.MutableTextUpdate;
import org.mdpnp.devices.profile.data.text.MutableTextUpdateImpl;


public abstract class AbstractDevice implements Device {
	protected final Gateway gateway;
	protected final Map<Identifier, IdentifiableUpdate<?>> updates = new HashMap<Identifier, IdentifiableUpdate<?>>();
	protected final MutableTextUpdate nameUpdate = new MutableTextUpdateImpl(NAME);
	protected final MutableTextUpdate guidUpdate = new MutableTextUpdateImpl(GUID);
	
	protected void add(IdentifiableUpdate<?>... uu) {
		for(IdentifiableUpdate<?> u : uu) {
			add(u);
		}
	}
	
	protected void add(IdentifiableUpdate<?> u) {
		updates.put(u.getIdentifier(), u);
	}
	
	public AbstractDevice(Gateway gateway) {
		this.gateway = gateway;
		add(nameUpdate);
		add(guidUpdate);
		gateway.addListener(this);
	}
	
	protected IdentifiableUpdate<?> get(Identifier identifier) {
		return updates.get(identifier);		
	}
	
	@Override
	public void update(IdentifiableUpdate<?> command) {
		if(Device.REQUEST_IDENTIFIED_UPDATES.equals(command.getIdentifier())) {
			IdentifierArrayUpdate upds = (IdentifierArrayUpdate)command;
			for(Identifier i : upds.getValue()) {
				IdentifiableUpdate<?> iu = get(i);
				if(null != iu) {
					gateway.update(iu);
				}
			}
		} else if(Device.REQUEST_AVAILABLE_IDENTIFIERS.equals(command.getIdentifier())) {
			MutableIdentifierArrayUpdate upds = new MutableIdentifierArrayUpdateImpl(Device.GET_AVAILABLE_IDENTIFIERS);
			upds.setValue(this.updates.keySet().toArray(new Identifier[0]));
			gateway.update(upds);
		}
	}
	
}
