package org.erlymon.core.view;

import org.erlymon.core.model.data.DeviceSettings;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public interface DeviceActivityView extends View {
    void showData(DeviceSettings data);
    long getDeviceId();
    DeviceSettings getDevice();
}