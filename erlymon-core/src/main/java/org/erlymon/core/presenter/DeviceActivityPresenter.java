package org.erlymon.core.presenter;


import org.erlymon.core.model.data.DeviceSettings;
import org.erlymon.core.view.DeviceActivityView;

import rx.Observer;
import rx.Subscription;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class DeviceActivityPresenter extends BasePresenter {
    private DeviceActivityView view;
    public DeviceActivityPresenter(DeviceActivityView view) {
        this.view = view;
    }

    public void onSaveButtonClick() {
        if (view.getDeviceId() == 0) {
            Subscription subscription = model.createDeviceSettings(view.getDevice())
                    .subscribe(new Observer<DeviceSettings>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            view.showError(e.getMessage());
                        }

                        @Override
                        public void onNext(DeviceSettings data) {
                            view.showData(data);
                        }
                    });
            addSubscription(subscription);
        } else {
            Subscription subscription = model.updateDeviceSettings(view.getDeviceId(), view.getDevice())
                    .subscribe(new Observer<DeviceSettings>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            view.showError(e.getMessage());
                        }

                        @Override
                        public void onNext(DeviceSettings data) {
                            view.showData(data);
                        }
                    });
            addSubscription(subscription);
        }
    }
}