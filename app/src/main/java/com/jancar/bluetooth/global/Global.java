package com.jancar.bluetooth.global;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.model.Contact;

import java.util.List;
import java.util.UUID;

/**
 * @author suhy
 */
public class Global {
    public final static int TIMEOUT = 20000;
    private static Global mInstance = null;
    private static List<Contact> contactList;
    public final static int REQUEST_ENABLE_BT = 1;
    public final static String EXTRA_IS_COMING = "IS_COMING";
    public final static String EXTRA_NUMBER = "NUMBER";
    public final static String EXTRA_NAME = "NAME";
    public static int connStatus = 0;
    public final static int NOT_CONNECTED = 0;
    public final static int CONNECTING = 1;
    public final static int CONNECTED = 2;
    public static int scanStatus = 0;
    public final static int NOT_SCAN = 0;
    public final static int SCANNING = 1;
    public static int contactOrHistoryLoadStatus = 0;
    public final static int NOT_SCAN_COL = 0;
    public final static int SCAN_CONTACT = 1;
    public final static int SCAN_CALL_LOG = 2;
    public final static int REMOVE_CODE = 0;
    public final static int REFRESH_CODE = 1;


    private static UUID MY_UUID = UUID.fromString("00001105-0000-1000-8000-00805f9B34FB");
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

    public static String findNameByNumber(String number) {
        String name = MainApplication.getInstance().getString(R.string.str_unknown_call);
        if (contactList != null){
            for(Contact contact: contactList) {
                if (contact.getNumber().equals(number)) {
                    name = contact.getName();
                    break;
                }
            }
        }
        return name;
    }

    public static void setContactList(List<Contact> contactList) {
        Global.contactList = contactList;
    }

    public static List<Contact> getContactList() {
        return contactList;
    }

}
