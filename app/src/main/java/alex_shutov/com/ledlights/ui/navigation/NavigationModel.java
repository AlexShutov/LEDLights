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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import alex_shutov.com.ledlights.BuildConfig;
import alex_shutov.com.ledlights.R;
import alex_shutov.com.ledlights.ui.MainActivity;
import alex_shutov.com.ledlights.ui.archframework.Model;
import alex_shutov.com.ledlights.ui.archframework.QueryEnum;
import alex_shutov.com.ledlights.ui.archframework.UserActionEnum;

import static alex_shutov.com.ledlights.ui.navigation.NavigationConfig.*;


/**
 * Determines which items to show in the {@link AppNavigationView}.
 */
public class NavigationModel implements Model<NavigationModel.NavigationQueryEnum,
        NavigationModel.NavigationUserActionEnum> {

    private Context mContext;

    private NavigationItemEnum[] mItems;

    public NavigationModel(Context context) {
        mContext = context;
    }

    public NavigationItemEnum[] getItems() {
        return mItems;
    }

    @Override
    public NavigationQueryEnum[] getQueries() {
        return NavigationQueryEnum.values();
    }

    @Override
    public NavigationUserActionEnum[] getUserActions() {
        return NavigationUserActionEnum.values();
    }

    @Override
    public void deliverUserAction(final NavigationUserActionEnum action,
            @Nullable final Bundle args, final UserActionCallback callback) {
        switch (action) {
            case RELOAD_ITEMS:
                mItems = null;
                populateNavigationItems();
                callback.onModelUpdated(this, action);
                break;
        }
    }

    @Override
    public void requestData(final NavigationQueryEnum query,
            final Model.DataQueryCallback callback) {
        switch (query) {
            case LOAD_ITEMS:
                if (mItems != null) {
                    callback.onModelUpdated(this, query);
                } else {
                    populateNavigationItems();
                    callback.onModelUpdated(this, query);
                }
                break;
        }
    }

    private void populateNavigationItems() {
        boolean debug = BuildConfig.DEBUG;

        NavigationItemEnum[] items = null;

        items = NavigationConfig.NAVIGATION_ITEMS_GENERAL;

        mItems = filterOutItemsDisabledInBuildConfig(items);
    }

    @Override
    public void cleanUp() {
        mContext = null;
    }

    /**
     * List of all possible navigation items.
     */
    public enum NavigationItemEnum {

        MY_SCHEDULE(R.id.myschedule_nav_item, R.string.navdrawer_item_my_schedule,
                R.drawable.ic_navview_schedule, null),
        IO_LIVE(R.id.iolive_nav_item, R.string.navdrawer_item_io_live, R.drawable.ic_navview_live,
                null),

//        EXPLORE(R.id.explore_nav_item, R.string.navdrawer_item_explore,
//                R.drawable.ic_navview_explore, ExploreIOActivity.class, true),
//        MAP(R.id.map_nav_item, R.string.navdrawer_item_map, R.drawable.ic_navview_map, MapActivity.class),
//        VIDEO_LIBRARY(R.id.videos_nav_item, R.string.navdrawer_item_video_library,
//                R.drawable.ic_navview_video_library, VideoLibraryActivity.class),
//        SIGN_IN(R.id.signin_nav_item, R.string.navdrawer_item_sign_in, 0, null),
        SETTINGS(R.id.settings_nav_item, R.string.navdrawer_item_settings, R.drawable.ic_navview_settings,
             MainActivity.class),
//        ABOUT(R.id.about_nav_item, R.string.description_about, R.drawable.ic_about,
//                AboutActivity.class),
//        DEBUG(R.id.debug_nav_item, R.string.navdrawer_item_debug, R.drawable.ic_navview_settings,
//                DebugActivity.class),
//
        INVALID(12, 0, 0, null);

        private int id;

        private int titleResource;

        private int iconResource;

        private Class classToLaunch;

        private boolean finishCurrentActivity;

        NavigationItemEnum(int id, int titleResource, int iconResource, Class classToLaunch) {
            this(id, titleResource, iconResource, classToLaunch, false);
        }

        NavigationItemEnum(int id, int titleResource, int iconResource, Class classToLaunch,
                boolean finishCurrentActivity) {
            this.id = id;
            this.titleResource = titleResource;
            this.iconResource = iconResource;
            this.classToLaunch = classToLaunch;
            this.finishCurrentActivity = finishCurrentActivity;
        }

        public int getId() {
            return id;
        }

        public int getTitleResource() {
            return titleResource;
        }

        public int getIconResource() {
            return iconResource;
        }

        public Class getClassToLaunch() {
            return classToLaunch;
        }

        public boolean finishCurrentActivity() {
            return finishCurrentActivity;
        }

        public static NavigationItemEnum getById(int id) {
            NavigationItemEnum[] values = NavigationItemEnum.values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getId() == id) {
                return values[i];
                }
            }
            return INVALID;
        }
    }

    public enum NavigationQueryEnum implements QueryEnum {
        LOAD_ITEMS(0);

        private int id;

        NavigationQueryEnum(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String[] getProjection() {
            return new String[0];
        }
    }

    public enum NavigationUserActionEnum implements UserActionEnum {
        RELOAD_ITEMS(0);

        private int id;

        NavigationUserActionEnum(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }
}
