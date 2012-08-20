/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.impl.nonin;

import org.mdpnp.devices.profile.technique.PulseOximeter;

public interface NoninPulseOximeter extends PulseOximeter {
	Boolean isArtifact();
	Boolean isOutOfTrack();
	Boolean isSensorAlarm();
	Boolean isRedPerfusion();
	Boolean isGreenPerfusion();
	Boolean isYellowPerfusion();
	
	Integer getAvgHeartRateFourBeat();
	Short getFirmwareRevision();
	Integer getTimer();
	Boolean isSmartPoint();
	Boolean isLowBattery();
	Short getAvgSpO2FourBeat();
	Short getAvgSpO2FourBeatFast();
	Short getSpO2BeatToBeat();
	Integer getAvgHeartRateEightBeat();
	Short getAvgSpO2EightBeat();
	Short getAvgSpO2EightBeatForDisplay();
	Integer getAvgHeartRateFourBeatForDisplay();
	Integer getAvgHeartRateEightBeatForDisplay();

}
