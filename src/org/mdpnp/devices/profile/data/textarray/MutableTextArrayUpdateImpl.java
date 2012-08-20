/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.data.textarray;

import java.util.Arrays;

import org.mdpnp.devices.profile.MutableIdentifiableUpdateImpl;

public class MutableTextArrayUpdateImpl extends MutableIdentifiableUpdateImpl<TextArray> implements MutableTextArrayUpdate {
	private final TextArray textArray;
	
	public MutableTextArrayUpdateImpl(TextArray textArray) {
		this.textArray = textArray;
	}
	
	@Override
	public TextArray getIdentifier() {
		return textArray;
	}
	
	private String[] value;
	@Override
	public String[] getValue() {
		return value;
	}

	@Override
	public void setValue(String[] value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "[identifier="+getIdentifier()+",source="+getSource()+",target="+getTarget()+",value="+Arrays.toString(value)+"]";
	}

}
