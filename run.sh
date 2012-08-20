#!/bin/sh

java -cp bin:lib/purejavacomm.jar:lib/jna.jar:lib/platform.jar org.mdpnp.devices.testapp.TestApp $1
