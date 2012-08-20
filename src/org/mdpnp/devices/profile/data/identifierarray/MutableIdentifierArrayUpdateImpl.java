/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.data.identifierarray;

import java.util.Arrays;

import org.mdpnp.devices.profile.Identifier;
import org.mdpnp.devices.profile.MutableIdentifiableUpdateImpl;

public class MutableIdentifierArrayUpdateImpl extends MutableIdentifiableUpdateImpl<IdentifierArray> implements MutableIdentifierArrayUpdate {
	private Identifier[] value;
	private final IdentifierArray identifierArray;
	
	public MutableIdentifierArrayUpdateImpl(IdentifierArray identifierArray) {
		this.identifierArray = identifierArray;
	}
	
	@Override
	public Identifier[] getValue() {
		return value;
	}

	@Override
	public IdentifierArray getIdentifier() {
		return identifierArray;
	}

	@Override
	public void setValue(Identifier[] value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "[identifier="+getIdentifier()+",source="+getSource()+"target="+getTarget()+"value="+Arrays.toString(value)+"]";
	}

}
