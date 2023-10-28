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
        if (mInstance == null){
            synchronized (Global.class) {
                if (mInstance == null) {
                    mInstance = new Global();
                }
            }
        }
        return mInstance;
    }

    public final int REQUEST_ENABLE_BT = 1;
    public final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
}
