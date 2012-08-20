/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.profile.technique;

import org.mdpnp.devices.profile.data.numeric.Numeric;
import org.mdpnp.devices.profile.data.numeric.NumericImpl;
import org.mdpnp.devices.profile.data.numeric.UnitCode;
import org.mdpnp.devices.profile.data.waveform.Waveform;
import org.mdpnp.devices.profile.data.waveform.WaveformImpl;
import org.mdpnp.devices.profile.device.Device;

public interface PulseOximeter extends Device {
	Numeric PULSE = new NumericImpl(PulseOximeter.class, "PULSE", UnitCode.BEATS_PER_MINUTE, null);
	Numeric SPO2  = new NumericImpl(PulseOximeter.class, "SPO2" , UnitCode.SAT_PERIPHERAL_OXYGEN, null);
	
	Numeric PULSE_LOWER = new NumericImpl(PulseOximeter.class, "PULSE_LOWER", UnitCode.BEATS_PER_MINUTE, null);
	Numeric PULSE_UPPER = new NumericImpl(PulseOximeter.class, "PULSE_UPPER", UnitCode.BEATS_PER_MINUTE, null);
	
	Numeric SPO2_LOWER = new NumericImpl(PulseOximeter.class, "SPO2_LOWER", UnitCode.SAT_PERIPHERAL_OXYGEN, null);
	Numeric SPO2_UPPER = new NumericImpl(PulseOximeter.class, "SPO2_UPPER", UnitCode.SAT_PERIPHERAL_OXYGEN, null);
	
	Waveform PLETH = new WaveformImpl(PulseOximeter.class,  "PLETH", UnitCode.NONE);
	

}
