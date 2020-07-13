package com.mathrithms.mShare.callback;

import com.mathrithms.mShare.object.NetworkDevice;

import java.util.List;

public interface OnDeviceSelectedListener
{
    void onDeviceSelected(NetworkDevice.Connection connection, List<NetworkDevice.Connection> availableInterfaces);
}
