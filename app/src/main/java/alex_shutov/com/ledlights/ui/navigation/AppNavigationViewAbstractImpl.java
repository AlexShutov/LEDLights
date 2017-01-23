/*
 * Copyright (c) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package alex_shutov.com.ledlights.ui.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import alex_shutov.com.ledlights.ui.archframework.PresenterImpl;
import alex_shutov.com.ledlights.ui.archframework.UpdatableView;
import alex_shutov.com.ledlights.util.ActivityUtils;

import static alex_shutov.com.ledlights.ui.navigation.NavigationModel.*;


/**
 * This abstract class implements both {@link UpdatableView} and {@link AppNavigationView}, without
 * any specific UI implementation details. This uses the {@link com.google.samples.apps.iosched
 * .archframework} for getting its data and processing user actions. Some methods which are UI
 * specific are left abstract. Extend this class for full navigation functionality.
 */
public abstract class AppNavigationViewAbstractImpl implements
        UpdatableView<NavigationModel, NavigationModel.NavigationQueryEnum,
                NavigationModel.NavigationUserActionEnum>,
        AppNavigationView {

    private UserActionListener mUserActionListener;

    protected Activity mActivity;

    protected NavigationItemEnum mSelfItem;

    @Override
    public void displayData(final NavigationModel model,
            final NavigationQueryEnum query) {
        switch (query) {
            case LOAD_ITEMS:
                displayNavigationItems(model.getItems());
                break;
        }
    }

    @Override
    public void displayErrorMessage(final NavigationQueryEnum query) {
        switch (query) {
            case LOAD_ITEMS:
                // No error message displayed
                break;
        }
    }

    @Override
    public void activityReady(Activity activity,
            NavigationItemEnum self) {
        mActivity = activity;
        mSelfItem = self;

        setUpView();

        NavigationModel model = new NavigationModel(getContext());
        PresenterImpl presenter = new PresenterImpl(model, this,
                NavigationUserActionEnum.values(), NavigationQueryEnum.values());
        presenter.loadInitialQueries();
        addListener(presenter);
    }

    @Override
    public void updateNavigationItems() {
        mUserActionListener.onUserAction(NavigationUserActionEnum.RELOAD_ITEMS, null);
    }

    @Override
    public abstract void displayNavigationItems(final NavigationItemEnum[] items);

    @Override
    public abstract void setUpView();

    @Override
    public abstract void showNavigation();

    @Override
    public void itemSelected(final NavigationItemEnum item) {
        switch (item) {

            default:
                if (item.getClassToLaunch() != null) {
                    ActivityUtils.createBackStack(mActivity,
                            new Intent(mActivity, item.getClassToLaunch()));
                    if (item.finishCurrentActivity()) {
                        mActivity.finish();
                    }
                }
                break;
        }
    }

    @Override
    public void displayUserActionResult(final NavigationModel model,
            final NavigationUserActionEnum userAction, final boolean success) {
        switch (userAction) {
            case RELOAD_ITEMS:
                displayNavigationItems(model.getItems());
                break;
        }
    }

    @Override
    public Uri getDataUri(final NavigationQueryEnum query) {
        // This feature has no Uri
        return null;
    }

    @Override
    public Context getContext() {
        return mActivity;
    }

    @Override
    public void addListener(final UserActionListener listener) {
        mUserActionListener = listener;
    }
}
