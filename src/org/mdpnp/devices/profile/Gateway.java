/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile;

import java.util.Collection;

import org.mdpnp.devices.profile.device.DeviceListener;

public class Gateway implements DeviceListener {
//	private static final Gateway instance = new Gateway();
	
	public Gateway() {
		
	}
	
//	public static final Gateway getInstance() {
//		return instance;
//	}
	
	public void addListener(DeviceListener listener) {
		listeners.add(listener);
	}
	public void removeListener(DeviceListener listener) {
		listeners.remove(listener);
	}
	private final java.util.Set<DeviceListener> listeners = new java.util.concurrent.CopyOnWriteArraySet<DeviceListener>();

	@Override
	public void update(IdentifiableUpdate<?> update) {
//		System.err.println("DEBUG:"+update);
		for(DeviceListener listener : listeners) {
			listener.update(update);
		}
	}
	
	public void update(Collection<? extends IdentifiableUpdate<?>> updates) {
		for(IdentifiableUpdate<?> update : updates) {
			update(update);
		}
	}

	public void update(IdentifiableUpdate<?>... updates) {
		for(IdentifiableUpdate<?> update : updates) {
			update(update);
		}
	}
	
}
