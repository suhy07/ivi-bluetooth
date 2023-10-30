package com.jancar.bluetooth.global;

import java.util.UUID;

/**
 * @author suhy
 */
public class Global {
    private static Global mInstance = null;
    private Global() {

    }

    public static Global getInstance() {
        if (mInstance == null) {
            synchronized (Global.class) {
                if (mInstance == null) {
                    mInstance = new Global();
                }
            }
        }
        return mInstance;
    }

    public static UUID getUUID() {
        if (MY_UUID == null) {
            MY_UUID = UUID.randomUUID();
        }
        return MY_UUID;
    }
    public final static int REQUEST_ENABLE_BT = 1;
    private static UUID MY_UUID;
}
