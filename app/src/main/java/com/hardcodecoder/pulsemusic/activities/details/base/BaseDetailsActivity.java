package com.hardcodecoder.pulsemusic.activities.details.base;

import android.media.session.MediaController;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.main.base.MediaSessionActivity;
import com.hardcodecoder.pulsemusic.views.MarqueeTitleToolbar;

public abstract class BaseDetailsActivity extends MediaSessionActivity {

    public static final String KEY_TRANSITION_NAME = "Transition";
    private int mCurrentSortOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
        mCurrentSortOrder = getSortOrder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(getMenuId(), menu);
        return true;
    }

    protected void setUpToolbar(@NonNull MarqueeTitleToolbar toolbar, @NonNull String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar.getToolbar());
        toolbar.setNavigationOnClickListener(v -> finishAfterTransition());
    }

    protected void setUpTransitions() {
        Fade enterFade = new Fade();
        enterFade.excludeTarget(android.R.id.statusBarBackground, true);
        enterFade.excludeTarget(android.R.id.navigationBarBackground, true);
        enterFade.excludeTarget(R.id.stub_details_activity_rv, true);
        enterFade.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        getWindow().setEnterTransition(enterFade);
    }

    protected int getCurrentSortOrder() {
        return mCurrentSortOrder;
    }

    protected void onChangeSortOrder(int newSortOrder) {
        if (mCurrentSortOrder != newSortOrder) {
            mCurrentSortOrder = newSortOrder;
            onSortOrderChanged(newSortOrder);
        }
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }

    public abstract int getSortOrder();

    @MenuRes
    public abstract int getMenuId();

    public abstract void onSortOrderChanged(int newSortOrder);
}