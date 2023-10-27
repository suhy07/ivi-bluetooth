package com.jancar.bluetooth.global;

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
}
