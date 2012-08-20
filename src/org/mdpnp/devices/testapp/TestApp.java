package org.mdpnp.devices.testapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.mdpnp.devices.impl.nonin.NoninPulseOximeterImpl;
import org.mdpnp.devices.profile.Gateway;
import org.mdpnp.devices.profile.IdentifiableUpdate;
import org.mdpnp.devices.profile.connected.AbstractGetConnected;
import org.mdpnp.devices.profile.device.DeviceListener;

public class TestApp {
	public static void main(final String[] args) throws IOException {
		if(args.length < 1) {
			System.err.println("Please specify serial port");
			return;
		}
		Gateway gateway = new Gateway();
		new NoninPulseOximeterImpl(gateway);
		AbstractGetConnected getConnected = new AbstractGetConnected(gateway) {

			@Override
			protected void abortConnect() {
				System.exit(0);
			}

			@Override
			protected String addressFromUser() {
				return args[0];
			}

			@Override
			protected String addressFromUserList(String[] list) {
				return args[0];
			}

			@Override
			protected boolean isFixedAddress() {
				return true;
			}
			
		};
		
		gateway.addListener(new DeviceListener() {

			@Override
			public void update(IdentifiableUpdate<?> update) {
				System.out.println(update);
			}
			
		});
		
		getConnected.connect();
		
		System.err.println("Press <Enter> to quit");
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		br.readLine();
		
		getConnected.disconnect();
		
	}
}
