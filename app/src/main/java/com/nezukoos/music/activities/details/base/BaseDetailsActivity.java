package com.nezukoos.music.activities.details.base;

import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.nezukoos.music.R;
import com.nezukoos.music.activities.base.ControllerActivity;

public abstract class BaseDetailsActivity extends ControllerActivity {

    public static final String KEY_TRANSITION_NAME = "Transition";
    private int mCurrentSortOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null); // We don't want to restore any fragments/ dialogs/ bottom sheet
        supportPostponeEnterTransition();
        setContentView(R.layout.activity_details);
        setUpTransitions();
        onViewCreated();
    }

    protected int getCurrentSortOrder() {
        return mCurrentSortOrder;
    }

    protected void setCurrentSortOrder(int sortOrder) {
        mCurrentSortOrder = sortOrder;
    }

    protected void setUpToolbar(@NonNull String title, @Nullable View.OnClickListener listener) {
        ImageView closeBtn = findViewById(R.id.details_activity_close_btn);
        closeBtn.setOnClickListener(v -> finishAfterTransition());

        MaterialTextView headerTitle = findViewById(R.id.details_activity_title);
        headerTitle.setText(title);
        headerTitle.setSelected(true);

        ImageView optionsBtn = findViewById(R.id.details_activity_options_btn);
        optionsBtn.setOnClickListener(listener);
    }

    protected void setUpTransitions() {
        Fade enterFade = new Fade();
        enterFade.excludeTarget(android.R.id.statusBarBackground, true);
        enterFade.excludeTarget(android.R.id.navigationBarBackground, true);
        enterFade.excludeTarget(R.id.stub_details_activity_rv, true);
        enterFade.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        getWindow().setEnterTransition(enterFade);
    }

    protected void onChangeSortOrder(int newSortOrder) {
        if (mCurrentSortOrder != newSortOrder) {
            mCurrentSortOrder = newSortOrder;
            onSortOrderChanged(newSortOrder);
        }
    }

    public abstract void onViewCreated();

    public abstract void onSortOrderChanged(int newSortOrder);
}