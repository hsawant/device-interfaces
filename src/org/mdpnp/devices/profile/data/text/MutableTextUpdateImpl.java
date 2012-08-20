/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.data.text;

import org.mdpnp.devices.profile.MutableIdentifiableUpdateImpl;

public class MutableTextUpdateImpl extends MutableIdentifiableUpdateImpl<Text> implements MutableTextUpdate {
	private String value;
	private final Text text;
	
	public MutableTextUpdateImpl(Text text) {
		this.text = text;
	}
	
	public MutableTextUpdateImpl(Text text, String value) {
		this.text = text;
		this.value = value;
	}


	@Override
	public String getValue() {
		return value;
	}

	@Override
	public Text getIdentifier() {
		return text;
	}

	@Override
	public boolean setValue(String value) {
		if(null == value) {
			if(null == this.value) {
				return false;
			}
		} else {
			if(null != this.value) {
				if(this.value.equals(value)) {
					return false;
				}
			}
		}
		this.value = value;
		return true;
	}
	
	@Override
	public String toString() {
		return "[identifier="+getIdentifier()+",source="+getSource()+"target="+getTarget()+",value="+this.value+"]";
	}
}
