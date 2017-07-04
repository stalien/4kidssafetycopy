package org.erlymon.core.presenter;

import org.erlymon.core.model.Model;
import org.erlymon.core.model.ModelImpl;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 1/19/17.
 */

public abstract class BasePresenter implements Presenter {

    protected Model model = new ModelImpl(null);
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    protected void addSubscription(Subscription subscription) {
        compositeSubscription.add(subscription);
    }

    @Override
    public void onStop() {
        compositeSubscription.clear();
    }
}