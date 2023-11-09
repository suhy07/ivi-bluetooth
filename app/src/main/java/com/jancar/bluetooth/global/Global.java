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
    private static Global mInstance = null;
    private static List<Contact> contactList;
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

    public final static int REQUEST_ENABLE_BT = 1;
    public final static String EXTRA_IS_COMING = "IS_COMING";
    public final static String EXTRA_NUMBER = "NUMBER";
    public final static String EXTRA_NAME = "NAME";
    private static UUID MY_UUID = UUID.fromString("00001105-0000-1000-8000-00805f9B34FB");

}
