/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.data.enumeration;

import org.mdpnp.devices.profile.MutableIdentifiableUpdateImpl;

public class MutableEnumerationUpdateImpl extends MutableIdentifiableUpdateImpl<Enumeration> implements MutableEnumerationUpdate {

	private final Enumeration enumeration;
	private Enum<?> value;
	
	public MutableEnumerationUpdateImpl(Enumeration enumeration) {
		this.enumeration = enumeration;
	}
	
	@Override
	public Enum<?> getValue() {
		return value;
	}

	@Override
	public Enumeration getIdentifier() {
		return enumeration;
	}

	@Override
	public void setValue(Enum<?> e) {
		this.value = e;
	}
	@Override
	public String toString() {
		return "[identifier="+getIdentifier()+",source="+getSource()+",target="+getTarget()+",value="+value+"]";
	}

}
