package org.billthefarmer.tuner;

import android.app.Application;
import android.bluetooth.BluetoothGatt;

public class GlobalVariable extends Application {
    private boolean GlobalVar1;
    private boolean GlobalVar2;
    private BluetoothGatt GlobalVar3;

    public boolean getConnected() {
        return GlobalVar1;
    }

    public boolean getEchoInitialized() {
        return GlobalVar2;
    }

    public BluetoothGatt getGatt() {
        return GlobalVar3;
    }

    public void setConnected(boolean GlobalVar1) {
        this.GlobalVar1 = GlobalVar1;
    }

    public void setEchoInitialized(boolean GlobalVar2) {
        this.GlobalVar2 = GlobalVar2;
    }

    public void setGatt(BluetoothGatt GlobalVar3) {
        this.GlobalVar3 = GlobalVar3;
    }
}
