package org.billthefarmer.tuner;

import java.util.UUID;

public class Constants {

    public static String SERVICE_STRING = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
    public static UUID SERVICE_UUID = UUID.fromString(SERVICE_STRING);

    public static String CHARACTERISTIC_ECHO_STRING = "beb5483e-36e1-4688-b7f5-ea07361b26a8";
    public static UUID CHARACTERISTIC_ECHO_UUID = UUID.fromString(CHARACTERISTIC_ECHO_STRING);

    public static final long SCAN_PERIOD = 5000;
}
