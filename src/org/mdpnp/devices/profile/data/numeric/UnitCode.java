/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.data.numeric;

import org.mdpnp.devices.profile.Identifier;

public interface UnitCode extends Identifier {
	UnitCode PERCENT = new UnitCodeImpl(UnitCode.class, "PERCENT", "Percent", "%");
	UnitCode BEATS_PER_MINUTE = new UnitCodeImpl(UnitCode.class, "BEATS_PER_MINUTE", "Beats Per Minute", "BPM");
	UnitCode SAT_PERIPHERAL_OXYGEN = new UnitCodeImpl(UnitCode.class, "SAT_PERIPHERAL_OXYGEN", "Saturation of peripheral oxygen", "SpO2");
	UnitCode NONE = new UnitCodeImpl(UnitCode.class, "NONE", "", "");
	UnitCode MM_HG = new UnitCodeImpl(UnitCode.class, "MM_HG", "millimeters of mercury", "mmHg");
	UnitCode MILLISECONDS = new UnitCodeImpl(UnitCode.class, "MILLISECONDS", "milliseconds", "ms");
	
	String getDisplayName();
	String getDisplaySymbol();
}
