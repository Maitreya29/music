package com.hardcodecoder.pulsemusic.dialog;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.base.RoundedCustomBottomSheetFragment;
import com.hardcodecoder.pulsemusic.fragments.main.base.PulseFragment.OptionsMenuListener;
import com.hardcodecoder.pulsemusic.model.MenuCategory;
import com.hardcodecoder.pulsemusic.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuDetailsDialog extends RoundedCustomBottomSheetFragment {

    public static final String TAG = MenuDetailsDialog.class.getSimpleName();
    private final List<MenuCategory> mCategoryList = new ArrayList<>();
    private final OptionsMenuListener mListener;
    private final String mDialogTitle;
    private final int mGroupId;
    private int mOrientation;

    public MenuDetailsDialog(@NonNull String dialogTitle, int groupId, @NonNull OptionsMenuListener listener) {
        mDialogTitle = dialogTitle;
        mGroupId = groupId;
        mListener = listener;
    }

    public void addCategory(@NonNull String categoryTitle, @NonNull MenuItem[] items) {
        mCategoryList.add(new MenuCategory(categoryTitle, items));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_context_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mOrientation = getResources().getConfiguration().orientation;
        MaterialTextView dialogTitle = view.findViewById(R.id.context_menu_title);
        dialogTitle.setText(mDialogTitle);

        LinearLayout menuRoot = view.findViewById(R.id.context_menu_root);
        final Context context = requireContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        for (MenuCategory menuCategory : mCategoryList) {

            View category = inflater.inflate(R.layout.item_menu_category, menuRoot, false);
            menuRoot.addView(category);

            MaterialTextView categoryTitle = category.findViewById(R.id.menu_category_title);
            categoryTitle.setText(menuCategory.getCategoryTitle());

            for (final MenuItem item : menuCategory.getItems()) {
                View itemView = inflater.inflate(R.layout.item_menu_options, menuRoot, false);
                menuRoot.addView(itemView);

                MaterialTextView textView = itemView.findViewById(R.id.category_menu_item);
                textView.setId(item.getId());
                textView.setText(item.getTitle());
                textView.setOnClickListener(v -> {
                    mListener.onItemSelected(mGroupId, item.getId());
                    dismiss();
                });
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mOrientation != newConfig.orientation) dismiss();
    }
}