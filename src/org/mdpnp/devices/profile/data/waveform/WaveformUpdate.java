/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.data.waveform;

import java.util.Date;

import org.mdpnp.devices.profile.IdentifiableUpdate;
import org.mdpnp.devices.profile.Persistent;

@Persistent
public interface WaveformUpdate extends IdentifiableUpdate<Waveform> {
	Number[] getValues();
//	Number getValue(int x);
//	Integer getCount();
	Date getTimestamp();
	Double getMillisecondsPerSample();
}
