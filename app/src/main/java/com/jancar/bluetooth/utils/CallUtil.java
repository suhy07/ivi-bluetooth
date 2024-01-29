package com.jancar.bluetooth.utils;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;

import com.jancar.bluetooth.MainApplication;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;
import com.jancar.sdk.utils.Logcat;

import java.util.List;

public class CallUtil {

    private static CallUtil mCallUtil = null;

    private Handler mHandler;

    private BluetoothAdapter bluetoothAdapter;

    private String connectingHfpMac = "";
    private String connectingA2dpMac = "";

    private int a2dpStatus;
    private int a2dpSinkStatus;
    private int hfpStatus;

    private BluetoothHeadsetClient mBluetoothHeadsetClient;
    private BluetoothA2dp mBluetoothA2dp;
    private BluetoothA2dpSink mBluetoothA2dpSink;


    private CallUtil() {

        mHandler = new Handler();
        init();
        bluetoothAdapter.getProfileProxy(MainApplication.getInstance(), mServiceListener,BluetoothProfile.A2DP);
        bluetoothAdapter.getProfileProxy(MainApplication.getInstance(), mServiceListener,BluetoothProfile.A2DP_SINK);
        bluetoothAdapter.getProfileProxy(MainApplication.getInstance(), mServiceListener,BluetoothProfile.HEADSET_CLIENT);
    }

    public void init(){

        connectingHfpMac = "";
        connectingA2dpMac = "";

        if(bluetoothAdapter == null){
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        a2dpStatus = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
        a2dpSinkStatus = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP_SINK);
        hfpStatus = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET_CLIENT);

        Logcat.d("init a2dpStatus:"+a2dpStatus+" hfpStatus:"+hfpStatus+" a2dpSinkStatus:"+a2dpSinkStatus);

    }

    public int getA2dpStatus() {
        return a2dpStatus;
    }

    public void setA2dpStatus(int a2dpStatus) {
        this.a2dpStatus = a2dpStatus;
    }

    public int getHfpStatus() {
        return hfpStatus;
    }

    public void setHfpStatus(int hfpStatus) {
        this.hfpStatus = hfpStatus;
    }

    public int getA2dpSinkStatus() {
        return a2dpSinkStatus;
    }

    public void setA2dpSinkStatus(int a2dpSinkStatus) {
        this.a2dpSinkStatus = a2dpSinkStatus;
    }

    public boolean isConnected(){
        if(isBluetoothSendStatus()){
            return a2dpStatus == BluetoothProfile.STATE_CONNECTED|| hfpStatus == BluetoothProfile.STATE_CONNECTED;
        }else{
            return a2dpSinkStatus == BluetoothProfile.STATE_CONNECTED || hfpStatus == BluetoothProfile.STATE_CONNECTED;
        }
    }

    public boolean canPlayBluetoothMusic(){
        if(isBluetoothSendStatus()){
            return false;
        }
        return a2dpSinkStatus == BluetoothProfile.STATE_CONNECTED;
    }

    public boolean isDeviceConnected(BluetoothDevice device){
        if(!device.isConnected()){
            return false;
        }

        boolean isConnected;

        if(mBluetoothHeadsetClient!=null){
            isConnected = mBluetoothHeadsetClient.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED;
            Logcat.d(device.getName()+" "+device.getAddress()+" hfp:"+isConnected);
            if(isConnected){
                return true;
            }
        }
        if(isBluetoothSendStatus()){
            if(mBluetoothA2dp!=null){
                isConnected = mBluetoothA2dp.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED;
                Logcat.d(device.getName()+" "+device.getAddress()+" a2dp:"+isConnected);
                if(isConnected){
                    return true;
                }
            }
        }else{
            if(mBluetoothA2dpSink!=null){
                isConnected = mBluetoothA2dpSink.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED;
                Logcat.d(device.getName()+" "+device.getAddress()+" a2dpSink:"+isConnected);
                if(isConnected){
                    return true;
                }
            }
        }
        return false;

    }

    public boolean isDeviceConnecting(BluetoothDevice device){
        String mac = device.getAddress();
        return mac.equals(connectingA2dpMac)||mac.equals(connectingHfpMac);
    }

    public boolean isConnecting(){
        return !connectingA2dpMac.equals("")||!connectingHfpMac.equals("");
    }

    public boolean canCallNumber(){
        if(isBluetoothSendStatus()){
            return false;
        }
        return hfpStatus == BluetoothProfile.STATE_CONNECTED;
    }

    public static boolean isBluetoothSendStatus(){
        String value = SystemProperties.get("persist.atc.bt.a2dpsourcerole","");
        Logcat.i("value:"+value);
        return value!=null && value.equals("enable");
    }

    public static CallUtil getInstance() {
        if (mCallUtil == null) {
            synchronized (CallUtil.class) {
                if (mCallUtil == null) {
                    mCallUtil = new CallUtil();
                }
            }
        }
        return mCallUtil;
    }

    private String callNumber = "";

    private String callName = "";

    private int callStatus = IVIBluetooth.CallStatus.HANGUP;

    private int lastCallStatus = IVIBluetooth.CallStatus.HANGUP;

    private int callTime = 0;

    private  boolean isVoiceInCar = true;

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public int getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(int callStatus) {
        this.callStatus = callStatus;
        switch(callStatus){
            case IVIBluetooth.CallStatus.TALKING:
                if(lastCallStatus!=callStatus){
                    mHandler.removeCallbacks(updateTimeRunnable);
                    callTime = 0;
                    mHandler.postDelayed(updateTimeRunnable,1000);
                }
                break;
            case IVIBluetooth.CallStatus.HANGUP:
                mHandler.removeCallbacks(updateTimeRunnable);
                callTime = 0;
                break;
        }


        lastCallStatus = callStatus;
    }

    public void listenPhone() {
        BluetoothManager bluetoothManager = MainApplication.getInstance().getBluetoothManager();
        if(bluetoothManager!=null){
            bluetoothManager.listenPhone(bluetoothExecCallback);
        }
    }

    public void hangupPhone() {
        BluetoothManager bluetoothManager = MainApplication.getInstance().getBluetoothManager();
        if(bluetoothManager!=null){
            bluetoothManager.hangPhone(bluetoothExecCallback);
        }
    }

    public void callPhone(String number){
        BluetoothManager bluetoothManager = MainApplication.getInstance().getBluetoothManager();
        if(bluetoothManager!=null){
            bluetoothManager.callPhone(number,bluetoothExecCallback);
        }
    }

    public void switchVoice() {
        BluetoothManager bluetoothManager = MainApplication.getInstance().getBluetoothManager();
        if(bluetoothManager!=null){
            bluetoothManager.transferCall(bluetoothExecCallback);
        }
    }

    public void requestDTMF(int code) {

        BluetoothManager bluetoothManager = MainApplication.getInstance().getBluetoothManager();
        if(bluetoothManager!=null){
            bluetoothManager.requestDTMF(code, bluetoothExecCallback);
        }

    }

    IBluetoothExecCallback.Stub bluetoothExecCallback = new IBluetoothExecCallback.Stub() {
        @Override
        public void onSuccess(String msg) throws RemoteException {

        }

        @Override
        public void onFailure(int errorCode) throws RemoteException {

        }
    };

    public boolean isVoiceInCar() {
        return isVoiceInCar;
    }

    public void setVoiceInCar(boolean voiceInCar) {
        isVoiceInCar = voiceInCar;
    }

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            callTime++;
            mHandler.postDelayed(updateTimeRunnable,1000);
        }
    };

    private String timeToString(int s) {
        return s >= 10 ? ("" + s) : ("0" + s);
    }

    public String getCallingTime(int count) {
        int min = count / 60 % 60;
        int sec = count % 60;
        int h = count / 60 / 60;
        if (h > 0)
            return (timeToString(h) + ":" + timeToString(min) + ":" + timeToString(sec));

        return (timeToString(min) + ":" + timeToString(sec));
    }

    public int getCallTime() {
        return callTime;
    }

    public void setCallTime(int callTime) {
        this.callTime = callTime;
    }

    private BluetoothProfile.ServiceListener mServiceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Logcat.i("profile:"+profile);
            if(profile == BluetoothProfile.A2DP){
                 mBluetoothA2dp = (BluetoothA2dp)proxy;
            }else if(profile == BluetoothProfile.HEADSET_CLIENT){
                 mBluetoothHeadsetClient = (BluetoothHeadsetClient)proxy;
            }else if(profile == BluetoothProfile.A2DP_SINK){
                mBluetoothA2dpSink = (BluetoothA2dpSink)proxy;
            }

        }

        @Override
        public void onServiceDisconnected(int profile) {
            Logcat.i("profile:"+profile);
            if(profile == BluetoothProfile.A2DP){
                mBluetoothA2dp = null;
            }else if(profile == BluetoothProfile.HEADSET_CLIENT){
                mBluetoothHeadsetClient = null;
            }else if(profile == BluetoothProfile.A2DP_SINK){
                mBluetoothA2dpSink = null;
            }
        }
    };

    public String getConnectingHfpMac() {
        return connectingHfpMac;
    }

    public void setConnectingHfpMac(String connectingHfpMac) {
        this.connectingHfpMac = connectingHfpMac;
    }

    public String getConnectingA2dpMac() {
        return connectingA2dpMac;
    }

    public void setConnectingA2dpMac(String connectingA2dpMac) {
        this.connectingA2dpMac = connectingA2dpMac;
    }

    public boolean isPairing(List<BluetoothDevice> devices) {
        for (BluetoothDevice device : devices) {
            if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                return true;
            }
        }
        return false;
    }
}
